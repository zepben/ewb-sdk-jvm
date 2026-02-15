/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.Breaker
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.cim.validateEnum
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.NetworkServiceComparator
import com.zepben.ewb.services.network.NetworkState
import com.zepben.ewb.services.network.translator.networkIdentifiedObject
import com.zepben.ewb.services.network.translator.toPb
import com.zepben.ewb.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.forEachBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.validateFailure
import com.zepben.ewb.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.ewb.streaming.get.testdata.*
import com.zepben.ewb.streaming.get.testservices.TestNetworkConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.nc.*
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import com.zepben.protobuf.nc.NetworkIdentifiedObject as NIO


internal class NetworkConsumerClientTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()

    private val consumerService = TestNetworkConsumerService()

    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(NetworkConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient = spy(NetworkConsumerClient(stub).apply { addErrorHandler(onErrorHandler) })
    private val service = consumerClient.service

    private val serverException = IllegalStateException("custom message")

    @BeforeEach
    internal fun beforeEach() {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(consumerService).build().start())
    }

    @Test
    internal fun `resolving references in future batches does not cause concurrent modifications`() {
        //
        // NOTE: There was a bug that caused a concurrent modification when you had a set of unresolved references that was large enough to require a batch
        //       send, and one of those references was resolved by processing the response of an earlier part of the batch before the later part was sent. This
        //       is a race condition between the processing of the references and the sending of requests.
        //
        val feeder = Feeder(generateId()).also { service.add(it) }

        // We create a set of references that will require a batch send, and one that will be resolved by the first half of the batch.
        (0..1000).onEach { service.resolveOrDeferReference(Resolvers.equipment(feeder), "b$it") }
        service.resolveOrDeferReference(Resolvers.normalHeadTerminal(feeder), "b1-t1")

        // This is a convoluted way of getting the requests to have a delay after sending to allow responses to be processed mid-batch.
        doAnswer { getIdentifiedObjectsInv ->
            @Suppress("UNCHECKED_CAST")
            spy(getIdentifiedObjectsInv.callRealMethod() as StreamObserver<GetIdentifiedObjectsRequest>).also {
                doAnswer { onNextInv ->
                    onNextInv.callRealMethod()
                    // Go to sleep to delay the processing of the next batch. This is done to make sure that the previous responses are processed before the
                    // remainder of the batch is sent to check for concurrent modification.
                    Thread.sleep(100)
                }.`when`(it).onNext(any())
            }
        }.`when`(stub).getIdentifiedObjects(any())

        // Send back the requested equipment, plus the terminals, in order to resolve more references than just those requested.
        consumerService.onGetIdentifiedObjects = spy { request, resp ->
            batchedResponseOf(request.mridsList.flatMap {
                listOf(Breaker(it), Terminal("$it-t1"), Terminal("$it-t1"))
            }).forEach {
                resp.onNext(it)
            }
        }

        consumerClient.resolveReferences(MultiObjectResult(mutableMapOf(feeder.mRID to feeder)))

        onErrorHandler.lastError?.printStackTrace()
        assertThat("Unexpected error: ${onErrorHandler.lastError}", onErrorHandler.count, equalTo(0))
        assertThat(feeder.normalHeadTerminal, notNullValue())
    }

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = NIO.newBuilder()

        forEachBuilder(builder) {
            val mRID = "id" + ++counter
            val response = createResponse(builder, it, mRID)

            consumerService.onGetIdentifiedObjects = spy { request, resp ->
                assertThat(request.mridsList, containsInAnyOrder(mRID))
                resp.onNext(response)
            }

            val result = consumerClient.getIdentifiedObject(mRID)

            response.identifiedObjectsList.forEach { nio ->
                val type = nio.identifiedObjectCase
                if (isSupported(type)) {
                    if (result.wasFailure)
                    /**
                     * If you hit this you've probably got fields in your CIM class that have requirements that are not being met by the default
                     * builder. Fix this in [ConsumerUtils.buildFromBuilder]
                     */
                        throw result.thrown
                    assertThat(result.value.mRID, equalTo(mRID))
                } else {
                    assertThat("getIdentifiedObject should fail for unsupported type ${type.name}", result.wasFailure)
                    assertThat(result.thrown, instanceOf(StatusRuntimeException::class.java))
                    assertThat(result.thrown.cause, instanceOf(UnsupportedOperationException::class.java))
                    assertThat(result.thrown.cause?.message, equalTo("Identified object type $type is not supported by the network service"))
                    assertThat(result.thrown, equalTo(onErrorHandler.lastError))
                }
            }

            verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        }
    }

    @Test
    internal fun `onGetEquipmentForContainers receives options for getEquipmentForContainer`() {
        val mRID = "f001"
        val includeEnergizingContainers = IncludedEnergizingContainers.SUBSTATIONS
        val includeEnergizedContainers = IncludedEnergizedContainers.LV_FEEDERS
        val networkState = NetworkState.CURRENT
        configureGetEquipmentForContainersToValidateParameters(mRID, includeEnergizingContainers, includeEnergizedContainers, networkState)

        consumerClient.getEquipmentForContainer(mRID, includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentForContainer(Feeder(mRID), includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentForContainers(listOf(mRID), includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentForContainers(sequenceOf(mRID), includeEnergizingContainers, includeEnergizedContainers, networkState)

        verify(consumerService.onGetEquipmentForContainers, times(4)).invoke(any(), any())
    }

    @Test
    internal fun `onGetEquipmentForContainers receives options for getEquipmentContainer`() {
        val mRID = "f001"
        val includeEnergizingContainers = IncludedEnergizingContainers.SUBSTATIONS
        val includeEnergizedContainers = IncludedEnergizedContainers.LV_FEEDERS
        val networkState = NetworkState.CURRENT

        val expectedService = FeederNetwork.create()
        configureFeederResponses(expectedService)
        configureGetEquipmentForContainersToValidateParameters(mRID, includeEnergizingContainers, includeEnergizedContainers, networkState)

        consumerClient.getEquipmentContainer<Feeder>(mRID, includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentContainer(mRID, Feeder::class.java, includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentContainers(listOf(mRID), Feeder::class.java, includeEnergizingContainers, includeEnergizedContainers, networkState)
        consumerClient.getEquipmentContainers(sequenceOf(mRID), Feeder::class.java, includeEnergizingContainers, includeEnergizedContainers, networkState)

        verify(consumerService.onGetEquipmentForContainers, times(4)).invoke(any(), any())
    }

    @Test
    internal fun `onGetEquipmentForContainers receives options for getEquipmentForLoop`() {
        val mRID = "BTS-ZEP-BEN-BTS-CBR"
        val networkState = NetworkState.CURRENT

        val expectedService = LoopNetwork.create()
        configureResponses(expectedService)
        configureGetEquipmentForContainersToValidateParameters(
            mRID,
            IncludedEnergizingContainers.NONE,
            IncludedEnergizedContainers.NONE,
            networkState
        )

        consumerClient.getEquipmentForLoop(mRID, networkState)
        consumerClient.getEquipmentForLoop(Loop(mRID), networkState)
        consumerClient.getAllLoops(networkState)

        verify(consumerService.onGetEquipmentForContainers, times(3)).invoke(any(), any())
    }

    @Test
    internal fun `returns error when object is not found`() {
        val mRID = "unknown"
        consumerService.onGetIdentifiedObjects = spy { _, _ -> }

        val result = consumerClient.getIdentifiedObject(mRID)

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        assertThat("getIdentifiedObject should fail for mRID '$mRID', which isn't in the network", result.wasFailure)
        expect { throw result.thrown }
            .toThrow<NoSuchElementException>()
            .withMessage("No object with mRID $mRID could be found.")
    }

    @Test
    internal fun `calls error handler when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        consumerService.onGetIdentifiedObjects = spy { _, _ -> throw serverException }

        val result = consumerClient.getIdentifiedObject(mRID)

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        validateFailure(onErrorHandler, result, serverException)
    }


    @Test
    internal fun `captures unhandled exceptions when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        consumerService.onGetIdentifiedObjects = spy { _, _ -> throw serverException }

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiedObject(mRID)

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        validateFailure(onErrorHandler, result, serverException, expectHandled = false)
    }

    @Test
    internal fun `can get multiple identified objects in single call`() {
        val mRIDs = listOf("id1", "id2", "id3")

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getAcLineSegmentBuilder, mRIDs[0]))
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getAcLineSegmentBuilder, mRIDs[1]))
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getBreakerBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs.asSequence())

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.objects[mRIDs[0]], instanceOf(AcLineSegment::class.java))
        assertThat(result.value.objects[mRIDs[1]], instanceOf(AcLineSegment::class.java))
        assertThat(result.value.objects[mRIDs[2]], instanceOf(Breaker::class.java))

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `calls error handler when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        consumerService.onGetIdentifiedObjects = spy { _, _ -> throw serverException }

        val result = consumerClient.getIdentifiedObjects(mRIDs.asSequence())

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
        validateFailure(onErrorHandler, result, serverException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        consumerService.onGetIdentifiedObjects = spy { _, _ -> throw serverException }

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
        validateFailure(onErrorHandler, result, serverException, expectHandled = false)
    }

    @Test
    internal fun `can get network hierarchy`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse()) }

        val result = consumerClient.getNetworkHierarchy()

        verify(consumerService.onGetNetworkHierarchy).invoke(isA<GetNetworkHierarchyRequest>(), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy())
    }

    internal fun buildHierarchyRequest(
        includeGeographicalRegions: Boolean = false,
        includeSubgeographicalRegions: Boolean = false,
        includeSubstations: Boolean = false,
        includeFeeders: Boolean = false,
        includeCircuits: Boolean = false,
        includeLoops: Boolean = false,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false,
    ): GetNetworkHierarchyRequest = GetNetworkHierarchyRequest.newBuilder().also { req ->
        req.includeGeographicalRegions = includeGeographicalRegions
        req.includeSubgeographicalRegions = includeSubgeographicalRegions
        req.includeSubstations = includeSubstations
        req.includeFeeders = includeFeeders
        req.includeCircuits = includeCircuits
        req.includeLoops = includeLoops
        req.includeLvSubstations = includeLvSubstations
        req.includeLvFeeders = includeLvFeeders
    }.build()

    internal fun callGetHierarchy(
        consumerClient: NetworkConsumerClient,
        includeGeographicalRegions: Boolean = false,
        includeSubgeographicalRegions: Boolean = false,
        includeSubstations: Boolean = false,
        includeFeeders: Boolean = false,
        includeCircuits: Boolean = false,
        includeLoops: Boolean = false,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false
    ): GrpcResult<NetworkHierarchy> =
        consumerClient.getNetworkHierarchy(
            includeGeographicalRegions = includeGeographicalRegions,
            includeSubgeographicalRegions = includeSubgeographicalRegions,
            includeSubstations = includeSubstations,
            includeFeeders = includeFeeders,
            includeCircuits = includeCircuits,
            includeLoops = includeLoops,
            includeLvSubstations = includeLvSubstations,
            includeLvFeeders = includeLvFeeders,
        )

    @Test
    internal fun `can optionally retrieve geographical regions`() {
        consumerService.onGetNetworkHierarchy =
            spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeGeographicalRegions = true)) }
        val result = callGetHierarchy(consumerClient, includeGeographicalRegions = true)
        val request = buildHierarchyRequest(includeGeographicalRegions = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeGeographicalRegions = true))
    }

    @Test
    internal fun `can optionally retrieve subgeographical regions`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeSubgeographicalRegions = true)) }
        val result = callGetHierarchy(consumerClient, includeSubgeographicalRegions = true)
        val request = buildHierarchyRequest(includeSubgeographicalRegions = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeSubgeographicalRegions = true))

    }

    @Test
    internal fun `can optionally retrieve substations`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeSubstations = true)) }
        val result = callGetHierarchy(consumerClient, includeSubstations = true)
        val request = buildHierarchyRequest(includeSubstations = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeSubstations = true))

    }

    @Test
    internal fun `can optionally retrieve feeders`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeFeeders = true)) }
        val result = callGetHierarchy(consumerClient, includeFeeders = true)
        val request = buildHierarchyRequest(includeFeeders = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeFeeders = true))
    }

    @Test
    internal fun `can optionally retrieve circuits`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeCircuits = true)) }
        val result = callGetHierarchy(consumerClient, includeCircuits = true)
        val request = buildHierarchyRequest(includeCircuits = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeCircuits = true))

    }

    @Test
    internal fun `can optionally retrieve loops`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeLoops = true)) }
        val result = callGetHierarchy(consumerClient, includeLoops = true)
        val request = buildHierarchyRequest(includeLoops = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeLoops = true))

    }

    @Test
    internal fun `can optionally retrieve lv substations`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeLvSubstations = true)) }
        val result = callGetHierarchy(consumerClient, includeLvSubstations = true)
        val request = buildHierarchyRequest(includeLvSubstations = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeLvSubstations = true))

    }

    @Test
    internal fun `can optionally retrieve lv feeders`() {
        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(includeLvFeeders = true)) }
        val result = callGetHierarchy(consumerClient, includeLvFeeders = true)
        val request = buildHierarchyRequest(includeLvFeeders = true)
        verify(consumerService.onGetNetworkHierarchy).invoke(eq(request), any())
        assertThat("getNetworkHierarchy should succeed", result.wasSuccessful)
        validateNetworkHierarchy(result.value, NetworkHierarchyAllTypes.createNetworkHierarchy(includeLvFeeders = true))
    }

    @Test
    internal fun `runGetMetadata calls stub with arguments it's passed`() {
        val request = GetMetadataRequest.newBuilder().build()
        val streamObserver = AwaitableStreamObserver<GetMetadataResponse> {}
        doNothing().`when`(stub).getMetadata(request, streamObserver)

        consumerClient.runGetMetadata(request, streamObserver)

        verify(stub).getMetadata(request, streamObserver)
    }

    @Test
    internal fun `calls error handler when getting the metadata throws`() {
        consumerService.onGetMetadataRequest = spy { _, _ -> throw serverException }

        val result = consumerClient.getMetadata()

        verify(consumerService.onGetMetadataRequest).invoke(eq(GetMetadataRequest.newBuilder().build()), any())
        validateFailure(onErrorHandler, result, serverException)
    }

    @Test
    internal fun `calls error handler when getting the network hierarchy throws`() {
        consumerService.onGetNetworkHierarchy = spy { _, _ -> throw serverException }

        val result = consumerClient.getNetworkHierarchy()

        verify(consumerService.onGetNetworkHierarchy).invoke(isA<GetNetworkHierarchyRequest>(), any())
        validateFailure(onErrorHandler, result, serverException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting the network hierarchy throws`() {
        consumerService.onGetNetworkHierarchy = spy { _, _ -> throw serverException }

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getNetworkHierarchy()

        verify(consumerService.onGetNetworkHierarchy).invoke(isA<GetNetworkHierarchyRequest>(), any())
        validateFailure(onErrorHandler, result, serverException, expectHandled = false)
    }

    @Test
    internal fun `can get feeder`() {
        val expectedService = FeederNetwork.create()
        configureFeederResponses(expectedService)

        val mRID = "f001"
        val result = consumerClient.getEquipmentContainer<Feeder>(mRID)

        verify(consumerService.onGetNetworkHierarchy, times(1)).invoke(any(), any())
        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(3)).invoke(any(), any())
        verifyNoMoreInteractions(consumerService.onGetNetworkHierarchy)

        assertThat("getEquipmentContainer should succeed for in-network feeder $mRID", result.wasSuccessful)
        assertThat("getEquipmentContainer result should contain the fetched container's mRID", result.value.objects.containsKey(mRID))
        assertThat(result.value.objects, aMapWithSize(20))

        result.value.objects.values.forEach { assertThat(service[it.mRID], equalTo(it)) }
        service.sequenceOf<IdentifiedObject>().forEach {
            // Hierarchy objects are not added to the result collection.
            if ((it.mRID != mRID) && ((it is GeographicalRegion) || (it is SubGeographicalRegion) || (it is Substation) || (it is Feeder) || (it is Circuit) || (it is Loop)))
                assertThat(result.value.objects[it.mRID], nullValue())
            else
                assertThat(result.value.objects[it.mRID], equalTo(it))
        }
        assertThat(result.value.failed, empty())

        val actualFeeder: Feeder = service[mRID]!!
        val expectedFeeder: Feeder = expectedService[mRID]!!

        assertThat(NetworkServiceComparator().compare(actualFeeder, expectedFeeder).differences, anEmptyMap())
    }

    @Test
    internal fun `handles missing feeder`() {
        val expectedService = FeederNetwork.create()
        configureFeederResponses(expectedService)

        val result = consumerClient.getEquipmentContainer<Feeder>("f002")

        verify(consumerService.onGetIdentifiedObjects, times(1)).invoke(any(), any())

        assertThat("getEquipmentContainer should fail for mRID f002, which isn't in the customer service", !result.wasSuccessful)
        expect { throw result.thrown }
            .toThrow<NoSuchElementException>()
            .withMessage("No object with mRID f002 could be found.")
    }

    @Test
    internal fun `calls error handler when getting the feeder throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Feeder::class.java)!!

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetIdentifiedObjects, times(1)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting the feeder throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Feeder::class.java)!!

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetIdentifiedObjects, times(1)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException, expectHandled = false)
    }

    @Test
    internal fun `calls error handler when getting the feeder equipment throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Equipment::class.java)!!

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(1)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting the feeder equipment throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Equipment::class.java)!!

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(1)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException, expectHandled = false)
    }

    @Test
    internal fun `calls error handler when getting the feeder terminal throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Terminal::class.java)!!

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(2)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting the feeder terminal throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, Terminal::class.java)!!

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(2)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException, expectHandled = false)
    }

    @Test
    internal fun `calls error handler when getting the feeder equipment connectivity throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, ConnectivityNode::class.java)!!

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetIdentifiedObjects, times(3)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting the feeder equipment connectivity throws`() {
        val expectedService = FeederNetwork.create()
        val expectedException = configureFeederResponses(expectedService, ConnectivityNode::class.java)!!

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getEquipmentContainer<Feeder>("f001")

        verify(consumerService.onGetIdentifiedObjects, times(3)).invoke(any(), any())
        validateFailure(onErrorHandler, result, expectedException, expectHandled = false)
    }

    @Test
    internal fun `get equipment containers sequence variant coverage`() {
        val expectedResult = mock<GrpcResult<MultiObjectResult>>()
        doReturn(expectedResult).`when`(consumerClient).getEquipmentContainers(any<Sequence<String>>(), any(), any(), any(), any())

        assertThat(consumerClient.getEquipmentContainers(sequenceOf("f001")), equalTo(expectedResult))

        verify(consumerClient).getEquipmentContainers(any<Sequence<String>>(), any(), any(), any(), any())
    }

    @Test
    internal fun `getIdentifiedObjects returns failed mRID when an mRID is not found`() {
        val mRIDs = listOf("id1", "id2")

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getAcLineSegmentBuilder, mRIDs[0]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(result.value.objects["id1"], instanceOf(AcLineSegment::class.java))
        assertThat(result.value.failed, containsInAnyOrder(mRIDs[1]))

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `getIdentifiedObjects returns map containing existing entries in the service`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val acls = AcLineSegment(mRIDs[0])
        service.add(acls)

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getAcLineSegmentBuilder, mRIDs[0]))
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getAcLineSegmentBuilder, mRIDs[1]))
            response.onNext(createResponse(NIO.newBuilder(), NIO.Builder::getBreakerBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        assertThat(result.value.objects, hasEntry("id1", acls))
        assertThat(result.value.objects, hasKey("id2"))
        assertThat(result.value.objects, hasKey("id3"))
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.failed, empty())
    }

    @Test
    internal fun `getEquipmentForContainer returns equipment for a given container`() {
        val expectedService = FeederNetwork.create()
        configureFeederResponses(expectedService)

        val result = consumerClient.getEquipmentForContainer("f001")

        assertThat(result.value.objects, aMapWithSize(service.num<Equipment>()))
        assertThat(service.num<Equipment>(), equalTo(3))
        assertThat(service.listOf<IdentifiedObject>().map { it.mRID }, containsInAnyOrder("fsp", "c2", "tx"))
    }

    @Test
    internal fun `getEquipmentForRestriction returns equipment for a given OperationalRestriction`() {
        val expectedService = OperationalRestrictionTestNetworks.create()
        configureResponses(expectedService)

        val result = consumerClient.getEquipmentForRestriction("or1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(service.num<Equipment>()))
        assertThat(service.num<Equipment>(), equalTo(3))
        assertThat(service.listOf<IdentifiedObject>().map { it.mRID }, containsInAnyOrder("fsp", "c2", "tx"))
    }

    @Test
    internal fun `getTerminalsForNode returns terminals for a given ConnectivityNode`() {
        val expectedService = ConnectivityNodeNetworks.createSimpleConnectivityNode()
        configureResponses(expectedService)

        val result = consumerClient.getTerminalsForConnectivityNode("cn1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(service.num<Terminal>()))
        assertThat(service.num<Terminal>(), equalTo(3))
        expectedService.get<ConnectivityNode>("cn1")!!.terminals.forEach {
            assertThat(service[it.mRID], notNullValue())
        }
    }

    @Test
    internal fun `direct object variant coverage`() {
        val expectedResult = mock<GrpcResult<MultiObjectResult>>()

        val feeder = Feeder(generateId())
        val operationalRestriction = OperationalRestriction(generateId())
        val connectivityNode = ConnectivityNode(generateId())

        doReturn(expectedResult).`when`(consumerClient).getEquipmentForContainer(eq(feeder.mRID), any(), any(), any())
        doReturn(expectedResult).`when`(consumerClient).getEquipmentContainer(eq(feeder.mRID), any(), any(), any(), any())
        doReturn(expectedResult).`when`(consumerClient).getEquipmentForRestriction(eq(operationalRestriction.mRID))
        doReturn(expectedResult).`when`(consumerClient).getTerminalsForConnectivityNode(eq(connectivityNode.mRID))

        assertThat(consumerClient.getEquipmentForContainer(feeder), equalTo(expectedResult))
        assertThat(consumerClient.getEquipmentContainer(feeder.mRID), equalTo(expectedResult))
        assertThat(consumerClient.getEquipmentForRestriction(operationalRestriction), equalTo(expectedResult))
        assertThat(consumerClient.getTerminalsForConnectivityNode(connectivityNode), equalTo(expectedResult))
    }

    @Test
    internal fun `iterable mrids variant coverage`() {
        val result1 = mock<GrpcResult<MultiObjectResult>>()
        val result2 = mock<GrpcResult<MultiObjectResult>>()

        doReturn(result1).`when`(consumerClient).getEquipmentContainers(any<Sequence<String>>(), any(), any(), any(), any())
        doReturn(result2).`when`(consumerClient).getEquipmentForContainers(any<Sequence<String>>(), any(), any(), any())

        assertThat(consumerClient.getEquipmentContainers(listOf("id")), equalTo(result1))
        assertThat(consumerClient.getEquipmentForContainers(listOf("id")), equalTo(result2))

        verify(consumerClient).getEquipmentContainers(any<Sequence<String>>(), any(), any(), any(), any())
        verify(consumerClient).getEquipmentForContainers(any<Sequence<String>>(), any(), any(), any())
    }

    @Test
    internal fun `can get a loop`() {
        val loopNetwork = LoopNetwork.create()
        configureResponses(loopNetwork)

        val loop = "BTS-ZEP-BEN-BTS-CBR"
        val loopContainers = listOf("BTS", "ZEP", "BEN", "CBR", "BTSZEP", "ZEPBENCBR", "BTSBEN")
        val hierarchyObjs = listOf(
            "TG", "ZTS", "ACT",
            "TGZTS", "TGBTS", "ZTSBTS", "BTSACT", "ZTSACT",
            "TG-ZTS-BTS-TG", "ZTS-ACT-BTS",
            "ZEP001", "BEN001", "CBR001", "ACT001"
        )
        val containerEquip = listOf(
            "BTS-j-132000", "BTS-j-66000", "ZEP-j-66000", "ZEP-j-11000", "BEN-j-66000", "BEN-j-11000", "CBR-j-66000", "CBR-j-11000",
            "BTSZEP-j", "ZEPBENCBR-j", "BTSBEN-j"
        )
        val assocObjs = containerEquip.map { "$it-t" } + listOf("bv132", "bv66", "bv11")

        val mor = consumerClient.getEquipmentForLoop(loop).throwOnError().value
        assertThat(service.num<IdentifiedObject>(), equalTo((listOf(loop) + loopContainers + hierarchyObjs + containerEquip + assocObjs).size))
        assertThat(mor.objects, aMapWithSize((listOf(loop) + loopContainers + containerEquip + assocObjs).size))
    }

    @Test
    internal fun `can get loop by cim object`() {
        val loopNetwork = LoopNetwork.create()
        configureResponses(loopNetwork)

        val loop = Loop("BTS-ZEP-BEN-BTS-CBR")
        val loopContainers = listOf("BTS", "ZEP", "BEN", "CBR", "BTSZEP", "ZEPBENCBR", "BTSBEN")
        val hierarchyObjs = listOf(
            "TG", "ZTS", "ACT",
            "TGZTS", "TGBTS", "ZTSBTS", "BTSACT", "ZTSACT",
            "TG-ZTS-BTS-TG", "ZTS-ACT-BTS",
            "ZEP001", "BEN001", "CBR001", "ACT001"
        )
        val containerEquip = listOf(
            "BTS-j-132000", "BTS-j-66000", "ZEP-j-66000", "ZEP-j-11000", "BEN-j-66000", "BEN-j-11000", "CBR-j-66000", "CBR-j-11000",
            "BTSZEP-j", "ZEPBENCBR-j", "BTSBEN-j"
        )
        val assocObjs = containerEquip.map { "$it-t" } + listOf("bv132", "bv66", "bv11")

        val mor = consumerClient.getEquipmentForLoop(loop).throwOnError().value
        assertThat(service.num<IdentifiedObject>(), equalTo((listOf(loop.mRID) + loopContainers + hierarchyObjs + containerEquip + assocObjs).size))
        assertThat(mor.objects, aMapWithSize((listOf(loop.mRID) + loopContainers + containerEquip + assocObjs).size))
    }

    @Test
    internal fun `can get all loops`() {
        val expectedService = LoopNetwork.create()
        configureResponses(expectedService)

        val expectedContainers = listOf(
            "TG", "ZTS", "BTS", "ZEP", "BEN", "CBR", "ACT",
            "TGZTS", "TGBTS", "ZTSBTS", "BTSZEP", "BTSBEN", "ZEPBENCBR", "BTSACT", "ZTSACT"
        )

        val result = consumerClient.getAllLoops()
        assertThat("getAllLoops should succeed", result.wasSuccessful)
        assertThat(result.value.failed, empty())

        val equipmentContainersRequestCaptor = argumentCaptor<GetEquipmentForContainersRequest>()

        verify(consumerService.onGetNetworkHierarchy).invoke(any(), any())
        verify(consumerService.onGetEquipmentForContainers).invoke(equipmentContainersRequestCaptor.capture(), any())

        assertThat(equipmentContainersRequestCaptor.allValues.flatMap { it.mridsList }, containsInAnyOrder(*expectedContainers.toTypedArray()))
    }

    @Test
    internal fun `get equipment container validates type`() {
        val expectedService = FeederNetwork.create()
        configureFeederResponses(expectedService)

        val result = consumerClient.getEquipmentContainer<Circuit>("f001")

        assertThat("getEquipmentContainer should fail if container with matching mRID isn't of the specified type", !result.wasSuccessful)
        expect { throw result.thrown }
            .toThrow<ClassCastException>()
            .withMessage("Unable to extract Circuit networks from [${expectedService.get<Feeder>("f001")?.typeNameAndMRID()}].")
    }

    @Test
    internal fun `generic get equipment container calls java interop`() {
        val result = mock<GrpcResult<MultiObjectResult>>()

        doReturn(result).`when`(consumerClient).getEquipmentContainer(any(), any(), any(), any(), any())

        assertThat(
            consumerClient.getEquipmentContainer<Feeder>(
                "fdr",
                IncludedEnergizingContainers.SUBSTATIONS,
                IncludedEnergizedContainers.LV_FEEDERS,
                NetworkState.ALL
            ),
            equalTo(result)
        )

        verify(consumerClient).getEquipmentContainer(
            "fdr",
            Feeder::class.java,
            IncludedEnergizingContainers.SUBSTATIONS,
            IncludedEnergizedContainers.LV_FEEDERS,
            NetworkState.ALL
        )
    }

    @Test
    internal fun `resolve references skips resolvers referencing equipment containers`() {
        val expectedService = LvFeedersWithOpenPoint.create()
        configureFeederResponses(expectedService)

        val mRID = "lvf5"
        val result = consumerClient.getEquipmentContainer<LvFeeder>(mRID).throwOnError()

        verify(consumerService.onGetNetworkHierarchy, times(1)).invoke(any(), any())
        verify(consumerService.onGetEquipmentForContainers, times(1)).invoke(any(), any())
        verify(consumerService.onGetIdentifiedObjects, times(3)).invoke(any(), any())
        verifyNoMoreInteractions(consumerService.onGetNetworkHierarchy)

        assertThat("getEquipmentContainer should succeed for in-network LV feeder $mRID", result.wasSuccessful)
        assertThat(
            result.value.objects.keys,
            containsInAnyOrder(
                "lvf5",
                "tx0",
                "c1",
                "b2",
                "tx0-t2",
                "tx0-e1",
                "tx0-e2",
                "tx0-t1",
                "c1-t1",
                "c1-t2",
                "b2-t1",
                "b2-t2",
                "lvf6",
                "generated_cn_0",
                "generated_cn_1",
                "generated_cn_2",
            )
        )
        assertThat(consumerClient.service.get<PowerTransformer>("tx4"), nullValue())
        assertThat(consumerClient.service.get<PowerTransformer>("tx0"), equalTo(result.value.objects["tx0"]))
    }

    private fun createResponse(
        identifiedObjectBuilder: NIO.Builder,
        subClassBuilder: (NIO.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse =
        createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)

    private fun createResponse(
        identifiedObjectBuilder: NIO.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        logger.info("$identifiedObjectBuilder")

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()

        responseBuilder.addIdentifiedObjects(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: NIO.IdentifiedObjectCase): Boolean =
        type != NIO.IdentifiedObjectCase.OTHER

    private fun validateNetworkHierarchy(actual: NetworkHierarchy, expected: NetworkHierarchy) {
        validateMap(actual.geographicalRegions, expected.geographicalRegions)
        validateMap(actual.subGeographicalRegions, expected.subGeographicalRegions)
        validateMap(actual.substations, expected.substations)
        validateMap(actual.feeders, expected.feeders)
        validateMap(actual.circuits, expected.circuits)
        validateMap(actual.loops, expected.loops)
    }

    private fun <T : IdentifiedObject> validateMap(actualMap: Map<String, T>, expectedMap: Map<String, T>) {
        assertThat(actualMap, aMapWithSize(expectedMap.size))

        actualMap.forEach { (mRID, it) ->
            val expected = expectedMap[mRID]
            assertThat(expected, notNullValue())
            val comparison = NetworkServiceComparator().compare(it, expected!!)
            assertThat("Differences comparing network hierarchy: ${comparison.differences}", comparison.differences, anEmptyMap())
        }
    }

    private fun configureFeederResponses(expectedService: NetworkService, invalidObject: Class<out IdentifiedObject>? = null): Throwable? {
        val expectedException = createException(invalidObject)

        consumerService.onGetNetworkHierarchy = spy { _, response -> response.onNext(NetworkHierarchyAllTypes.createResponse(true, true, true, true, true, true, true, true)) }

        consumerService.onGetIdentifiedObjects = spy { request, response ->
            val objects = mutableListOf<IdentifiedObject>()
            request.mridsList.forEach { mRID ->
                expectedService.get<IdentifiedObject>(mRID)?.let { identifiedObject ->
                    if (identifiedObject::class.java == invalidObject)
                        throw expectedException!!
                    else
                        objects.add(identifiedObject)
                }
            }
            responseOf(objects).forEach { response.onNext(it) }
        }

        consumerService.onGetEquipmentForContainers = spy { request, response ->
            if (invalidObject == Equipment::class.java)
                throw expectedException!!

            containerEquipmentResponseOf(request.mridsList.flatMap { expectedService.get<EquipmentContainer>(it)!!.equipment }.distinct().toList())
                .forEach { response.onNext(it) }
        }

        return expectedException
    }

    private fun createException(invalidObject: Class<out IdentifiedObject>? = null): Throwable? = when {
        invalidObject != null -> Exception("invalid ${invalidObject.simpleName}")
        else -> null
    }

    private fun configureResponses(expectedService: NetworkService) {
        consumerService.onGetEquipmentForRestriction = spy { request, response ->
            restrictionEquipmentResponseOf(expectedService.get<OperationalRestriction>(request.mrid)!!.equipment.toList()).forEach { response.onNext(it) }
        }

        consumerService.onGetTerminalsForNode = spy { request, response ->
            nodeTerminalResponseOf(expectedService.get<ConnectivityNode>(request.mrid)!!.terminals.toList()).forEach { response.onNext((it)) }
        }

        consumerService.onGetIdentifiedObjects = spy { request, response ->
            responseOf(request.mridsList.map { expectedService[it]!! }).forEach { response.onNext((it)) }
        }

        consumerService.onGetEquipmentForContainers = spy { request, response ->
            containerEquipmentResponseOf(request.mridsList.flatMap { expectedService.get<EquipmentContainer>(it)!!.equipment }.distinct().toList())
                .forEach { response.onNext((it)) }
        }

        consumerService.onGetNetworkHierarchy = spy { _, response ->
            response.onNext(
                networkHierarchyResponseOf(
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                    expectedService.listOf(),
                )
            )
        }
    }


    private fun responseOf(objects: List<IdentifiedObject>): MutableIterator<GetIdentifiedObjectsResponse> {
        val responses = mutableListOf<GetIdentifiedObjectsResponse>()
        objects.forEach {
            responses.add(GetIdentifiedObjectsResponse.newBuilder().apply { addIdentifiedObjects(networkIdentifiedObject(it)) }.build())
        }
        return responses.iterator()
    }

    private fun batchedResponseOf(objects: List<IdentifiedObject>): MutableIterator<GetIdentifiedObjectsResponse> {
        assertThat(objects, not(empty()))
        val responses = mutableListOf<GetIdentifiedObjectsResponse>()

        var builder = GetIdentifiedObjectsResponse.newBuilder()
        objects.forEachIndexed { index, obj ->
            if ((index % 1000 == 0) && (builder.identifiedObjectsCount > 0)) {
                responses.add(builder.build())
                builder = GetIdentifiedObjectsResponse.newBuilder()
            }

            builder.addIdentifiedObjects(networkIdentifiedObject(obj))
        }
        responses.add(builder.build())

        return responses.iterator()
    }

    private fun restrictionEquipmentResponseOf(objects: List<IdentifiedObject>): MutableIterator<GetEquipmentForRestrictionResponse> {
        val responses = mutableListOf<GetEquipmentForRestrictionResponse>()
        objects.forEach {
            responses.add(GetEquipmentForRestrictionResponse.newBuilder().apply { addIdentifiedObjects(networkIdentifiedObject(it)) }.build())
        }
        return responses.iterator()
    }

    private fun containerEquipmentResponseOf(objects: List<IdentifiedObject>): MutableIterator<GetEquipmentForContainersResponse> {
        val responses = mutableListOf<GetEquipmentForContainersResponse>()
        objects.forEach {
            responses.add(GetEquipmentForContainersResponse.newBuilder().apply { addIdentifiedObjects(networkIdentifiedObject(it)) }.build())
        }
        return responses.iterator()
    }

    private fun nodeTerminalResponseOf(objects: List<Terminal>): MutableIterator<GetTerminalsForNodeResponse> {
        val responses = mutableListOf<GetTerminalsForNodeResponse>()
        objects.forEach {
            responses.add(GetTerminalsForNodeResponse.newBuilder().setTerminal(it.toPb()).build())
        }
        return responses.iterator()
    }

    private fun networkHierarchyResponseOf(
        geographicalRegions: List<GeographicalRegion>,
        subGeographicalRegions: List<SubGeographicalRegion>,
        substations: List<Substation>,
        feeders: List<Feeder>,
        circuits: List<Circuit>,
        loops: List<Loop>,
        lvSubstations: List<LvSubstation>,
        lvFeeders: List<LvFeeder>
    ): GetNetworkHierarchyResponse = GetNetworkHierarchyResponse.newBuilder()
        .addAllGeographicalRegions(geographicalRegions.map { it.toPb() })
        .addAllSubGeographicalRegions(subGeographicalRegions.map { it.toPb() })
        .addAllSubstations(substations.map { it.toPb() })
        .addAllFeeders(feeders.map { it.toPb() })
        .addAllCircuits(circuits.map { it.toPb() })
        .addAllLoops(loops.map { it.toPb() })
        .addAllLvSubstations(lvSubstations.map { it.toPb() })
        .addAllLvFeeders(lvFeeders.map { it.toPb() })
        .build()

    private fun validateFeederNetwork(actual: NetworkService?, expectedService: NetworkService) {
        assertThat(actual, notNullValue())
        val differences = NetworkServiceComparator().compare(actual!!, expectedService)

        logger.info("$differences")

        assertThat("missing from source", differences.missingFromSource(), empty())
        assertThat("missing from target", differences.missingFromTarget(), empty())
        assertThat("has differences", differences.modifications().entries, empty())
    }

    private fun configureGetEquipmentForContainersToValidateParameters(
        mRID: String,
        includeEnergizingContainers: IncludedEnergizingContainers,
        includeEnergizedContainers: IncludedEnergizedContainers,
        networkState: NetworkState
    ) {
        consumerService.onGetEquipmentForContainers = spy { request, _ ->
            assertThat(request.mridsList, containsInAnyOrder(mRID))
            assertThat(request.includeEnergizingContainers, equalTo(includeEnergizingContainers))
            assertThat(request.includeEnergizedContainers, equalTo(includeEnergizedContainers))
            assertThat(request.networkState, equalTo(networkState))
        }
    }

    @Test
    internal fun validateVsPb() {
        validateEnum(IncludedEnergizingContainers.entries, com.zepben.protobuf.nc.IncludedEnergizingContainers.entries)
        validateEnum(IncludedEnergizedContainers.entries, com.zepben.protobuf.nc.IncludedEnergizedContainers.entries)
    }

}

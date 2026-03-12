/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.diagram.translator.diagramIdentifiable
import com.zepben.ewb.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.forEachBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.validateFailure
import com.zepben.ewb.streaming.get.testservices.TestDiagramConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.GetDiagramObjectsResponse
import com.zepben.protobuf.dc.GetIdentifiablesRequest
import com.zepben.protobuf.dc.GetIdentifiablesResponse
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Channel
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import com.zepben.protobuf.dc.DiagramIdentifiable as DIO

internal class DiagramConsumerClientTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()

    private val consumerService = TestDiagramConsumerService()

    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(DiagramConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient = spy(DiagramConsumerClient(stub).apply { addErrorHandler(onErrorHandler) })
    private val service = consumerClient.service

    private val serverException = IllegalStateException("custom message")

    @BeforeEach
    internal fun beforeEach() {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(consumerService).build().start())
    }

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = DIO.newBuilder()

        forEachBuilder(builder) {
            val mRID = "id" + ++counter
            val response = createResponse(builder, it, mRID)

            consumerService.onGetIdentifiables = spy { request, resp ->
                assertThat(request.mridsList, containsInAnyOrder(mRID))
                resp.onNext(response)
            }

            val result = consumerClient.getIdentifiable(mRID)

            val type = response.identifiablesList[0].identifiableCase
            if (isSupported(type)) {
                assertThat("getIdentifiable should succeed for supported type ${type.name}", result.wasSuccessful)
                assertThat(result.value.mRID, equalTo(mRID))
            } else {
                assertThat("getIdentifiable should fail for unsupported type ${type.name}", result.wasFailure)
                assertThat(result.thrown, instanceOf(StatusRuntimeException::class.java))
                assertThat(result.thrown.cause, instanceOf(UnsupportedOperationException::class.java))
                assertThat(result.thrown.cause?.message, equalTo("Identified object type $type is not supported by the diagram service"))
                assertThat(result.thrown, equalTo(onErrorHandler.lastError))
            }

            verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addMrids(mRID).build()), any())
        }
    }

    @Test
    internal fun `returns error when object is not found`() {
        val mRID = "unknown"
        consumerService.onGetIdentifiables = spy { _, _ -> }

        val result = consumerClient.getIdentifiable(mRID)

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addMrids(mRID).build()), any())
        assertThat("getIdentifiable should fail for mRID '$mRID', which isn't in the diagram", result.wasFailure)
        ExpectException.expect { throw result.thrown }
            .toThrow<NoSuchElementException>()
            .withMessage("No object with mRID $mRID could be found.")
    }

    @Test
    internal fun `calls error handler when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        consumerService.onGetIdentifiables = spy { _, _ -> throw serverException }

        val result = consumerClient.getIdentifiable(mRID)

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addMrids(mRID).build()), any())
        validateFailure(onErrorHandler, result, serverException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        consumerService.onGetIdentifiables = spy { _, _ -> throw serverException }

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiable(mRID)

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addMrids(mRID).build()), any())
        validateFailure(onErrorHandler, result, serverException, expectHandled = false)
    }

    @Test
    internal fun `can get multiple identified objects in single call`() {
        val mRIDs = listOf("id1", "id2", "id3")

        consumerService.onGetIdentifiables = spy { _, response ->
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[1]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramObjectBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiables(mRIDs.asSequence())

        assertThat("getIdentifiables should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.objects[mRIDs[0]], instanceOf(Diagram::class.java))
        assertThat(result.value.objects[mRIDs[1]], instanceOf(Diagram::class.java))
        assertThat(result.value.objects[mRIDs[2]], instanceOf(DiagramObject::class.java))

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `calls error handler when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        consumerService.onGetIdentifiables = spy { _, _ -> throw serverException }

        val result = consumerClient.getIdentifiables(mRIDs.asSequence())

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addAllMrids(mRIDs).build()), any())
        validateFailure(onErrorHandler, result, serverException)
    }

    @Test
    internal fun `captures unhandled exceptions when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        consumerService.onGetIdentifiables = spy { _, _ -> throw serverException }

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiables(mRIDs)

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addAllMrids(mRIDs).build()), any())
        validateFailure(onErrorHandler, result, serverException, expectHandled = false)
    }

    @Test
    internal fun `getIdentifiables returns failed mRID when an mRID is not found`() {
        val mRIDs = listOf("id1", "id2")

        consumerService.onGetIdentifiables = spy { _, response ->
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
        }

        val result = consumerClient.getIdentifiables(mRIDs)

        assertThat("getIdentifiables should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(result.value.objects["id1"], instanceOf(Diagram::class.java))
        assertThat(result.value.failed, containsInAnyOrder(mRIDs[1]))

        verify(consumerService.onGetIdentifiables).invoke(eq(GetIdentifiablesRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `getIdentifiables returns map containing existing entries in the service`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val diagram = Diagram(mRIDs[0])
        service.add(diagram)

        consumerService.onGetIdentifiables = spy { _, response ->
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[1]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramObjectBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiables(mRIDs)

        assertThat(result.value.objects, hasEntry("id1", diagram))
        assertThat(result.value.objects, hasKey("id2"))
        assertThat(result.value.objects, hasKey("id3"))
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.failed, empty())
    }

    @Test
    internal fun `getDiagramObjects returns objects for a given ID`() {
        val diagramService = DiagramService().apply {
            add(DiagramObject("d1").also { it.identifiedObjectMRID = "io1" })
            add(DiagramObject("d2").also { it.identifiedObjectMRID = "io1" })
            add(DiagramObject("d3").also { it.identifiedObjectMRID = "io2" })
        }

        configureResponses(diagramService)

        val result = consumerClient.getDiagramObjects("io1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(2))
        assertThat(service.listOf<IdentifiedObject>().map { it.mRID }, containsInAnyOrder("d1", "d2"))
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
    internal fun `construct via Channel`() {
        val diagramService = DiagramService().apply {
            add(DiagramObject("d1").also { it.identifiedObjectMRID = "io1" })
        }

        configureResponses(diagramService)

        val channel = mockk<Channel>()
        mockkStatic(DiagramConsumerGrpc::class)
        every { DiagramConsumerGrpc.newStub(channel) } returns stub

        val clientViaChannel = DiagramConsumerClient(channel)
        val result = clientViaChannel.getDiagramObjects("io1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(clientViaChannel.service.listOf<IdentifiedObject>().map { it.mRID }, contains("d1"))
    }

    @Test
    internal fun `construct via GrpcChannel`() {
        val diagramService = DiagramService().apply {
            add(DiagramObject("d1").also { it.identifiedObjectMRID = "io1" })
        }

        configureResponses(diagramService)

        val channel = mockk<Channel>()
        val grpcChannel = GrpcChannel(channel)
        mockkStatic(DiagramConsumerGrpc::class)
        every { DiagramConsumerGrpc.newStub(channel) } returns stub

        val clientViaGrpcChannel = DiagramConsumerClient(grpcChannel)
        val result = clientViaGrpcChannel.getDiagramObjects("io1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(clientViaGrpcChannel.service.listOf<IdentifiedObject>().map { it.mRID }, contains("d1"))
    }

    private fun configureResponses(expectedDiagramService: DiagramService) {
        consumerService.onGetDiagramObjects = spy { request, response ->
            val objects = mutableListOf<DiagramObject>()
            request.mridsList.forEach { mRID ->
                expectedDiagramService.getDiagramObjects(mRID).forEach { diagramObject ->
                    objects.add(diagramObject)
                }
            }
            responseOf(objects).forEach { response.onNext(it) }
        }
    }

    private fun responseOf(objects: List<DiagramObject>): MutableIterator<GetDiagramObjectsResponse> {
        val responses = mutableListOf<GetDiagramObjectsResponse>()
        objects.forEach {
            responses.add(GetDiagramObjectsResponse.newBuilder().apply { addIdentifiables(diagramIdentifiable(it)) }.build())
        }
        return responses.iterator()
    }

    private fun createResponse(
        identifiedObjectBuilder: DIO.Builder,
        subClassBuilder: (DIO.Builder) -> Any,
        mRID: String
    ): GetIdentifiablesResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(
        identifiedObjectBuilder: DIO.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiablesResponse {
        buildFromBuilder(subClassBuilder, mRID)
        logger.info("$identifiedObjectBuilder")

        val responseBuilder = GetIdentifiablesResponse.newBuilder()
        responseBuilder.addIdentifiables(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: DIO.IdentifiableCase): Boolean =
        type != DIO.IdentifiableCase.OTHER

}

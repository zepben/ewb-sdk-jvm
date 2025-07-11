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
import com.zepben.ewb.services.diagram.translator.diagramIdentifiedObject
import com.zepben.ewb.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.forEachBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.validateFailure
import com.zepben.ewb.streaming.get.testservices.TestDiagramConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.GetDiagramObjectsResponse
import com.zepben.protobuf.dc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.dc.GetIdentifiedObjectsResponse
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
import java.util.concurrent.Executors
import com.zepben.protobuf.dc.DiagramIdentifiedObject as DIO

internal class DiagramConsumerClientTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

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

            consumerService.onGetIdentifiedObjects = spy { request, resp ->
                assertThat(request.mridsList, containsInAnyOrder(mRID))
                resp.onNext(response)
            }

            val result = consumerClient.getIdentifiedObject(mRID)

            val type = response.identifiedObjectsList[0].identifiedObjectCase
            if (isSupported(type)) {
                assertThat("getIdentifiedObject should succeed for supported type ${type.name}", result.wasSuccessful)
                assertThat(result.value.mRID, equalTo(mRID))
            } else {
                assertThat("getIdentifiedObject should fail for unsupported type ${type.name}", result.wasFailure)
                assertThat(result.thrown, instanceOf(StatusRuntimeException::class.java))
                assertThat(result.thrown.cause, instanceOf(UnsupportedOperationException::class.java))
                assertThat(result.thrown.cause?.message, equalTo("Identified object type $type is not supported by the diagram service"))
                assertThat(result.thrown, equalTo(onErrorHandler.lastError))
            }

            verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        }
    }

    @Test
    internal fun `returns error when object is not found`() {
        val mRID = "unknown"
        consumerService.onGetIdentifiedObjects = spy { _, _ -> }

        val result = consumerClient.getIdentifiedObject(mRID)

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build()), any())
        assertThat("getIdentifiedObject should fail for mRID '$mRID', which isn't in the diagram", result.wasFailure)
        ExpectException.expect { throw result.thrown }
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
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[1]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramObjectBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs.asSequence())

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.objects[mRIDs[0]], instanceOf(Diagram::class.java))
        assertThat(result.value.objects[mRIDs[1]], instanceOf(Diagram::class.java))
        assertThat(result.value.objects[mRIDs[2]], instanceOf(DiagramObject::class.java))

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
    internal fun `getIdentifiedObjects returns failed mRID when an mRID is not found`() {
        val mRIDs = listOf("id1", "id2")

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(result.value.objects["id1"], instanceOf(Diagram::class.java))
        assertThat(result.value.failed, containsInAnyOrder(mRIDs[1]))

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `getIdentifiedObjects returns map containing existing entries in the service`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val diagram = Diagram(mRIDs[0])
        service.add(diagram)

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[0]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramBuilder, mRIDs[1]))
            response.onNext(createResponse(DIO.newBuilder(), DIO.Builder::getDiagramObjectBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

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
            responses.add(GetDiagramObjectsResponse.newBuilder().apply { addIdentifiedObjects(diagramIdentifiedObject(it)) }.build())
        }
        return responses.iterator()
    }

    private fun createResponse(
        identifiedObjectBuilder: DIO.Builder,
        subClassBuilder: (DIO.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(
        identifiedObjectBuilder: DIO.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        println(identifiedObjectBuilder)

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()
        responseBuilder.addIdentifiedObjects(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: DIO.IdentifiedObjectCase): Boolean =
        type != DIO.IdentifiedObjectCase.OTHER

}

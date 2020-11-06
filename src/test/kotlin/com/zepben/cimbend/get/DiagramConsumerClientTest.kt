/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.get.ConsumerUtils.buildFromBuilder
import com.zepben.cimbend.get.ConsumerUtils.forEachBuilder
import com.zepben.cimbend.get.ConsumerUtils.validateFailure
import com.zepben.cimbend.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.DiagramIdentifiedObject
import com.zepben.protobuf.dc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.dc.GetIdentifiedObjectsResponse
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

internal class DiagramConsumerClientTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val stub = mock(DiagramConsumerGrpc.DiagramConsumerBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient: DiagramConsumerClient =
        DiagramConsumerClient(stub).apply { addErrorHandler(onErrorHandler) }
    private val service: DiagramService = DiagramService()

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = DiagramIdentifiedObject.newBuilder()

        forEachBuilder(builder) {
            val mRID = "id" + ++counter
            val response = createResponse(builder, it, mRID)

            doReturn(listOf(response).iterator()).`when`(stub).getIdentifiedObjects(any())

            val result = consumerClient.getIdentifiedObject(service, mRID)

            val type = response.identifiedObjectsList[0].identifiedObjectCase
            if (isSupported(type)) {
                assertThat(result.wasSuccessful, equalTo(true))
                assertThat(result.value?.mRID, equalTo(mRID))
            } else {
                assertThat(result.wasFailure, equalTo(true))
                assertThat(result.thrown, instanceOf(UnsupportedOperationException::class.java))
                assertThat(
                    result.thrown.message,
                    equalTo("Identified object type $type is not supported by the diagram service")
                )
                assertThat(result.thrown, equalTo(onErrorHandler.lastError))
            }

            verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
            clearInvocations(stub)
        }
    }

    @Test
    internal fun `calls error handler when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObject(service, mRID)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
        validateFailure(onErrorHandler, result, expectedEx, true)
    }

    @Test
    internal fun `captures unhandled exceptions when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiedObject(service, mRID)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
        validateFailure(onErrorHandler, result, expectedEx, false)
    }

    @Test
    internal fun `can get multiple identified objects in single call`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val response1 = createResponse(
            DiagramIdentifiedObject.newBuilder(),
            DiagramIdentifiedObject.Builder::getDiagramBuilder,
            mRIDs[0]
        )
        val response2 = createResponse(
            DiagramIdentifiedObject.newBuilder(),
            DiagramIdentifiedObject.Builder::getDiagramBuilder,
            mRIDs[1]
        )
        val response3 = createResponse(
            DiagramIdentifiedObject.newBuilder(),
            DiagramIdentifiedObject.Builder::getDiagramObjectBuilder,
            mRIDs[2]
        )

        doReturn(listOf(response1, response2, response3).iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.value.size, equalTo(3))
        assertThat(result.value[mRIDs[0]], instanceOf(Diagram::class.java))
        assertThat(result.value[mRIDs[1]], instanceOf(Diagram::class.java))
        assertThat(result.value[mRIDs[2]], instanceOf(DiagramObject::class.java))

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
        clearInvocations(stub)
    }

    @Test
    internal fun `calls error handler when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
        validateFailure(onErrorHandler, result, expectedEx, true)
    }

    @Test
    internal fun `captures unhandled exceptions when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        consumerClient.removeErrorHandler(onErrorHandler)

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
        validateFailure(onErrorHandler, result, expectedEx, false)
    }

    private fun createResponse(
        identifiedObjectBuilder: DiagramIdentifiedObject.Builder,
        subClassBuilder: (DiagramIdentifiedObject.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(
        identifiedObjectBuilder: DiagramIdentifiedObject.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        println(identifiedObjectBuilder)

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()
        responseBuilder.addIdentifiedObjects(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: DiagramIdentifiedObject.IdentifiedObjectCase): Boolean =
        type != DiagramIdentifiedObject.IdentifiedObjectCase.OTHER

}

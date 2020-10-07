/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.wires.AcLineSegment
import com.zepben.cimbend.cim.iec61970.base.wires.Breaker
import com.zepben.cimbend.get.hierarchy.NetworkHierarchy
import com.zepben.cimbend.get.hierarchy.NetworkHierarchyIdentifiedObject
import com.zepben.cimbend.get.testdata.NetworkHierarchyAllTypes
import com.zepben.cimbend.grpc.CaptureLastRpcErrorHandler
import com.zepben.cimbend.network.NetworkService
import com.zepben.protobuf.cim.iec61970.base.wires.TapChanger
import com.zepben.protobuf.nc.*
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import kotlin.reflect.full.declaredMemberFunctions

internal class NetworkConsumerClientTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val stub = mock(NetworkConsumerGrpc.NetworkConsumerBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient: NetworkConsumerClient = NetworkConsumerClient(stub).apply { addErrorHandler(onErrorHandler) }
    private val service: NetworkService = NetworkService()

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = NetworkIdentifiedObject.newBuilder()

        forEachBuilder(builder) {
            val mRID = "id" + ++counter
            val response = createResponse(builder, it, mRID)

            doReturn(listOf(response).iterator()).`when`(stub).getIdentifiedObjects(any())

            val result = consumerClient.getIdentifiedObject(service, mRID)

            val type = response.objectGroup.identifiedObject.identifiedObjectCase
            if (isSupported(type)) {
                assertThat(result.wasSuccessful, equalTo(true))
                assertThat(result.result!!.mRID, equalTo(mRID))
            } else {
                assertThat(result.wasSuccessful, equalTo(false))
                assertThat(result.thrown, instanceOf(UnsupportedOperationException::class.java))
                assertThat(result.thrown!!.message, equalTo("Identified object type $type is not supported by the network service"))
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
        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.thrown, equalTo(expectedEx))
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `throws unhandled exceptions when getting an IdentifiedObject throws`() {
        val mRID = "1234"
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        consumerClient.removeErrorHandler(onErrorHandler)

        expect { consumerClient.getIdentifiedObject(service, mRID) }
            .toThrow(StatusRuntimeException::class.java)
            .withMessage(expectedEx.message!!)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
    }

    @Test
    internal fun `can get multiple identified objects in single call`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val response1 = createResponse(NetworkIdentifiedObject.newBuilder(), NetworkIdentifiedObject.Builder::getAcLineSegmentBuilder, mRIDs[0])
        val response2 = createResponse(NetworkIdentifiedObject.newBuilder(), NetworkIdentifiedObject.Builder::getAcLineSegmentBuilder, mRIDs[1])
        val response3 = createResponse(NetworkIdentifiedObject.newBuilder(), NetworkIdentifiedObject.Builder::getBreakerBuilder, mRIDs[2])

        doReturn(listOf(response1, response2, response3).iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.result!!.size, equalTo(3))
        assertThat(result.result!![mRIDs[0]], instanceOf(AcLineSegment::class.java))
        assertThat(result.result!![mRIDs[1]], instanceOf(AcLineSegment::class.java))
        assertThat(result.result!![mRIDs[2]], instanceOf(Breaker::class.java))

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
        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.thrown, equalTo(expectedEx))
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `throws unhandled exceptions when getting multiple IdentifiedObject throws`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getIdentifiedObjects(any())

        consumerClient.removeErrorHandler(onErrorHandler)

        expect { consumerClient.getIdentifiedObjects(service, mRIDs) }
            .toThrow(StatusRuntimeException::class.java)
            .withMessage(expectedEx.message!!)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
    }

    @Test
    internal fun `can get network hierarchy`() {
        doReturn(NetworkHierarchyAllTypes.createResponse()).`when`(stub).getNetworkHierarchy(any())

        val result = consumerClient.getNetworkHierarchy()

        verify(stub).getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build())
        assertThat(result.wasSuccessful, equalTo(true))
        validateNetworkHierarchy(result.result, NetworkHierarchyAllTypes.createNetworkHierarchy())
    }

    @Test
    internal fun `calls error handler when getting the network hierarchy throws`() {
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getNetworkHierarchy(any())

        val result = consumerClient.getNetworkHierarchy()

        verify(stub).getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build())
        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.thrown, equalTo(expectedEx))
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `throws unhandled exceptions when getting the network hierarchy throws`() {
        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).getNetworkHierarchy(any())

        consumerClient.removeErrorHandler(onErrorHandler)

        expect { consumerClient.getNetworkHierarchy() }
            .toThrow(StatusRuntimeException::class.java)
            .withMessage(expectedEx.message!!)

        verify(stub).getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build())
    }

    private fun forEachBuilder(obj: Any, action: (Any) -> Unit) {
        obj::class.declaredMemberFunctions
            .asSequence()
            .filter { it.parameters.size == 1 }
            .filter { it.name.startsWith("get") }
            .filter { it.name.endsWith("Builder") }
            .filter { !it.name.endsWith("FieldBuilder") }
            .filter { !it.name.endsWith("OrBuilder") || it.name == "getOrBuilder" }
            .mapNotNull { it.call(obj) }
            .forEach(action)
    }

    private fun createResponse(
        identifiedObjectBuilder: NetworkIdentifiedObject.Builder,
        subClassBuilder: (NetworkIdentifiedObject.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(identifiedObjectBuilder: NetworkIdentifiedObject.Builder, subClassBuilder: Any, mRID: String): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        println(identifiedObjectBuilder)

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()
        val objectGroupBuilder = responseBuilder.objectGroupBuilder

        objectGroupBuilder.identifiedObject = identifiedObjectBuilder.build()
        objectGroupBuilder.build()

        return responseBuilder.build()
    }

    private fun buildFromBuilder(builder: Any, mRID: String): Any {
        println("-> ${builder::class.java.enclosingClass.simpleName}.${builder::class.simpleName}")
        builder::class.declaredMemberFunctions.find { it.name == "setMRID" }?.call(builder, mRID)

        // Add any customisations required to build the object at a bare minimum
        if (builder is TapChanger.Builder)
            builder.highStep = 1

        forEachBuilder(builder) { buildFromBuilder(it, mRID) }

        return builder::class.declaredMemberFunctions.single { it.name == "build" }.call(builder)!!
    }

    private fun isSupported(type: NetworkIdentifiedObject.IdentifiedObjectCase): Boolean =
        type != NetworkIdentifiedObject.IdentifiedObjectCase.OTHER

    private fun validateNetworkHierarchy(actual: NetworkHierarchy?, expected: NetworkHierarchy) {
        assertThat(actual, notNullValue())

        validateMap(actual!!.geographicalRegions, expected.geographicalRegions) { it, other ->
            assertThat(it.mRID, equalTo(other.mRID))
            assertThat(it.name, equalTo(other.name))
            assertThat(it.subGeographicalRegions.keys, equalTo(other.subGeographicalRegions.keys))
        }

        validateMap(actual.subGeographicalRegions, expected.subGeographicalRegions) { it, other ->
            assertThat(it.mRID, equalTo(other.mRID))
            assertThat(it.name, equalTo(other.name))
            assertThat(it.geographicalRegion?.mRID, equalTo(other.geographicalRegion?.mRID))
            assertThat(it.substations.keys, equalTo(other.substations.keys))
        }

        validateMap(actual.substations, expected.substations) { it, other ->
            assertThat(it.mRID, equalTo(other.mRID))
            assertThat(it.name, equalTo(other.name))
            assertThat(it.subGeographicalRegion?.mRID, equalTo(other.subGeographicalRegion?.mRID))
            assertThat(it.feeders.keys, equalTo(other.feeders.keys))
        }

        validateMap(actual.feeders, expected.feeders) { it, other ->
            assertThat(it.mRID, equalTo(other.mRID))
            assertThat(it.name, equalTo(other.name))
            assertThat(it.substation?.mRID, equalTo(other.substation?.mRID))
        }
    }

    private fun <T : NetworkHierarchyIdentifiedObject> validateMap(actualMap: Map<String, T>, expectedMap: Map<String, T>, comparator: (T, T) -> Unit) {
        assertThat(actualMap.size, equalTo(expectedMap.size))

        actualMap.forEach { (mRID, it) ->
            val expected = expectedMap[mRID]
            assertThat(expected, notNullValue())

            comparator(it, expected!!)
        }
    }

}

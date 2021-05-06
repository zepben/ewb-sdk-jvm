/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.nhaarman.mockitokotlin2.*
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.evolve.streaming.get.ConsumerUtils.forEachBuilder
import com.zepben.evolve.streaming.get.ConsumerUtils.validateFailure
import com.zepben.evolve.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.cc.GetIdentifiedObjectsResponse
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerConsumerClientTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val stub = mock<CustomerConsumerGrpc.CustomerConsumerBlockingStub>()
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient: CustomerConsumerClient = CustomerConsumerClient(stub).apply { addErrorHandler(onErrorHandler) }
    private val service: CustomerService = CustomerService()

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = CustomerIdentifiedObject.newBuilder()

        forEachBuilder(builder) {
            val mRID = "id" + ++counter
            val response = createResponse(builder, it, mRID)

            doReturn(listOf(response).iterator()).`when`(stub).getIdentifiedObjects(any())

            val result = consumerClient.getIdentifiedObject(service, mRID)

            val type = response.identifiedObjectsList[0].identifiedObjectCase
            if (isSupported(type)) {
                assertThat(result.wasSuccessful, equalTo(true))
                assertThat(result.value.mRID, equalTo(mRID))
            } else {
                assertThat(result.wasFailure, equalTo(true))
                assertThat(result.thrown, instanceOf(UnsupportedOperationException::class.java))
                assertThat(
                    result.thrown.message,
                    equalTo("Identified object type $type is not supported by the customer service")
                )
                assertThat(result.thrown, equalTo(onErrorHandler.lastError))
            }

            verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
            clearInvocations(stub)
        }
    }

    @Test
    internal fun `returns error when object is not found`() {
        val mRID = "unknown"
        doReturn(listOf<GetIdentifiedObjectsResponse>().iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObject(service, mRID)

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
        assertThat(result.wasFailure, equalTo(true))
        expect { throw result.thrown }
            .toThrow(NoSuchElementException::class.java)
            .withMessage("No object with mRID $mRID could be found.")
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
            CustomerIdentifiedObject.newBuilder(),
            CustomerIdentifiedObject.Builder::getCustomerBuilder,
            mRIDs[0]
        )
        val response2 = createResponse(
            CustomerIdentifiedObject.newBuilder(),
            CustomerIdentifiedObject.Builder::getCustomerBuilder,
            mRIDs[1]
        )
        val response3 = createResponse(
            CustomerIdentifiedObject.newBuilder(),
            CustomerIdentifiedObject.Builder::getCustomerAgreementBuilder,
            mRIDs[2]
        )

        doReturn(listOf(response1, response2, response3).iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.value.objects.size, equalTo(3))
        assertThat(result.value.objects[mRIDs[0]], instanceOf(Customer::class.java))
        assertThat(result.value.objects[mRIDs[1]], instanceOf(Customer::class.java))
        assertThat(result.value.objects[mRIDs[2]], instanceOf(CustomerAgreement::class.java))

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

    @Test
    internal fun `getIdentifiedObjects returns failed mRID when an mRID is not found`() {
        val mRIDs = listOf("id1", "id2")
        val response = createResponse(CustomerIdentifiedObject.newBuilder(), CustomerIdentifiedObject.Builder::getCustomerBuilder, mRIDs[0])

        doReturn(listOf(response).iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.value.objects.size, equalTo(1))
        assertThat(result.value.objects["id1"], instanceOf(Customer::class.java))
        assertThat(result.value.failed, containsInAnyOrder(mRIDs[1]))

        verify(stub).getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
        clearInvocations(stub)
    }

    @Test
    internal fun `getIdentifiedObjects returns map containing existing entries in the service`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val response2 = createResponse(CustomerIdentifiedObject.newBuilder(), CustomerIdentifiedObject.Builder::getCustomerBuilder, mRIDs[1])
        val response3 = createResponse(CustomerIdentifiedObject.newBuilder(), CustomerIdentifiedObject.Builder::getCustomerAgreementBuilder, mRIDs[2])
        val customer = Customer(mRIDs[0])
        service.add(customer)

        doReturn(listOf(response2, response3).iterator()).`when`(stub).getIdentifiedObjects(any())

        val result = consumerClient.getIdentifiedObjects(service, mRIDs)

        assertThat(result.value.objects, hasEntry("id1", customer))
        assertThat(result.value.objects, hasKey("id2"))
        assertThat(result.value.objects, hasKey("id3"))
        assertThat(result.value.objects.size, equalTo(3))
        assertThat(result.value.failed, empty())
    }

    private fun createResponse(
        identifiedObjectBuilder: CustomerIdentifiedObject.Builder,
        subClassBuilder: (CustomerIdentifiedObject.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(
        identifiedObjectBuilder: CustomerIdentifiedObject.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        println(identifiedObjectBuilder)

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()
        responseBuilder.addIdentifiedObjects(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: CustomerIdentifiedObject.IdentifiedObjectCase): Boolean =
        type != CustomerIdentifiedObject.IdentifiedObjectCase.OTHER

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.customer.translator.customerIdentifiedObject
import com.zepben.ewb.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.forEachBuilder
import com.zepben.ewb.streaming.get.ConsumerUtils.validateFailure
import com.zepben.ewb.streaming.get.testdata.CustomerNetwork
import com.zepben.ewb.streaming.get.testservices.TestCustomerConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.cc.GetCustomersForContainerResponse
import com.zepben.protobuf.cc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.cc.GetIdentifiedObjectsResponse
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.testutils.exception.ExpectException.Companion.expect
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
import com.zepben.protobuf.cc.CustomerIdentifiedObject as CIO

internal class CustomerConsumerClientTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()

    private val consumerService = TestCustomerConsumerService()

    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(CustomerConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient = spy(CustomerConsumerClient(stub).apply { addErrorHandler(onErrorHandler) })
    private val service = consumerClient.service

    private val serverException = IllegalStateException("custom message")

    @BeforeEach
    internal fun beforeEach() {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(consumerService).build().start())
    }

    @Test
    internal fun `can get all supported types`() {
        var counter = 0
        val builder = CIO.newBuilder()

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
                assertThat(result.thrown.cause?.message, equalTo("Identified object type $type is not supported by the customer service"))
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
        assertThat("getIdentifiedObject should fail for mRID '$mRID', which isn't in the customer service", result.wasFailure)
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
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerBuilder, mRIDs[0]))
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerBuilder, mRIDs[1]))
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerAgreementBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs.asSequence())

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.objects[mRIDs[0]], instanceOf(Customer::class.java))
        assertThat(result.value.objects[mRIDs[1]], instanceOf(Customer::class.java))
        assertThat(result.value.objects[mRIDs[2]], instanceOf(CustomerAgreement::class.java))

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
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerBuilder, mRIDs[0]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        assertThat("getIdentifiedObjects should succeed", result.wasSuccessful)
        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(result.value.objects["id1"], instanceOf(Customer::class.java))
        assertThat(result.value.failed, containsInAnyOrder(mRIDs[1]))

        verify(consumerService.onGetIdentifiedObjects).invoke(eq(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build()), any())
    }

    @Test
    internal fun `getIdentifiedObjects returns map containing existing entries in the service`() {
        val mRIDs = listOf("id1", "id2", "id3")
        val customer = Customer(mRIDs[0])
        service.add(customer)

        consumerService.onGetIdentifiedObjects = spy { _, response ->
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerBuilder, mRIDs[0]))
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerBuilder, mRIDs[1]))
            response.onNext(createResponse(CIO.newBuilder(), CIO.Builder::getCustomerAgreementBuilder, mRIDs[2]))
        }

        val result = consumerClient.getIdentifiedObjects(mRIDs)

        assertThat(result.value.objects, hasEntry("id1", customer))
        assertThat(result.value.objects, hasKey("id2"))
        assertThat(result.value.objects, hasKey("id3"))
        assertThat(result.value.objects, aMapWithSize(3))
        assertThat(result.value.failed, empty())
    }

    @Test
    internal fun `getCustomersForContainer returns customers for a given container`() {
        val (_, expectedCustomerService) = CustomerNetwork.create()
        configureFeederResponses(expectedCustomerService)

        val result = consumerClient.getCustomersForContainer("customer1").throwOnError()

        assertThat(result.value.objects, aMapWithSize(1))
        assertThat(service.num<Customer>(), equalTo(1))
        assertThat(service.listOf<IdentifiedObject>().map { it.mRID }, contains("customer1"))
    }

    @Test
    internal fun `getCustomersForContainers returns customers for given containers`() {
        val (_, expectedCustomerService) = CustomerNetwork.create()
        configureFeederResponses(expectedCustomerService)

        val result = consumerClient.getCustomersForContainers(setOf("customer1", "customer2")).throwOnError()

        assertThat(result.value.objects, aMapWithSize(2))
        assertThat(service.num<Customer>(), equalTo(2))
        assertThat(service.listOf<IdentifiedObject>().map { it.mRID }, containsInAnyOrder("customer1", "customer2"))
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
        val (_, expectedCustomerService) = CustomerNetwork.create()
        configureFeederResponses(expectedCustomerService)

        val channel = mockk<Channel>()
        mockkStatic(CustomerConsumerGrpc::class)
        every { CustomerConsumerGrpc.newStub(channel) } returns stub

        val clientViaChannel = CustomerConsumerClient(channel)
        val result = clientViaChannel.getCustomersForContainer("customer1")

        assertThat(result.value.objects, aMapWithSize(clientViaChannel.service.num<Customer>()))
        assertThat(clientViaChannel.service.num<Customer>(), equalTo(1))
        assertThat(clientViaChannel.service.listOf<IdentifiedObject>().map { it.mRID }, contains("customer1"))
    }

    @Test
    internal fun `construct via GrpcChannel`() {
        val (_, expectedCustomerService) = CustomerNetwork.create()
        configureFeederResponses(expectedCustomerService)

        val channel = mockk<Channel>()
        val grpcChannel = GrpcChannel(channel)
        mockkStatic(CustomerConsumerGrpc::class)
        every { CustomerConsumerGrpc.newStub(channel) } returns stub

        val clientViaGrpcChannel = CustomerConsumerClient(grpcChannel)
        val result = clientViaGrpcChannel.getCustomersForContainer("customer1")

        assertThat(result.value.objects, aMapWithSize(clientViaGrpcChannel.service.num<Customer>()))
        assertThat(clientViaGrpcChannel.service.num<Customer>(), equalTo(1))
        assertThat(clientViaGrpcChannel.service.listOf<IdentifiedObject>().map { it.mRID }, contains("customer1"))
    }

    private fun configureFeederResponses(expectedCustomerService: CustomerService) {

        consumerService.onGetCustomersForContainer = spy { request, response ->
            val objects = mutableListOf<Customer>()
            expectedCustomerService.sequenceOf<Customer>().filter { it.mRID in request.mridsList }.forEach { customer ->
                objects.add(customer)
            }
            responseOf(objects).forEach { response.onNext(it) }
        }
    }

    private fun responseOf(objects: List<Customer>): MutableIterator<GetCustomersForContainerResponse> {
        val responses = mutableListOf<GetCustomersForContainerResponse>()
        objects.forEach {
            responses.add(GetCustomersForContainerResponse.newBuilder().apply { addIdentifiedObjects(customerIdentifiedObject(it)) }.build())
        }
        return responses.iterator()
    }

    private fun createResponse(
        identifiedObjectBuilder: CIO.Builder,
        subClassBuilder: (CIO.Builder) -> Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        return createResponse(identifiedObjectBuilder, subClassBuilder(identifiedObjectBuilder), mRID)
    }

    private fun createResponse(
        identifiedObjectBuilder: CIO.Builder,
        subClassBuilder: Any,
        mRID: String
    ): GetIdentifiedObjectsResponse {
        buildFromBuilder(subClassBuilder, mRID)
        println(identifiedObjectBuilder)

        val responseBuilder = GetIdentifiedObjectsResponse.newBuilder()
        responseBuilder.addIdentifiedObjects(identifiedObjectBuilder.build())

        return responseBuilder.build()
    }

    private fun isSupported(type: CIO.IdentifiedObjectCase): Boolean =
        type != CIO.IdentifiedObjectCase.OTHER

}

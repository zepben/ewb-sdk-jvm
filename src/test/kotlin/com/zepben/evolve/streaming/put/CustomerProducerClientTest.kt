/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.translator.toPb
import com.zepben.evolve.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.cp.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class CustomerProducerClientTest {

    private val stub = mock(CustomerProducerGrpc.CustomerProducerBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val producerClient: CustomerProducerClient = CustomerProducerClient(stub).apply { addErrorHandler(onErrorHandler) }
    private val service: CustomerService = CustomerService()

    @Test
    internal fun `sends Customer`() {
        val customer = Customer("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createCustomerService(CreateCustomerServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createCustomer(CreateCustomerRequest.newBuilder().setCustomer(customer.toPb()).build())
        stubInOrder.verify(stub).completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Customer throws`() {
        val customer = Customer("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createCustomer(any())
        producerClient.send(service)

        verify(stub).createCustomer(CreateCustomerRequest.newBuilder().setCustomer(customer.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends CustomerAgreement`() {
        val customerAgreement = CustomerAgreement("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createCustomerService(CreateCustomerServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createCustomerAgreement(CreateCustomerAgreementRequest.newBuilder().setCustomerAgreement(customerAgreement.toPb()).build())
        stubInOrder.verify(stub).completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending CustomerAgreement throws`() {
        val customerAgreement = CustomerAgreement("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createCustomerAgreement(any())
        producerClient.send(service)

        verify(stub).createCustomerAgreement(CreateCustomerAgreementRequest.newBuilder().setCustomerAgreement(customerAgreement.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Organisation`() {
        val organisation = Organisation("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createCustomerService(CreateCustomerServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createOrganisation(CreateOrganisationRequest.newBuilder().setOrganisation(organisation.toPb()).build())
        stubInOrder.verify(stub).completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Organisation throws`() {
        val organisation = Organisation("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createOrganisation(any())
        producerClient.send(service)

        verify(stub).createOrganisation(CreateOrganisationRequest.newBuilder().setOrganisation(organisation.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends PricingStructure`() {
        val pricingStructure = PricingStructure("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createCustomerService(CreateCustomerServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createPricingStructure(CreatePricingStructureRequest.newBuilder().setPricingStructure(pricingStructure.toPb()).build())
        stubInOrder.verify(stub).completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending PricingStructure throws`() {
        val pricingStructure = PricingStructure("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createPricingStructure(any())
        producerClient.send(service)

        verify(stub).createPricingStructure(CreatePricingStructureRequest.newBuilder().setPricingStructure(pricingStructure.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Tariff`() {
        val tariff = Tariff("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createCustomerService(CreateCustomerServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createTariff(CreateTariffRequest.newBuilder().setTariff(tariff.toPb()).build())
        stubInOrder.verify(stub).completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Tariff throws`() {
        val tariff = Tariff("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createTariff(any())
        producerClient.send(service)

        verify(stub).createTariff(CreateTariffRequest.newBuilder().setTariff(tariff.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

}

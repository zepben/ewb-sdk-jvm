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
package com.zepben.cimbend.put

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.translator.toPb
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.customer.translator.toPb
import com.zepben.cimbend.customer.whenCustomerServiceObject
import com.zepben.protobuf.cp.*
import io.grpc.Channel
import io.grpc.StatusException

/**
 * Producer client for a [CustomerService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class CustomerProducerClient @JvmOverloads constructor(
    private val stub: CustomerProducerGrpc.CustomerProducerBlockingStub,
    onRpcError: RpcErrorHandler = RpcErrorLogger(StatusException::class.java)
) : CimProducerClient<CustomerService>(onRpcError) {

    @JvmOverloads
    constructor(channel: Channel, onRpcError: RpcErrorHandler = RpcErrorLogger(StatusException::class.java))
        : this(CustomerProducerGrpc.newBlockingStub(channel), onRpcError)

    override fun send(service: CustomerService) {
        tryRpc { stub.createCustomerService(CreateCustomerServiceRequest.newBuilder().build()) }

        service.sequenceOf<IdentifiedObject>().forEach { sendToServer(it) }

        tryRpc { stub.completeCustomerService(CompleteCustomerServiceRequest.newBuilder().build()) }
    }

    private fun sendToServer(identifiedObject: IdentifiedObject) = tryRpc {
        whenCustomerServiceObject(
            identifiedObject,
            isCustomer = {
                val builder = CreateCustomerRequest.newBuilder().apply { customer = it.toPb() }.build()
                stub.createCustomer(builder)
            },
            isCustomerAgreement = {
                val builder = CreateCustomerAgreementRequest.newBuilder().apply { customerAgreement = it.toPb() }.build()
                stub.createCustomerAgreement(builder)
            },
            isOrganisation = {
                val builder = CreateOrganisationRequest.newBuilder().setOrganisation(it.toPb()).build()
                stub.createOrganisation(builder)
            },
            isPricingStructure = {
                val builder = CreatePricingStructureRequest.newBuilder().apply { pricingStructure = it.toPb() }.build()
                stub.createPricingStructure(builder)
            },
            isTariff = {
                val builder = CreateTariffRequest.newBuilder().apply { tariff = it.toPb() }.build()
                stub.createTariff(builder)
            }
        )
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.customer.translator.CustomerProtoToCim
import com.zepben.cimbend.grpc.GrpcResult
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cc.CustomerIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.cc.GetIdentifiedObjectsRequest
import io.grpc.Channel

/**
 * Consumer client for a [CustomerService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class CustomerConsumerClient(
    private val stub: CustomerConsumerGrpc.CustomerConsumerBlockingStub,
    private val protoToCimProvider: (CustomerService) -> CustomerProtoToCim = { CustomerProtoToCim(it) }
) : CimConsumerClient<CustomerService>() {

    constructor(channel: Channel) : this(CustomerConsumerGrpc.newBlockingStub(channel))

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return The item if found, otherwise null.
     */
    override fun getIdentifiedObject(service: CustomerService, mRID: String): GrpcResult<IdentifiedObject?> {
        return tryRpc {
            processIdentifiedObjects(service, GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
                .firstOrNull()
        }
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return A [Map] containing the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     */
    override fun getIdentifiedObjects(service: CustomerService, mRIDs: Iterable<String>): GrpcResult<Map<String, IdentifiedObject>> {
        return tryRpc {
            processIdentifiedObjects(service, GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
                .filterNotNull()
                .associateBy({ it.mRID }, { it })
        }
    }


    private fun processIdentifiedObjects(service: CustomerService, request: GetIdentifiedObjectsRequest): Sequence<IdentifiedObject?> {
        return stub.getIdentifiedObjects(request)
            .asSequence()
            .flatMap { it.identifiedObjectsList.asSequence() }
            .map { extractIdentifiedObject(service, it) }
    }

    private fun extractIdentifiedObject(service: CustomerService, it: CustomerIdentifiedObject): IdentifiedObject {
        return when (it.identifiedObjectCase) {
            ORGANISATION -> protoToCimProvider(service).addFromPb(it.organisation)
            CUSTOMER -> protoToCimProvider(service).addFromPb(it.customer)
            CUSTOMERAGREEMENT -> protoToCimProvider(service).addFromPb(it.customerAgreement)
            PRICINGSTRUCTURE -> protoToCimProvider(service).addFromPb(it.pricingStructure)
            TARIFF -> protoToCimProvider(service).addFromPb(it.tariff)
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${it.identifiedObjectCase} is not supported by the customer service")
        }
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.services.common.translator.mRID
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.translator.CustomerProtoToCim
import com.zepben.evolve.services.customer.translator.mRID
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cc.CustomerIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.cc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.cc.GetIdentifiedObjectsResponse
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import java.util.concurrent.Executors

/**
 * Consumer client for a [CustomerService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [CustomerService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class CustomerConsumerClient(
    private val stub: CustomerConsumerGrpc.CustomerConsumerStub,
    override val service: CustomerService = CustomerService(),
    override val protoToCim: CustomerProtoToCim = CustomerProtoToCim(service)
) : CimConsumerClient<CustomerService, CustomerProtoToCim>() {

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [ManagedChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(CustomerConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor())
            .apply { callCredentials?.let { withCallCredentials(it) } })

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(CustomerConsumerGrpc.newStub(channel.channel).withExecutor(Executors.newSingleThreadExecutor())
            .apply { callCredentials?.let { withCallCredentials(it) } })


    override fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetIdentifiedObjectsResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getIdentifiedObjects(streamObserver)
        val builder = GetIdentifiedObjectsRequest.newBuilder()

        batchSend(mRIDs, builder::addMrids) {
            if (builder.mridsList.isNotEmpty())
                request.onNext(builder.build())
            builder.clearMrids()
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun extractIdentifiedObject(io: CustomerIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            ORGANISATION -> extractResult(io.organisation.mRID()) { addFromPb(io.organisation) }
            CUSTOMER -> extractResult(io.customer.mRID()) { addFromPb(io.customer) }
            CUSTOMERAGREEMENT -> extractResult(io.customerAgreement.mRID()) { addFromPb(io.customerAgreement) }
            PRICINGSTRUCTURE -> extractResult(io.pricingStructure.mRID()) { addFromPb(io.pricingStructure) }
            TARIFF -> extractResult(io.tariff.mRID()) { addFromPb(io.tariff) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${io.identifiedObjectCase} is not supported by the customer service")
        }
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.translator.mRID
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.translator.CustomerProtoToCim
import com.zepben.evolve.services.customer.translator.mRID
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cc.CustomerIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.cc.GetIdentifiedObjectsRequest
import io.grpc.CallCredentials
import io.grpc.ManagedChannel

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
    private val stub: CustomerConsumerGrpc.CustomerConsumerBlockingStub,
    private val protoToCimProvider: (CustomerService) -> CustomerProtoToCim = { CustomerProtoToCim(it) }
) : CimConsumerClient<CustomerService>() {

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [ManagedChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { CustomerConsumerGrpc.newBlockingStub(channel).withCallCredentials(callCredentials) }
            ?: CustomerConsumerGrpc.newBlockingStub(channel))

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [GrpcChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { CustomerConsumerGrpc.newBlockingStub(channel.channel).withCallCredentials(callCredentials) }
            ?: CustomerConsumerGrpc.newBlockingStub(channel.channel))


    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], the item found, accessible via [GrpcResult.value].
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown]. One of:
     *    - [NoSuchElementException] if the object could not be found.
     *    - The gRPC error that occurred while retrieving the object
     */
    override fun getIdentifiedObject(service: CustomerService, mRID: String): GrpcResult<IdentifiedObject> = tryRpc {
        processIdentifiedObjects(service, setOf(mRID)).firstOrNull()?.identifiedObject
            ?: throw NoSuchElementException("No object with mRID $mRID could be found.")
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [CustomerConsumerClient] warning in this case.
     */
    override fun getIdentifiedObjects(service: CustomerService, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = tryRpc {
        processExtractResults(mRIDs, processIdentifiedObjects(service, mRIDs.toSet()))
    }

    private fun processIdentifiedObjects(service: CustomerService, mRIDs: Set<String>): Sequence<ExtractResult> {
        val toFetch = mutableSetOf<String>()
        val existing = mutableSetOf<ExtractResult>()
        mRIDs.forEach { mRID ->  // Only process mRIDs not already present in service
            service.get<IdentifiedObject>(mRID)?.let { existing.add(ExtractResult(it, it.mRID)) } ?: toFetch.add(mRID)
        }

        return stub.getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(toFetch).build())
            .asSequence()
            .flatMap { it.identifiedObjectsList.asSequence() }
            .map {
                extractIdentifiedObject(service, it)
            } + existing
    }

    private fun extractIdentifiedObject(service: CustomerService, io: CustomerIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            ORGANISATION -> extractResult(service, io.organisation.mRID()) { it.addFromPb(io.organisation) }
            CUSTOMER -> extractResult(service, io.customer.mRID()) { it.addFromPb(io.customer) }
            CUSTOMERAGREEMENT -> extractResult(service, io.customerAgreement.mRID()) { it.addFromPb(io.customerAgreement) }
            PRICINGSTRUCTURE -> extractResult(service, io.pricingStructure.mRID()) { it.addFromPb(io.pricingStructure) }
            TARIFF -> extractResult(service, io.tariff.mRID()) { it.addFromPb(io.tariff) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${io.identifiedObjectCase} is not supported by the customer service")
        }
    }

    private inline fun <reified CIM : IdentifiedObject> extractResult(
        service: CustomerService,
        mRID: String,
        addFromPb: (CustomerProtoToCim) -> CIM?
    ): ExtractResult =
        ExtractResult(service[mRID] ?: addFromPb(protoToCimProvider(service)), mRID)

}

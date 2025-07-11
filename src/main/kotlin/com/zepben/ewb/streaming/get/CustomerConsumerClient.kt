/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.translator.mRID
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.customer.translator.CustomerProtoToCim
import com.zepben.ewb.services.customer.translator.mRID
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.cc.*
import com.zepben.protobuf.cc.CustomerIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import io.grpc.CallCredentials
import io.grpc.Channel
import java.util.concurrent.ExecutorService
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
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class CustomerConsumerClient @JvmOverloads constructor(
    private val stub: CustomerConsumerGrpc.CustomerConsumerStub,
    override val service: CustomerService = CustomerService(),
    override val protoToCim: CustomerProtoToCim = CustomerProtoToCim(service),
    executor: ExecutorService? = null
) : CimConsumerClient<CustomerService, CustomerProtoToCim>(executor) {

    init {
        executor?.also { stub.withExecutor(it) }
    }

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            CustomerConsumerGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [CustomerConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) : this(channel.channel, callCredentials)

    /**
     * Get the [Customer]s in the [EquipmentContainer] represented by [mRID].
     *
     * @param mRID The mRID of the [EquipmentContainer]s to fetch [Customer]s for.
     * @return a [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [CustomerConsumerClient] warning in this case.
     */
    fun getCustomersForContainer(mRID: String): GrpcResult<MultiObjectResult> = getCustomersForContainers(setOf(mRID))

    /**
     * Get the [Customer]s in the [EquipmentContainer]s represented by their [mRIDs].
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch [Customer]s for.
     * @return a [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [CustomerConsumerClient] warning in this case.
     */
    fun getCustomersForContainers(mRIDs: Set<String>): GrpcResult<MultiObjectResult> = handleMultiObjectRPC {
        processCustomersForContainers(mRIDs)
    }

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

    private fun processCustomersForContainers(
        mRIDs: Set<String>,
    ): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetCustomersForContainerResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getCustomersForContainer(streamObserver)
        val builder = GetCustomersForContainerRequest.newBuilder()

        batchSend(mRIDs.asSequence(), builder::addMrids) {
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

    override fun runGetMetadata(getMetadataRequest: GetMetadataRequest, streamObserver: AwaitableStreamObserver<GetMetadataResponse>) {
        stub.getMetadata(getMetadataRequest, streamObserver)
    }
}

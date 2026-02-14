/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet as PBChangeSet
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.translator.VariantProtoToCim
import com.zepben.ewb.services.variant.translator.addFromPb
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.services.network.translator.mRID
import com.zepben.ewb.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject as PBNetworkModelProject
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.nc.GetNetworkHierarchyResponse
import com.zepben.protobuf.vc.*
import io.grpc.CallCredentials
import io.grpc.Channel
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Consumer client for a [VariantService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [VariantService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class VariantConsumerClient @JvmOverloads constructor(
    private val stub: VariantConsumerGrpc.VariantConsumerStub,
    override val service: VariantService = VariantService(),
    override val protoToCim: VariantProtoToCim = VariantProtoToCim(service),
    executor: ExecutorService? = null
) : CimConsumerClient<VariantService, VariantProtoToCim>(executor) {

    init {
        executor?.also { stub.withExecutor { it } }
    }

    /**
     * Create a [VariantConsumerClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            VariantConsumerGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [VariantConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) : this(channel.channel, callCredentials)

    override fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult> =
        throw NotImplementedError()

    /**
     * Retrieve a ChangeSet from the server. Note this does not receive the contents of the ChangeSet, only the metadata.
     * To retrieve the contents you must use getChangeSet against each respective service (network, diagram, customer).
     *
     * @param mRID The mRID of the ChangeSet to retrieve.
     * @return A [GrpcResult] capturing the result of the request for the ChangeSet.
     */
    fun getChangeSet(mRID: String): GrpcResult<ChangeSet> = tryRpc {
        var changeSet: ChangeSet? = null
        val streamObserver = AwaitableStreamObserver<GetChangeSetResponse> { response ->
            changeSet = service.addFromPb(response.changeSet)
        }
        stub.getChangeSet(GetChangeSetRequest.newBuilder().setMrid(mRID).build(), streamObserver)

        streamObserver.await()
        changeSet ?: throw IOException("No change set was received before GRPC channel was closed.")
    }

    // TODO: docs
    fun getNetworkModelProjects(): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processNetworkModelProjects() }

    // TODO: docs
    fun getNetworkModelProject(mRID: String): GrpcResult<IdentifiedObject> = tryRpc {
        processNetworkModelProjects(sequenceOf(mRID)).firstOrNull()?.identifiedObject
            ?: throw NoSuchElementException("No object with mRID $mRID could be found.")
    }

    private fun processNetworkModelProjects(mRIDs: Sequence<String>? = null): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetNetworkModelProjectsResponse> { response ->
            response.networkModelProjectsList.forEach {
                extractNetworkModelProject(it)?.let { nmp ->
                    extractResults.add(nmp)
                }
            }
        }

        val request = stub.getNetworkModelProjects(streamObserver)
        val builder = GetNetworkModelProjectsRequest.newBuilder()

        if (mRIDs == null) {
            request.onNext(builder.build())
        } else {
            batchSend(mRIDs, builder::addMrids) {
                if (builder.mridsList.isNotEmpty())
                    request.onNext(builder.build())
                builder.clearMrids()
            }
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }

    override fun runGetMetadata(
        getMetadataRequest: GetMetadataRequest,
        streamObserver: AwaitableStreamObserver<GetMetadataResponse>
    ) {
        stub.getMetadata(getMetadataRequest, streamObserver)
    }

    private fun extractNetworkModelProject(io: PBNetworkModelProject): ExtractResult? =
        protoToCim.networkService.addFromPb(io)?.let {
            ExtractResult(it, it.mRID)
        }

    private fun extractChangeSet(cs: PBChangeSet): DSExtractResult =
        protoToCim.networkService.addFromPb(cs).let {
            DSExtractResult(it, it.mRID)
        }

}

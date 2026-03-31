/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.google.protobuf.Timestamp
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.database.paths.VariantContents
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.ewb.services.common.translator.toTimestamp
import com.zepben.ewb.services.network.NetworkState
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.translator.VariantProtoToCim
import com.zepben.ewb.services.variant.translator.addFromPb
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.vc.*
import io.grpc.CallCredentials
import io.grpc.Channel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.Executors
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject as PBNetworkModelProject



/**
 * Consumer client for a [VariantService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [VariantService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class VariantConsumerClient @JvmOverloads constructor(
    override val stub: VariantConsumerGrpc.VariantConsumerStub,
    override val service: VariantService,
    override val protoToCim: VariantProtoToCim = VariantProtoToCim(service),
) : CimConsumerClient<VariantService, VariantProtoToCim, VariantConsumerGrpc.VariantConsumerStub>() {

    /**
     * Create a [VariantConsumerClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, variantService: VariantService = VariantService(), callCredentials: CallCredentials? = null) :
        this(
            VariantConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()).apply { callCredentials?.let { withCallCredentials(it) } },
            variantService
        )

    /**
     * Create a [VariantConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, variantService: VariantService = VariantService(), callCredentials: CallCredentials? = null) : this(channel.channel, variantService, callCredentials)

    override fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetIdentifiedObjectsResponse> { response ->
            response.identifiableObjectsList.forEach {
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

    /**
     * Retrieve a ChangeSet and all its associations from the server. See [getChangeSets] documentation.
     */
    fun getChangeSet(mRID: String, baseModelVersion: LocalDate? = null): GrpcResult<MultiObjectResult> = getChangeSets(listOf(mRID), baseModelVersion)

    /**
     * Retrieve a ChangeSet and all its associations from the server. This does not receive the contents of the ChangeSet, only the metadata.
     * To retrieve the contents you should use [ChangeSetConsumerClient.getChangeSet], which will call this function for you.
     *
     * @param mRIDs The mRIDs of the [ChangeSet]s to retrieve.
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed]
     */
    fun getChangeSets(mRIDs: Iterable<String>, baseModelVersion: LocalDate? = null): GrpcResult<MultiObjectResult> {
        val mor = MultiObjectResult()
        mRIDs.forEach { mRID ->
            val streamObserver = AwaitableStreamObserver<GetChangeSetResponse> { response ->
                val result = service.addFromPb(response.identifiableObject)
                result.identifiedObject?.let { mor.objects[result.mRID] = it } ?: mor.failed.add(result.mRID)
            }
            val requestBuilder = GetChangeSetRequest.newBuilder().setChangeSetMRID(mRID)
            baseModelVersion?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toTimestamp()?.also {
                requestBuilder.setModelVersion(it)
            }
            stub.getChangeSet(requestBuilder.build(), streamObserver)

            streamObserver.await()

        }

        resolveReferences(mor)?.let { return it }
        return GrpcResult(mor)
    }

    // TODO: docs
    fun getNetworkModelProjects(): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processNetworkModelProjects() }

    // TODO: docs
    fun getNetworkModelProject(mRID: String): GrpcResult<Identifiable> = tryRpc {
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
        protoToCim.variantService.addFromPb(io)?.let {
            ExtractResult(it, it.mRID)
        }

    private inline fun <reified T> getWithReferences(
        mRIDs: Sequence<String>,
        expectedClass: Class<out T>,
    ): GrpcResult<MultiObjectResult> {
        val mor = MultiObjectResult()

        val toFetch = mutableListOf<String>()
        mRIDs.forEach { mRID ->  // Only process mRIDs not already present in service
            service.get<Identifiable>(mRID)?.let { mor.objects[it.mRID] = it } ?: toFetch.add(mRID)
        }

        val response = getIdentifiedObjects(toFetch.asSequence())
        val result = response.onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }.value

        val invalid = result.objects.values.filter { !expectedClass.isInstance(it) }.toMutableSet()
        if (invalid.isNotEmpty()) {
            val e = ClassCastException("Unable to extract ${expectedClass.simpleName} networks from ${invalid.map { it.typeNameAndMRID() }}.")
            return GrpcResult.ofError(e, tryHandleError(e))
        }

        mor.objects.putAll(result.objects)
        mor.failed.addAll(result.failed)

        resolveReferences(mor)?.let { return@getWithReferences it }

        return GrpcResult(mor)
    }

    internal fun resolveReferences(mor: MultiObjectResult): GrpcResult<MultiObjectResult>? {
        var res = mor
        do {
            val toResolve = res.objects.keys
                .flatMap { service.getUnresolvedReferencesFrom(it) }
                .map { it.toMrid }
                .distinct()
                .toList()

            res = getIdentifiedObjects(toResolve).onError { thrown, wasHandled ->
                return GrpcResult.ofError(thrown, wasHandled)
            }.value
            mor.objects.putAll(res.objects)

        } while (res.objects.isNotEmpty())
        return null
    }

    private fun extractIdentifiedObject(io: VariantObject): ExtractResult =
        protoToCim.variantService.addFromPb(io).let {
            ExtractResult(it.identifiedObject, it.mRID)
        }

}

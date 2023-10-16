/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.translator.DiagramProtoToCim
import com.zepben.evolve.services.diagram.translator.mRID
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.dc.*
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Consumer client for a [DiagramService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [DiagramService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class DiagramConsumerClient(
    private val stub: DiagramConsumerGrpc.DiagramConsumerStub,
    override val service: DiagramService = DiagramService(),
    override val protoToCim: DiagramProtoToCim = DiagramProtoToCim(service),
    executor: ExecutorService? = null
) : CimConsumerClient<DiagramService, DiagramProtoToCim>(executor) {

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [ManagedChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(
            DiagramConsumerGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(
            DiagramConsumerGrpc.newStub(channel.channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )


    /**
     * Get DiagramObjects for a given mRID. This will effectively call [DiagramService.getDiagramObjects] on the remote server and return any DiagramObjects
     * that contain a match against the given mRID.
     *
     * @param mRIDs The mRIDs to fetch DiagramObjects for
     * @return a [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [DiagramConsumerClient] warning in this case.
     */
    fun getDiagramObjects(mRID: String): GrpcResult<MultiObjectResult> = getDiagramObjects(setOf(mRID))

    /**
     * Get DiagramObjects for a given mRID. This will effectively call [DiagramService.getDiagramObjects] on the remote server and return any DiagramObjects
     * that contain a match against the given mRID.
     *
     * @param mRIDs The mRIDs to fetch DiagramObjects for
     * @return a [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [DiagramConsumerClient] warning in this case.
     */
    fun getDiagramObjects(mRIDs: Set<String>): GrpcResult<MultiObjectResult> = handleMultiObjectRPC {
        processDiagramObjects(mRIDs.asSequence())
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

    private fun extractIdentifiedObject(io: DiagramIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            DIAGRAM -> extractResult(io.diagram.mRID()) { addFromPb(io.diagram) }
            DIAGRAMOBJECT -> extractResult(io.diagramObject.mRID()) { addFromPb(io.diagramObject) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${io.identifiedObjectCase} is not supported by the diagram service")
        }
    }

    private fun processDiagramObjects(
        mRIDs: Sequence<String>,
    ): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetDiagramObjectsResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getDiagramObjects(streamObserver)
        val builder = GetDiagramObjectsRequest.newBuilder()

        batchSend(mRIDs.asSequence(), builder::addMrids) {
            if (builder.mridsList.isNotEmpty())
                request.onNext(builder.build())
            builder.clearMrids()
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }
    override fun runGetMetadata(getMetadataRequest: GetMetadataRequest, streamObserver: AwaitableStreamObserver<GetMetadataResponse>) {
        stub.getMetadata(getMetadataRequest, streamObserver)
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.translator.DiagramProtoToCim
import com.zepben.evolve.services.diagram.translator.mRID
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.DiagramIdentifiedObject
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.dc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.dc.GetIdentifiedObjectsResponse
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
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
 */
class DiagramConsumerClient(
    private val stub: DiagramConsumerGrpc.DiagramConsumerStub,
    override val service: DiagramService = DiagramService(),
    override val protoToCim: DiagramProtoToCim = DiagramProtoToCim(service)
) : CimConsumerClient<DiagramService, DiagramProtoToCim>() {

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [ManagedChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(DiagramConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor())
            .apply { callCredentials?.let { withCallCredentials(it) } })

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(DiagramConsumerGrpc.newStub(channel.channel).withExecutor(Executors.newSingleThreadExecutor())
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

    private fun extractIdentifiedObject(io: DiagramIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            DIAGRAM -> extractResult(io.diagram.mRID()) { addFromPb(io.diagram) }
            DIAGRAMOBJECT -> extractResult(io.diagramObject.mRID()) { addFromPb(io.diagramObject) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${io.identifiedObjectCase} is not supported by the diagram service")
        }
    }

}

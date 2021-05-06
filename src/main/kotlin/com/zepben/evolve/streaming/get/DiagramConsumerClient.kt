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
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.translator.DiagramProtoToCim
import com.zepben.evolve.services.diagram.translator.mRID
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.DiagramIdentifiedObject
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.dc.GetIdentifiedObjectsRequest
import io.grpc.CallCredentials
import io.grpc.ManagedChannel

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
    private val stub: DiagramConsumerGrpc.DiagramConsumerBlockingStub,
    private val protoToCimProvider: (DiagramService) -> DiagramProtoToCim = { DiagramProtoToCim(it) }
) : CimConsumerClient<DiagramService>() {

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [ManagedChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { DiagramConsumerGrpc.newBlockingStub(channel).withCallCredentials(callCredentials) }
            ?: DiagramConsumerGrpc.newBlockingStub(channel))

    /**
     * Create a [DiagramConsumerClient]
     *
     * @param channel [GrpcChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { DiagramConsumerGrpc.newBlockingStub(channel.channel).withCallCredentials(callCredentials) }
            ?: DiagramConsumerGrpc.newBlockingStub(channel.channel))

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
    override fun getIdentifiedObject(service: DiagramService, mRID: String): GrpcResult<IdentifiedObject> = tryRpc {
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
     * Note the [DiagramConsumerClient] warning in this case.
     */
    override fun getIdentifiedObjects(service: DiagramService, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = tryRpc {
        processExtractResults(mRIDs, processIdentifiedObjects(service, mRIDs.toSet()))
    }


    private fun processIdentifiedObjects(service: DiagramService, mRIDs: Set<String>): Sequence<ExtractResult> {
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

    private fun extractIdentifiedObject(service: DiagramService, io: DiagramIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            DIAGRAM -> extractResult(service, io.diagram.mRID()) { it.addFromPb(io.diagram) }
            DIAGRAMOBJECT -> extractResult(service, io.diagramObject.mRID()) { it.addFromPb(io.diagramObject) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${io.identifiedObjectCase} is not supported by the diagram service")
        }
    }

    private inline fun <reified CIM : IdentifiedObject> extractResult(
        service: DiagramService,
        mRID: String,
        addFromPb: (DiagramProtoToCim) -> CIM?
    ): ExtractResult =
        ExtractResult(service[mRID] ?: addFromPb(protoToCimProvider(service)), mRID)

}

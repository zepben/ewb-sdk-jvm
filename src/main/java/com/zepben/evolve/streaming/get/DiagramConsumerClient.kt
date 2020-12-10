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
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.dc.DiagramIdentifiedObject
import com.zepben.protobuf.dc.DiagramIdentifiedObject.IdentifiedObjectCase.*
import com.zepben.protobuf.dc.GetIdentifiedObjectsRequest
import io.grpc.Channel

/**
 * Consumer client for a [DiagramService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class DiagramConsumerClient(
    private val stub: DiagramConsumerGrpc.DiagramConsumerBlockingStub,
    private val protoToCimProvider: (DiagramService) -> DiagramProtoToCim = { DiagramProtoToCim(it) }
) : CimConsumerClient<DiagramService>() {

    constructor(channel: Channel) : this(DiagramConsumerGrpc.newBlockingStub(channel))

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - The item if found
     * - null if an object could not be found or it was found but not added to [service] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the object, in which case, [GrpcResult.wasSuccessful] will return false.
     */
    override fun getIdentifiedObject(service: DiagramService, mRID: String): GrpcResult<IdentifiedObject?> {
        return tryRpc {
            processIdentifiedObjects(service, setOf(mRID)).firstOrNull()?.identifiedObject
        }
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * WARNING: This operation is not atomic upon [service], and thus if processing fails partway through [mRIDs], any previously successful mRID will have been
     * added to the service, and thus you may have an incomplete [DiagramService]. Also note that adding to the [service] may not occur for an object if another
     * object with the same mRID is already present in [service]. [MultiObjectResult.failed] can be used to check for mRIDs that were retrieved but not
     * added to [service].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
     * Note the warning above in this case.
     */
    override fun getIdentifiedObjects(service: DiagramService, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> {
        return tryRpc {
            processIdentifiedObjects(service, mRIDs.toSet()).let { extracted ->
                val results = mutableMapOf<String, IdentifiedObject>()
                val failed = mutableSetOf<String>()
                extracted.forEach {
                    if (it.identifiedObject == null) failed.add(it.mRID) else results[it.identifiedObject.mRID] = it.identifiedObject
                }
                MultiObjectResult(results, failed)
            }
        }
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

    private fun extractIdentifiedObject(service: DiagramService, it: DiagramIdentifiedObject): ExtractResult {
        return when (it.identifiedObjectCase) {
            DIAGRAM -> ExtractResult(protoToCimProvider(service).addFromPb(it.diagram), it.diagram.mRID())
            DIAGRAMOBJECT -> ExtractResult(protoToCimProvider(service).addFromPb(it.diagramObject), it.diagramObject.mRID())
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${it.identifiedObjectCase} is not supported by the diagram service")
        }
    }

}

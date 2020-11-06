/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.diagram.DiagramProtoToCim
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.grpc.GrpcResult
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
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return The item if found, otherwise null.
     */
    override fun getIdentifiedObject(service: DiagramService, mRID: String): GrpcResult<IdentifiedObject?> {
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
    override fun getIdentifiedObjects(
        service: DiagramService,
        mRIDs: Iterable<String>
    ): GrpcResult<Map<String, IdentifiedObject>> {
        return tryRpc {
            processIdentifiedObjects(service, GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
                .filterNotNull()
                .associateBy({ it.mRID }, { it })
        }
    }


    private fun processIdentifiedObjects(
        service: DiagramService,
        request: GetIdentifiedObjectsRequest
    ): Sequence<IdentifiedObject?> {
        return stub.getIdentifiedObjects(request)
            .asSequence()
            .flatMap { it.identifiedObjectsList.asSequence() }
            .map { extractIdentifiedObject(service, it) }
    }

    private fun extractIdentifiedObject(service: DiagramService, it: DiagramIdentifiedObject): IdentifiedObject {
        return when (it.identifiedObjectCase) {
            DIAGRAM -> protoToCimProvider(service).addFromPb(it.diagram)
            DIAGRAMOBJECT -> protoToCimProvider(service).addFromPb(it.diagramObject)
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${it.identifiedObjectCase} is not supported by the diagram service")
        }
    }

}
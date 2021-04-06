/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.translator.toPb
import com.zepben.evolve.services.diagram.whenDiagramServiceObject
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.protobuf.dp.*
import io.grpc.CallCredentials
import io.grpc.ManagedChannel

/**
 * Producer client for a [DiagramService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class DiagramProducerClient(
    private val stub: DiagramProducerGrpc.DiagramProducerBlockingStub
) : CimProducerClient<DiagramService>() {

    /**
     * Create a [DiagramProducerClient]
     *
     * @param channel [ManagedChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { DiagramProducerGrpc.newBlockingStub(channel).withCallCredentials(callCredentials) }
            ?: DiagramProducerGrpc.newBlockingStub(channel))

    /**
     * Create a [DiagramProducerClient]
     *
     * @param channel [GrpcChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { DiagramProducerGrpc.newBlockingStub(channel.channel).withCallCredentials(callCredentials) }
            ?: DiagramProducerGrpc.newBlockingStub(channel.channel))

    override fun send(service: DiagramService) {
        tryRpc { stub.createDiagramService(CreateDiagramServiceRequest.newBuilder().build()) }
            .throwOnUnhandledError()

        service.sequenceOf<IdentifiedObject>().forEach { sendToServer(it) }

        tryRpc { stub.completeDiagramService(CompleteDiagramServiceRequest.newBuilder().build()) }
            .throwOnUnhandledError()
    }

    private fun sendToServer(identifiedObject: IdentifiedObject) = tryRpc {
        whenDiagramServiceObject(
            identifiedObject,
            isDiagram = {
                val builder = CreateDiagramRequest.newBuilder().setDiagram(it.toPb()).build()
                stub.createDiagram(builder)
            },
            isDiagramObject = {
                val builder = CreateDiagramObjectRequest.newBuilder().apply { diagramObject = it.toPb() }.build()
                stub.createDiagramObject(builder)
            }
        )
    }
        .throwOnUnhandledError()

}

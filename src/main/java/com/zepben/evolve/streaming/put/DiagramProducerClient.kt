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
import com.zepben.protobuf.dp.*
import io.grpc.Channel

/**
 * Producer client for a [DiagramService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class DiagramProducerClient(
    private val stub: DiagramProducerGrpc.DiagramProducerBlockingStub
) : CimProducerClient<DiagramService>() {

    constructor(channel: Channel) : this(DiagramProducerGrpc.newBlockingStub(channel))

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

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zepben.cimbendput

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.diagram.toPb
import com.zepben.cimbend.diagram.whenDiagramServiceObject
import com.zepben.protobuf.dp.*
import io.grpc.Channel
import io.grpc.StatusException

/**
 * Producer client for a [DiagramService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class DiagramProducerClient @JvmOverloads constructor(
    private val stub: DiagramProducerGrpc.DiagramProducerBlockingStub,
    onRpcError: RpcErrorHandler = RpcErrorLogger(StatusException::class.java)
) : CimProducerClient<DiagramService>(onRpcError) {

    @JvmOverloads
    constructor(channel: Channel, onRpcError: RpcErrorHandler = RpcErrorLogger(StatusException::class.java))
        : this(DiagramProducerGrpc.newBlockingStub(channel), onRpcError)

    override fun send(service: DiagramService) {
        tryRpc { stub.createDiagramService(CreateDiagramServiceRequest.newBuilder().build()) }

        service.sequenceOf<IdentifiedObject>().forEach { sendToServer(it) }

        tryRpc { stub.completeDiagramService(CompleteDiagramServiceRequest.newBuilder().build()) }
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
}

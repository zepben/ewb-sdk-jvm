/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.translator.toPb
import com.zepben.evolve.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.dp.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class DiagramProducerClientTest {

    private val stub = mock(DiagramProducerGrpc.DiagramProducerBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val producerClient: DiagramProducerClient = DiagramProducerClient(stub).apply { addErrorHandler(onErrorHandler) }
    private val service: DiagramService = DiagramService()

    @Test
    internal fun `sends Diagram`() {
        val diagram = Diagram("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createDiagramService(CreateDiagramServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createDiagram(CreateDiagramRequest.newBuilder().setDiagram(diagram.toPb()).build())
        stubInOrder.verify(stub).completeDiagramService(CompleteDiagramServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Diagram throws`() {
        val diagram = Diagram("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createDiagram(any())
        producerClient.send(service)

        verify(stub).createDiagram(CreateDiagramRequest.newBuilder().setDiagram(diagram.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends DiagramObject`() {
        val diagramObject = DiagramObject("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createDiagramService(CreateDiagramServiceRequest.newBuilder().build())
        stubInOrder.verify(stub).createDiagramObject(CreateDiagramObjectRequest.newBuilder().setDiagramObject(diagramObject.toPb()).build())
        stubInOrder.verify(stub).completeDiagramService(CompleteDiagramServiceRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending DiagramObject throws`() {
        val diagramObject = DiagramObject("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createDiagramObject(any())
        producerClient.send(service)

        verify(stub).createDiagramObject(CreateDiagramObjectRequest.newBuilder().setDiagramObject(diagramObject.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

}

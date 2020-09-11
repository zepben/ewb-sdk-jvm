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
package com.zepben.cimbend.put

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.diagram.toPb
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

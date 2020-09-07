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
package com.zepben.cimbend.diagram

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class DiagramServiceTest {

    private val service = DiagramService()

    @Test
    internal fun supportsDiagram() {
        val diagram = Diagram()
        assertThat(service.add(diagram), equalTo(true))
        assertThat(service.remove(diagram), equalTo(true))
    }

    @Test
    internal fun supportsDiagramObject() {
        val diagramObject = DiagramObject()
        assertThat(service.add(diagramObject), equalTo(true))
        assertThat(service.remove(diagramObject), equalTo(true))
    }

    @Test
    internal fun testIndexDiagramObject() {
        val diagram1 = Diagram()
        val diagram2 = Diagram()
        val diagramObject1 = DiagramObject().apply { diagram = diagram1; identifiedObjectMRID = "io1" }
        val diagramObject2 = DiagramObject().apply { diagram = diagram1; identifiedObjectMRID = "io2" }
        val diagramObject3 = DiagramObject().apply { diagram = diagram2; identifiedObjectMRID = "io1" }

        service.add(diagramObject1)
        service.add(diagramObject2)
        service.add(diagramObject3)

        assertThat(service[diagramObject1.mRID], equalTo(diagramObject1))
        assertThat(service[diagramObject2.mRID], equalTo(diagramObject2))
        assertThat(service[diagramObject3.mRID], equalTo(diagramObject3))

        assertThat(service.getDiagramObjects(diagramObject1.mRID), containsInAnyOrder(diagramObject1))
        assertThat(service.getDiagramObjects(diagramObject2.mRID), containsInAnyOrder(diagramObject2))
        assertThat(service.getDiagramObjects(diagramObject3.mRID), containsInAnyOrder(diagramObject3))

        assertThat(service.getDiagramObjects(diagram1.mRID), containsInAnyOrder(diagramObject1, diagramObject2))
        assertThat(service.getDiagramObjects(diagram2.mRID), containsInAnyOrder(diagramObject3))

        assertThat(service.getDiagramObjects("io1"), containsInAnyOrder(diagramObject1, diagramObject3))
        assertThat(service.getDiagramObjects("io2"), containsInAnyOrder(diagramObject2))

        service.remove(diagramObject3)

        assertThat(service[diagramObject1.mRID], equalTo(diagramObject1))
        assertThat(service[diagramObject2.mRID], equalTo(diagramObject2))
        assertThat(service[diagramObject3.mRID], nullValue())

        assertThat(service.getDiagramObjects(diagramObject1.mRID), containsInAnyOrder(diagramObject1))
        assertThat(service.getDiagramObjects(diagramObject2.mRID), containsInAnyOrder(diagramObject2))
        assertThat(service.getDiagramObjects(diagramObject3.mRID), empty())

        assertThat(service.getDiagramObjects(diagram1.mRID), containsInAnyOrder(diagramObject1, diagramObject2))
        assertThat(service.getDiagramObjects(diagram2.mRID), empty())

        assertThat(service.getDiagramObjects("io1"), containsInAnyOrder(diagramObject1))
        assertThat(service.getDiagramObjects("io2"), containsInAnyOrder(diagramObject2))
    }
}

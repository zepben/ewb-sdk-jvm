/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.diagram

import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class DiagramServiceTest {

    private val service = DiagramService()


    @Test
    internal fun `can add and remove supported types`() {
        service.supportedClasses
            .asSequence()
            .map { it.getDeclaredConstructor().newInstance() }
            .forEach {
                assertThat("Initial tryAdd should return true", service.tryAdd(it))
                assertThat(service[it.mRID], equalTo(it))
                assertThat("tryRemove should return true for previously-added object", service.tryRemove(it))
                assertThat(service[it.mRID], nullValue())
            }
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

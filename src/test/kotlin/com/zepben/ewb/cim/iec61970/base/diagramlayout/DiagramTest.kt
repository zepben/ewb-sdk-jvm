/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.diagramlayout

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Diagram("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val diagram = Diagram(generateId())

        assertThat(diagram.diagramStyle, equalTo(DiagramStyle.SCHEMATIC))
        assertThat(diagram.orientationKind, equalTo(OrientationKind.POSITIVE))

        diagram.diagramStyle = DiagramStyle.GEOGRAPHIC
        diagram.orientationKind = OrientationKind.NEGATIVE

        assertThat(diagram.diagramStyle, equalTo(DiagramStyle.GEOGRAPHIC))
        assertThat(diagram.orientationKind, equalTo(OrientationKind.NEGATIVE))
    }

    @Test
    internal fun assignsDiagramToObjectsIfMissing() {
        val diagram = Diagram(generateId())
        val diagramObject = DiagramObject(generateId())

        diagram.addDiagramObject(diagramObject)
        assertThat(diagramObject.diagram, equalTo(diagram))
    }

    @Test
    internal fun rejectsObjectWithWrongDiagram() {
        val d1 = Diagram(generateId())
        val d2 = Diagram(generateId())
        val obj = DiagramObject(generateId()).apply { diagram = d2 }

        ExpectException.expect { d1.addDiagramObject(obj) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${obj.typeNameAndMRID()} `diagram` property references ${d2.typeNameAndMRID()}, expected ${d1.typeNameAndMRID()}.")
    }

    @Test
    internal fun diagramObjects() {
        PrivateCollectionValidator.validateUnordered(
            ::Diagram,
            ::DiagramObject,
            Diagram::diagramObjects,
            { it.numDiagramObjects() },
            Diagram::getDiagramObject,
            Diagram::addDiagramObject,
            Diagram::removeDiagramObject,
            Diagram::clearDiagramObjects
        )
    }

}

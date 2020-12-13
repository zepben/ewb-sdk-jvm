/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.diagramlayout

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramObjectTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(DiagramObject().mRID, not(equalTo("")))
        assertThat(DiagramObject("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val diagramObject = DiagramObject().apply { this.diagram = diagram }
        val diagram = Diagram()

        assertThat(diagramObject.diagram, nullValue())
        assertThat(diagramObject.identifiedObjectMRID, nullValue())
        assertThat(diagramObject.style, equalTo(DiagramObjectStyle.NONE))
        assertThat(diagramObject.rotation, equalTo(0.0))

        diagramObject.apply {
            this.diagram = diagram
            identifiedObjectMRID = "identifiedObjectMRID"
            style = DiagramObjectStyle.CONDUCTOR_132000
            rotation = 34.5
        }

        assertThat(diagramObject.diagram, equalTo(diagram))
        assertThat(diagramObject.identifiedObjectMRID, equalTo("identifiedObjectMRID"))
        assertThat(diagramObject.style, equalTo(DiagramObjectStyle.CONDUCTOR_132000))
        assertThat(diagramObject.rotation, equalTo(34.5))
    }

    @Test
    internal fun diagramObjectPoints() {
        var pointNumber = 0.0
        PrivateCollectionValidator.validate(
            { DiagramObject() },
            { DiagramObjectPoint(pointNumber, pointNumber++) },
            DiagramObject::numPoints,
            DiagramObject::getPoint,
            DiagramObject::forEachPoint,
            DiagramObject::addPoint,
            DiagramObject::addPoint,
            DiagramObject::removePoint,
            DiagramObject::clearPoints
        )
    }
}

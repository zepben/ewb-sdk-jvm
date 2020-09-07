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
package com.zepben.cimbend.cim.iec61970.base.diagramlayout

import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
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

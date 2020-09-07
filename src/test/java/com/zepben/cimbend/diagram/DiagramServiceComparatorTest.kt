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

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.*
import com.zepben.cimbend.common.BaseServiceComparatorTest
import com.zepben.cimbend.utils.ServiceComparatorValidator
import org.junit.jupiter.api.Test

internal class DiagramServiceComparatorTest : BaseServiceComparatorTest() {

    override val comparatorValidator: ServiceComparatorValidator<DiagramService, DiagramServiceComparator> = ServiceComparatorValidator(
        { DiagramService() },
        { _ -> DiagramServiceComparator() }
    )

    @Test
    internal fun testCompareDiagramAttributes() {
        compareIdentifiedObject { Diagram(it) }

        comparatorValidator.validateProperty(Diagram::diagramStyle, { Diagram(it) }, { DiagramStyle.SCHEMATIC }, { DiagramStyle.GEOGRAPHIC })
        comparatorValidator.validateProperty(Diagram::orientationKind, { Diagram(it) }, { OrientationKind.POSITIVE }, { OrientationKind.NEGATIVE })
        comparatorValidator.validateProperty(Diagram::orientationKind, { Diagram(it) }, { OrientationKind.POSITIVE }, { OrientationKind.NEGATIVE })
        comparatorValidator.validateIdObjCollection(
            Diagram::diagramObjects, Diagram::addDiagramObject,
            { Diagram(it) },
            { DiagramObject("1").apply { diagram = it } },
            { DiagramObject("2").apply { diagram = it } })
    }

    @Test
    internal fun testCompareDiagramObjectAttributes() {
        compareIdentifiedObject { DiagramObject(it) }

        comparatorValidator.validateProperty(DiagramObject::diagram, { DiagramObject(it) }, { Diagram("d1") }, { Diagram("d2") })
        comparatorValidator.validateProperty(DiagramObject::identifiedObjectMRID, { DiagramObject(it) }, { "dio1" }, { "dio2" })
        comparatorValidator.validateProperty(DiagramObject::style, { DiagramObject(it) }, { DiagramObjectStyle.JUNCTION }, { DiagramObjectStyle.CB })
        comparatorValidator.validateProperty(DiagramObject::rotation, { DiagramObject(it) }, { 0.0 }, { 1.1 })
        comparatorValidator.validateIndexedCollection(
            DiagramObject::points, DiagramObject::addPoint,
            { DiagramObject(it) }, { DiagramObjectPoint(1.0, 2.0) }, { DiagramObjectPoint(3.0, 4.0) })
    }
}

/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.diagram

import com.zepben.evolve.cim.iec61970.base.diagramlayout.*
import com.zepben.evolve.services.common.BaseServiceComparatorTest
import com.zepben.evolve.utils.ServiceComparatorValidator
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
        comparatorValidator.validateCollection(
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

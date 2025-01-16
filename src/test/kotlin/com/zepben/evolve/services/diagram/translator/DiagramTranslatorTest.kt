/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.diagram.translator

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.database.sqlite.cim.diagram.DiagramDatabaseTables
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.evolve.services.common.translator.TranslatorTestBase
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.DiagramServiceComparator
import com.zepben.evolve.services.diagram.testdata.fillFields

internal class DiagramTranslatorTest : TranslatorTestBase<DiagramService>(
    ::DiagramService,
    DiagramServiceComparator(),
    DiagramDatabaseTables(),
    DiagramService::addFromPb,
    ::diagramIdentifiedObject
) {

    private val dsToPb = DiagramCimToProto()

    override val validationInfo = listOf(
        /************ IEC61970 BASE DIAGRAM LAYOUT ************/
        ValidationInfo(Diagram(), { fillFields(it) }, { addFromPb(dsToPb.toPb(it)) }),
        ValidationInfo(DiagramObject(), { fillFields(it) }, { addFromPb(dsToPb.toPb(it)) })
    )

    override val excludedTables =
        super.excludedTables + setOf(
            // Excluded array data
            TableDiagramObjectPoints::class,
        )

}

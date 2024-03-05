/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.evolve.database.sqlite.common.BaseCIMWriter
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.extensions.setNullableString
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams


class DiagramCIMWriter(databaseTables: DatabaseTables) : BaseCIMWriter(databaseTables) {

    /************ IEC61970 DIAGRAM LAYOUT ************/
    fun save(diagram: Diagram): Boolean {
        val table = databaseTables.getTable<TableDiagrams>()
        val insert = databaseTables.getInsert<TableDiagrams>()

        insert.setNullableString(table.DIAGRAM_STYLE.queryIndex, diagram.diagramStyle.name)
        insert.setNullableString(table.ORIENTATION_KIND.queryIndex, diagram.orientationKind.name)

        return saveIdentifiedObject(table, insert, diagram, "diagram")
    }

    fun save(diagramObject: DiagramObject): Boolean {
        val table = databaseTables.getTable<TableDiagramObjects>()
        val insert = databaseTables.getInsert<TableDiagramObjects>()

        var status = true
        diagramObject.points.forEachIndexed { sequence, point ->
            status = status and saveDiagramObjectPoint(diagramObject, point, sequence)
        }

        insert.setNullableString(table.IDENTIFIED_OBJECT_MRID.queryIndex, diagramObject.identifiedObjectMRID)
        insert.setNullableString(table.DIAGRAM_MRID.queryIndex, diagramObject.diagram?.mRID)
        insert.setNullableString(table.STYLE.queryIndex, diagramObject.style)
        insert.setDouble(table.ROTATION.queryIndex, diagramObject.rotation)

        return status and saveIdentifiedObject(table, insert, diagramObject, "diagram object")
    }

    private fun saveDiagramObjectPoint(diagramObject: DiagramObject, diagramObjectPoint: DiagramObjectPoint, sequenceNumber: Int): Boolean {
        val table = databaseTables.getTable<TableDiagramObjectPoints>()
        val insert = databaseTables.getInsert<TableDiagramObjectPoints>()

        insert.setNullableString(table.DIAGRAM_OBJECT_MRID.queryIndex, diagramObject.mRID)
        insert.setInt(table.SEQUENCE_NUMBER.queryIndex, sequenceNumber)
        insert.setDouble(table.X_POSITION.queryIndex, diagramObjectPoint.xPosition)
        insert.setDouble(table.Y_POSITION.queryIndex, diagramObjectPoint.yPosition)

        return tryExecuteSingleUpdate(
            insert,
            "${diagramObject.mRID}-point$sequenceNumber",
            "diagram object point"
        )
    }
}

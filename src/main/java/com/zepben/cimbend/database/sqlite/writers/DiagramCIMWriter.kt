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
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.cimbend.database.sqlite.DatabaseTables
import com.zepben.cimbend.database.sqlite.extensions.setNullableString
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams


class DiagramCIMWriter(databaseTables: DatabaseTables) : BaseCIMWriter(databaseTables) {

    /************ IEC61970 DIAGRAM LAYOUT ************/
    fun save(diagram: Diagram): Boolean {
        val table = databaseTables.getTable(TableDiagrams::class.java)
        val insert = databaseTables.getInsert(TableDiagrams::class.java)

        insert.setNullableString(table.DIAGRAM_STYLE.queryIndex(), diagram.diagramStyle.name)
        insert.setNullableString(table.ORIENTATION_KIND.queryIndex(), diagram.orientationKind.name)

        return saveIdentifiedObject(table, insert, diagram, "diagram")
    }

    fun save(diagramObject: DiagramObject): Boolean {
        val table = databaseTables.getTable(TableDiagramObjects::class.java)
        val insert = databaseTables.getInsert(TableDiagramObjects::class.java)

        var status = true
        diagramObject.points.forEachIndexed { sequence, point -> status = status and saveDiagramObjectPoint(diagramObject, point, sequence) }

        insert.setNullableString(table.IDENTIFIED_OBJECT_MRID.queryIndex(), diagramObject.identifiedObjectMRID)
        insert.setNullableString(table.DIAGRAM_MRID.queryIndex(), diagramObject.diagram?.mRID)
        insert.setNullableString(table.STYLE.queryIndex(), diagramObject.style.name)
        insert.setDouble(table.ROTATION.queryIndex(), diagramObject.rotation)

        return status and saveIdentifiedObject(table, insert, diagramObject, "diagram object")
    }

    private fun saveDiagramObjectPoint(diagramObject: DiagramObject, diagramObjectPoint: DiagramObjectPoint, sequenceNumber: Int): Boolean {
        val table = databaseTables.getTable(TableDiagramObjectPoints::class.java)
        val insert = databaseTables.getInsert(TableDiagramObjectPoints::class.java)

        insert.setNullableString(table.DIAGRAM_OBJECT_MRID.queryIndex(), diagramObject.mRID)
        insert.setInt(table.SEQUENCE_NUMBER.queryIndex(), sequenceNumber)
        insert.setDouble(table.X_POSITION.queryIndex(), diagramObjectPoint.xPosition)
        insert.setDouble(table.Y_POSITION.queryIndex(), diagramObjectPoint.yPosition)

        return tryExecuteSingleUpdate(
            insert,
            "${diagramObject.mRID}-point$sequenceNumber",
            "Failed to save diagram object point."
        )
    }
}

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
package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.cim.iec61970.base.diagramlayout.*
import com.zepben.cimbend.common.extensions.ensureGet
import com.zepben.cimbend.common.extensions.getOrThrow
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.database.sqlite.extensions.getNullableString
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams
import com.zepben.cimbend.diagram.DiagramService
import java.sql.ResultSet

class DiagramServiceReader(private val diagramService: DiagramService) : BaseServiceReader(diagramService) {

    /************ IEC61970 DIAGRAM LAYOUT ************/
    fun load(table: TableDiagrams, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val diagram = Diagram(setLastMRID(resultSet.getString(table.MRID.queryIndex()))).apply {
            diagramStyle = DiagramStyle.valueOf(resultSet.getString(table.DIAGRAM_STYLE.queryIndex()))
            orientationKind = OrientationKind.valueOf(resultSet.getString(table.ORIENTATION_KIND.queryIndex()))
        }

        return loadIdentifiedObject(diagram, table, resultSet) && diagramService.addOrThrow(diagram)
    }

    fun load(table: TableDiagramObjects, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val mRID = setLastMRID(resultSet.getString(table.MRID.queryIndex()))

        val diagramObject = DiagramObject(mRID).apply {
            diagram = diagramService.ensureGet(resultSet.getString(table.DIAGRAM_MRID.queryIndex()), typeNameAndMRID())
            diagram?.addDiagramObject(this)

            identifiedObjectMRID = resultSet.getNullableString(table.IDENTIFIED_OBJECT_MRID.queryIndex())
            style = DiagramObjectStyle.valueOf(resultSet.getString(table.STYLE.queryIndex()))
            rotation = resultSet.getDouble(table.ROTATION.queryIndex())
        }

        return loadIdentifiedObject(diagramObject, table, resultSet) && diagramService.addOrThrow(diagramObject)
    }

    fun load(table: TableDiagramObjectPoints, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val diagramObjectMRID = setLastMRID(resultSet.getString(table.DIAGRAM_OBJECT_MRID.queryIndex()))
        val sequenceNumber = resultSet.getInt(table.SEQUENCE_NUMBER.queryIndex())

        setLastMRID("$diagramObjectMRID-point$sequenceNumber")

        diagramService.getOrThrow<DiagramObject>(diagramObjectMRID, "DiagramObjectPoint $sequenceNumber")
            .addPoint(
                DiagramObjectPoint(resultSet.getDouble(table.X_POSITION.queryIndex()), resultSet.getDouble(table.Y_POSITION.queryIndex())),
                sequenceNumber
            )

        return true
    }
}

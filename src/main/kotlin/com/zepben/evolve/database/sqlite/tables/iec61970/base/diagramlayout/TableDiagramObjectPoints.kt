/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableDiagramObjectPoints : SqliteTable() {

    val DIAGRAM_OBJECT_MRID: Column = Column(++columnIndex, "diagram_object_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", "TEXT", NOT_NULL)
    val X_POSITION: Column = Column(++columnIndex, "x_position", "TEXT", NULL)
    val Y_POSITION: Column = Column(++columnIndex, "y_position", "TEXT", NULL)

    override fun name(): String {
        return "diagram_object_points"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(DIAGRAM_OBJECT_MRID, SEQUENCE_NUMBER))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(DIAGRAM_OBJECT_MRID))

        return cols
    }

    override val tableClass: Class<TableDiagramObjectPoints> = this.javaClass
    override val tableClassInstance: TableDiagramObjectPoints = this

}

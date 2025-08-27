/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.diagramlayout

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
class TableDiagramObjectPoints : SqliteTable() {

    val DIAGRAM_OBJECT_MRID: Column = Column(++columnIndex, "diagram_object_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val X_POSITION: Column = Column(++columnIndex, "x_position", "NUMBER", NOT_NULL)
    val Y_POSITION: Column = Column(++columnIndex, "y_position", "NUMBER", NOT_NULL)

    override val name: String = "diagram_object_points"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(DIAGRAM_OBJECT_MRID, SEQUENCE_NUMBER))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(DIAGRAM_OBJECT_MRID))
        }

}

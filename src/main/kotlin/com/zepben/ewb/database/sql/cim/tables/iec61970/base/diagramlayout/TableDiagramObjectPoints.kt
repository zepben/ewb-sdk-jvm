/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.diagramlayout

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `DiagramObjectPoint` columns required for the database table.
 *
 * @property DIAGRAM_OBJECT_MRID The diagram object this point is for.
 * @property SEQUENCE_NUMBER The order of this point.
 * @property X_POSITION The X coordinate of this point.
 * @property Y_POSITION The Y coordinate of this point.
 */
@Suppress("PropertyName")
class TableDiagramObjectPoints : SqlTable() {

    val DIAGRAM_OBJECT_MRID: Column = Column(++columnIndex, "diagram_object_mrid", Column.Type.STRING, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)
    val X_POSITION: Column = Column(++columnIndex, "x_position", Column.Type.DOUBLE, NOT_NULL)
    val Y_POSITION: Column = Column(++columnIndex, "y_position", Column.Type.DOUBLE, NOT_NULL)

    override val name: String = "diagram_object_points"

    init {
        addUniqueIndexes(
            listOf(DIAGRAM_OBJECT_MRID, SEQUENCE_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(DIAGRAM_OBJECT_MRID)
        )
    }

}

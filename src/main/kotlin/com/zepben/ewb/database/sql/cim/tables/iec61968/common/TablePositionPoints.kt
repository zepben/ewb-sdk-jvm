/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `PositionPoint` columns required for the database table.
 *
 * @property LOCATION_MRID The location this position point is for.
 * @property SEQUENCE_NUMBER The order of this position point.
 * @property X_POSITION X axis position.
 * @property Y_POSITION Y axis position.
 */
@Suppress("PropertyName")
class TablePositionPoints : SqlTable() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", Column.Type.STRING, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)
    val X_POSITION: Column = Column(++columnIndex, "x_position", Column.Type.DOUBLE, NOT_NULL)
    val Y_POSITION: Column = Column(++columnIndex, "y_position", Column.Type.DOUBLE, NOT_NULL)

    override val name: String = "position_points"

    init {
        addUniqueIndexes(
            listOf(LOCATION_MRID, SEQUENCE_NUMBER)
        )
    }

}

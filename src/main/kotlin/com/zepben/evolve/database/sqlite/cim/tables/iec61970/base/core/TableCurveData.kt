/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable

/**
 * A class representing the CurveData columns required for the database table.
 *
 * @property CURVE_MRID A column storing the curve mRID.
 * @property X_VALUE A column storing the xValue of this curve data point.
 * @property Y1_VALUE A column storing the y1Value of this curve data point.
 * @property Y2_VALUE A column storing the y2Value of this curve data point.
 * @property Y3_VALUE A column storing the y3Value of this curve data point.
 */
@Suppress("PropertyName")
class TableCurveData : SqliteTable() {

    val CURVE_MRID: Column = Column(++columnIndex, "curve_mrid", "TEXT", NOT_NULL)
    val X_VALUE: Column = Column(++columnIndex, "x_value", "NUMBER", NOT_NULL)
    val Y1_VALUE: Column = Column(++columnIndex, "y1_value", "NUMBER", NOT_NULL)
    val Y2_VALUE: Column = Column(++columnIndex, "y2_value", "NUMBER", NULL)
    val Y3_VALUE: Column = Column(++columnIndex, "y3_value", "NUMBER", NULL)

    override val name: String = "curve_data"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(CURVE_MRID, X_VALUE))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(CURVE_MRID))
        }
}

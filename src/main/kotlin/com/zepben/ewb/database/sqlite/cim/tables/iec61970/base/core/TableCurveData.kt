/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

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

    val CURVE_MRID: Column = Column(++columnIndex, "curve_mrid", Column.Type.STRING, NOT_NULL)
    val X_VALUE: Column = Column(++columnIndex, "x_value", Column.Type.DOUBLE, NOT_NULL)
    val Y1_VALUE: Column = Column(++columnIndex, "y1_value", Column.Type.DOUBLE, NOT_NULL)
    val Y2_VALUE: Column = Column(++columnIndex, "y2_value", Column.Type.DOUBLE, NULL)
    val Y3_VALUE: Column = Column(++columnIndex, "y3_value", Column.Type.DOUBLE, NULL)

    override val name: String = "curve_data"

    init {
        addUniqueIndexes(
            listOf(CURVE_MRID, X_VALUE)
        )

        addNonUniqueIndexes(
            listOf(CURVE_MRID)
        )
    }

}

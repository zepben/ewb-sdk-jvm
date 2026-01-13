/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the CurveData columns required for the database table.
 *
 * @property CURVE_MRID The curve mRID.
 * @property X_VALUE The xValue of this curve data point.
 * @property Y1_VALUE The y1Value of this curve data point.
 * @property Y2_VALUE The y2Value of this curve data point.
 * @property Y3_VALUE The y3Value of this curve data point.
 */
@Suppress("PropertyName")
class TableCurveData : SqlTable() {

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

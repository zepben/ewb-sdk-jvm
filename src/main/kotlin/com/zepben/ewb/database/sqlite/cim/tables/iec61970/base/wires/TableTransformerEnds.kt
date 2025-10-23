/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
abstract class TableTransformerEnds : TableIdentifiedObjects() {

    val END_NUMBER: Column = Column(++columnIndex, "end_number", Column.Type.INTEGER, NOT_NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)
    val BASE_VOLTAGE_MRID: Column = Column(++columnIndex, "base_voltage_mrid", Column.Type.STRING, NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", Column.Type.BOOLEAN, NULL)
    val R_GROUND: Column = Column(++columnIndex, "r_ground", Column.Type.DOUBLE, NULL)
    val X_GROUND: Column = Column(++columnIndex, "x_ground", Column.Type.DOUBLE, NULL)
    val STAR_IMPEDANCE_MRID: Column = Column(++columnIndex, "star_impedance_mrid", Column.Type.STRING, NULL)

    init {
        addNonUniqueIndexes(
            listOf(STAR_IMPEDANCE_MRID)
        )
    }

}

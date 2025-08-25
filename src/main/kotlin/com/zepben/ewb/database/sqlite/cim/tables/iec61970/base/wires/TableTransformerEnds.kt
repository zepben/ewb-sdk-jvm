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

    val END_NUMBER: Column = Column(++columnIndex, "end_number", "INTEGER", NOT_NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", "TEXT", NULL)
    val BASE_VOLTAGE_MRID: Column = Column(++columnIndex, "base_voltage_mrid", "TEXT", NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", "BOOLEAN", NULL)
    val R_GROUND: Column = Column(++columnIndex, "r_ground", "NUMBER", NULL)
    val X_GROUND: Column = Column(++columnIndex, "x_ground", "NUMBER", NULL)
    val STAR_IMPEDANCE_MRID: Column = Column(++columnIndex, "star_impedance_mrid", "TEXT", NULL)

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(STAR_IMPEDANCE_MRID))
        }

}

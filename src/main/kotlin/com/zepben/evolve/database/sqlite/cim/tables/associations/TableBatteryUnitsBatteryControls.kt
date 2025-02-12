/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.associations

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.common.SqliteTable

/**
 * A class representing the association between BatteryUnits and BatteryControls
 *
 * @property BATTERY_UNIT_MRID A column storing the mRID of BatteryUnits.
 * @property BATTERY_CONTROL_MRID A column storing the mRID of BatteryControls.
 */
@Suppress("PropertyName")
class TableBatteryUnitsBatteryControls : SqliteTable() {

    val BATTERY_UNIT_MRID: Column = Column(++columnIndex, "battery_unit_mrid", "TEXT", NOT_NULL)
    val BATTERY_CONTROL_MRID: Column = Column(++columnIndex, "battery_control_mrid", "TEXT", NOT_NULL)

    override val name: String = "battery_units_battery_controls"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(BATTERY_UNIT_MRID, BATTERY_CONTROL_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(BATTERY_UNIT_MRID))
            add(listOf(BATTERY_CONTROL_MRID))
        }

}

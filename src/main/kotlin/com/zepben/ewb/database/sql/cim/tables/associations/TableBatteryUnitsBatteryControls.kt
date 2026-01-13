/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between BatteryUnits and BatteryControls
 *
 * @property BATTERY_UNIT_MRID The mRID of BatteryUnits.
 * @property BATTERY_CONTROL_MRID The mRID of BatteryControls.
 */
@Suppress("PropertyName")
class TableBatteryUnitsBatteryControls : SqlTable() {

    val BATTERY_UNIT_MRID: Column = Column(++columnIndex, "battery_unit_mrid", Column.Type.STRING, NOT_NULL)
    val BATTERY_CONTROL_MRID: Column = Column(++columnIndex, "battery_control_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "battery_units_battery_controls"

    init {
        addUniqueIndexes(
            listOf(BATTERY_UNIT_MRID, BATTERY_CONTROL_MRID)
        )

        addNonUniqueIndexes(
            listOf(BATTERY_UNIT_MRID),
            listOf(BATTERY_CONTROL_MRID)
        )
    }

}

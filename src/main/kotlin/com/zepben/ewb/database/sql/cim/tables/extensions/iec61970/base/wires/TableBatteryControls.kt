/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires.TableRegulatingControls
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `BatteryControl` columns required for the database table.
 *
 * @property CHARGING_RATE Charging rate (input power) in percentage of maxP. (Unit: PerCent)
 * @property DISCHARGING_RATE Discharge rate (output power) in percentage of maxP. (Unit: PerCent)
 * @property RESERVE_PERCENT Percentage of the rated storage capacity that should be reserved during normal operations. This reserve acts as a safeguard, preventing the energy level from
 * dropping below this threshold under standard conditions. The field must be set to a non-negative value between 0 and 1. (Unit: PerCent)
 * @property CONTROL_MODE Mode of operation for the dispatch (charging/discharging) function of BatteryControl.
 */
@Suppress("PropertyName")
class TableBatteryControls : TableRegulatingControls() {

    val CHARGING_RATE: Column = Column(++columnIndex, "charging_rate", Column.Type.DOUBLE, NULL)
    val DISCHARGING_RATE: Column = Column(++columnIndex, "discharging_rate", Column.Type.DOUBLE, NULL)
    val RESERVE_PERCENT: Column = Column(++columnIndex, "reserve_percent", Column.Type.DOUBLE, NULL)
    val CONTROL_MODE: Column = Column(++columnIndex, "control_mode", Column.Type.STRING, NOT_NULL)

    override val name: String = "battery_controls"

}

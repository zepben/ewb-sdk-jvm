/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.extensions.iec61970.base.wires

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires.TableRegulatingControls

@Suppress("PropertyName")
class TableBatteryControls : TableRegulatingControls() {

    val CHARGING_RATE: Column = Column(++columnIndex, "charging_rate", "NUMBER", NULL)
    val DISCHARGING_RATE: Column = Column(++columnIndex, "discharging_rate", "NUMBER", NULL)
    val RESERVE_PERCENT: Column = Column(++columnIndex, "reserve_percent", "NUMBER", NULL)
    val CONTROL_MODE: Column = Column(++columnIndex, "control_mode", "TEXT", NOT_NULL)

    override val name: String = "battery_controls"

}

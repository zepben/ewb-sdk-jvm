/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.generation.production

import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `BatteryUnit` columns required for the database table.
 *
 * @property BATTERY_STATE The current state of the battery (charging, full, etc.).
 * @property RATED_E Full energy storage capacity of the battery in watt-hours (Wh). The attribute shall be a positive value.
 * @property STORED_E Amount of energy currently stored in watt-hours (Wh). The attribute shall be a positive value or zero and lower than [BatteryUnit.ratedE].
 */
@Suppress("PropertyName")
class TableBatteryUnits : TablePowerElectronicsUnits() {

    val BATTERY_STATE: Column = Column(++columnIndex, "battery_state", Column.Type.STRING, NOT_NULL)
    val RATED_E: Column = Column(++columnIndex, "rated_e", Column.Type.INTEGER, NULL)
    val STORED_E: Column = Column(++columnIndex, "stored_e", Column.Type.INTEGER, NULL)

    override val name: String = "battery_units"

}

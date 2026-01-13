/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `RatioTapChanger` columns required for the database table.
 *
 * @property TRANSFORMER_END_MRID The transformer end to which this ratio tap changer belongs.
 * @property STEP_VOLTAGE_INCREMENT The tap step increment, in per cent of neutral voltage, per step position.
 */
@Suppress("PropertyName")
class TableRatioTapChangers : TableTapChangers() {

    val TRANSFORMER_END_MRID: Column = Column(++columnIndex, "transformer_end_mrid", Column.Type.STRING, NULL)
    val STEP_VOLTAGE_INCREMENT: Column = Column(++columnIndex, "step_voltage_increment", Column.Type.DOUBLE, NULL)

    override val name: String = "ratio_tap_changers"

    init {
        addUniqueIndexes(
            listOf(TRANSFORMER_END_MRID)
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

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

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableNoLoadTests : TableTransformerTest() {

    val ENERGISED_END_VOLTAGE: Column = Column(++columnIndex, "energised_end_voltage", Column.Type.INTEGER, NULL)
    val EXCITING_CURRENT: Column = Column(++columnIndex, "exciting_current", Column.Type.DOUBLE, NULL)
    val EXCITING_CURRENT_ZERO: Column = Column(++columnIndex, "exciting_current_zero", Column.Type.DOUBLE, NULL)
    val LOSS: Column = Column(++columnIndex, "loss", Column.Type.INTEGER, NULL)
    val LOSS_ZERO: Column = Column(++columnIndex, "loss_zero", Column.Type.INTEGER, NULL)

    override val name: String = "no_load_tests"

}

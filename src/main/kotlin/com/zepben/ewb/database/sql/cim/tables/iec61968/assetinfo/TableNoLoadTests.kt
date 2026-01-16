/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `NoLoadTest` columns required for the database table.
 *
 * @property ENERGISED_END_VOLTAGE Voltage applied to the winding (end) during test in volts.
 * @property EXCITING_CURRENT Exciting current measured from a positive-sequence or single-phase excitation test as a percentage.
 * @property EXCITING_CURRENT_ZERO Exciting current measured from a zero-sequence open-circuit excitation test as a percentage.
 * @property LOSS Losses measured from a positive-sequence or single-phase excitation test in watts.
 * @property LOSS_ZERO Losses measured from a zero-sequence excitation test in watts.
 */
@Suppress("PropertyName")
class TableNoLoadTests : TableTransformerTests() {

    val ENERGISED_END_VOLTAGE: Column = Column(++columnIndex, "energised_end_voltage", Column.Type.INTEGER, NULL)
    val EXCITING_CURRENT: Column = Column(++columnIndex, "exciting_current", Column.Type.DOUBLE, NULL)
    val EXCITING_CURRENT_ZERO: Column = Column(++columnIndex, "exciting_current_zero", Column.Type.DOUBLE, NULL)
    val LOSS: Column = Column(++columnIndex, "loss", Column.Type.INTEGER, NULL)
    val LOSS_ZERO: Column = Column(++columnIndex, "loss_zero", Column.Type.INTEGER, NULL)

    override val name: String = "no_load_tests"

}

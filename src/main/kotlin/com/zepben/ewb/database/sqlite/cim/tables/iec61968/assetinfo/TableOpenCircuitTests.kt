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
class TableOpenCircuitTests : TableTransformerTest() {

    val ENERGISED_END_STEP: Column = Column(++columnIndex, "energised_end_step", Column.Type.INTEGER, NULL)
    val ENERGISED_END_VOLTAGE: Column = Column(++columnIndex, "energised_end_voltage", Column.Type.INTEGER, NULL)
    val OPEN_END_STEP: Column = Column(++columnIndex, "open_end_step", Column.Type.INTEGER, NULL)
    val OPEN_END_VOLTAGE: Column = Column(++columnIndex, "open_end_voltage", Column.Type.INTEGER, NULL)
    val PHASE_SHIFT: Column = Column(++columnIndex, "phase_shift", Column.Type.DOUBLE, NULL)

    override val name: String = "open_circuit_tests"

}

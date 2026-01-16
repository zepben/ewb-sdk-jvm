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
 * A class representing the `OpenCircuitTest` columns required for the database table.
 *
 * @property ENERGISED_END_STEP The tap step number for the energised end of the test pair.
 * @property ENERGISED_END_VOLTAGE The voltage applied to the winding (end) during test in volts.
 * @property OPEN_END_STEP The tap step number for the open end of the test pair.
 * @property OPEN_END_VOLTAGE The voltage measured at the open-circuited end, with the energised end set to rated voltage and all other ends open in volts.
 * @property PHASE_SHIFT The phase shift measured at the open end with the energised end set to rated voltage and all other ends open in angle degrees.
 */
@Suppress("PropertyName")
class TableOpenCircuitTests : TableTransformerTests() {

    val ENERGISED_END_STEP: Column = Column(++columnIndex, "energised_end_step", Column.Type.INTEGER, NULL)
    val ENERGISED_END_VOLTAGE: Column = Column(++columnIndex, "energised_end_voltage", Column.Type.INTEGER, NULL)
    val OPEN_END_STEP: Column = Column(++columnIndex, "open_end_step", Column.Type.INTEGER, NULL)
    val OPEN_END_VOLTAGE: Column = Column(++columnIndex, "open_end_voltage", Column.Type.INTEGER, NULL)
    val PHASE_SHIFT: Column = Column(++columnIndex, "phase_shift", Column.Type.DOUBLE, NULL)

    override val name: String = "open_circuit_tests"

}

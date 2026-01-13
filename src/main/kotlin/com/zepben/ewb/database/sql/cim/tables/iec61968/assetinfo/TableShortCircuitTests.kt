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
 * A class representing the `ShortCircuitTest` columns required for the database table.
 *
 * @property CURRENT The short circuit current in amps.
 * @property ENERGISED_END_STEP The tap step number for the energised end of the test pair.
 * @property GROUNDED_END_STEP The tap step number for the grounded end of the test pair.
 * @property LEAKAGE_IMPEDANCE The leakage impedance measured from a positive-sequence or single-phase short-circuit test in ohms.
 * @property LEAKAGE_IMPEDANCE_ZERO The leakage impedance measured from a zero-sequence short-circuit test in ohms.
 * @property LOSS The load losses from a positive-sequence or single-phase short-circuit test in watts.
 * @property LOSS_ZERO The load losses from a zero-sequence short-circuit test in watts.
 * @property POWER The short circuit apparent power in VA.
 * @property VOLTAGE The short circuit voltage as a percentage.
 * @property VOLTAGE_OHMIC_PART The short Circuit Voltage – Ohmic Part as a percentage.
 */
@Suppress("PropertyName")
class TableShortCircuitTests : TableTransformerTests() {

    val CURRENT: Column = Column(++columnIndex, "current", Column.Type.DOUBLE, NULL)
    val ENERGISED_END_STEP: Column = Column(++columnIndex, "energised_end_step", Column.Type.INTEGER, NULL)
    val GROUNDED_END_STEP: Column = Column(++columnIndex, "grounded_end_step", Column.Type.INTEGER, NULL)
    val LEAKAGE_IMPEDANCE: Column = Column(++columnIndex, "leakage_impedance", Column.Type.DOUBLE, NULL)
    val LEAKAGE_IMPEDANCE_ZERO: Column = Column(++columnIndex, "leakage_impedance_zero", Column.Type.DOUBLE, NULL)
    val LOSS: Column = Column(++columnIndex, "loss", Column.Type.INTEGER, NULL)
    val LOSS_ZERO: Column = Column(++columnIndex, "loss_zero", Column.Type.INTEGER, NULL)
    val POWER: Column = Column(++columnIndex, "power", Column.Type.INTEGER, NULL)
    val VOLTAGE: Column = Column(++columnIndex, "voltage", Column.Type.DOUBLE, NULL)
    val VOLTAGE_OHMIC_PART: Column = Column(++columnIndex, "voltage_ohmic_part", Column.Type.DOUBLE, NULL)

    override val name: String = "short_circuit_tests"

}

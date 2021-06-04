/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableShortCircuitTests : TableTransformerTest() {

    val CURRENT = Column(++columnIndex, "current", "NUMBER", NOT_NULL)
    val ENERGISED_END_STEP = Column(++columnIndex, "energised_end_step", "INTEGER", NOT_NULL)
    val GROUNDED_END_STEP = Column(++columnIndex, "grounded_end_step", "INTEGER", NOT_NULL)
    val LEAKAGE_IMPEDANCE = Column(++columnIndex, "leakage_impedance", "NUMBER", NOT_NULL)
    val LEAKAGE_IMPEDANCE_ZERO = Column(++columnIndex, "leakage_impedance_zero", "NUMBER", NOT_NULL)
    val LOSS = Column(++columnIndex, "loss", "INTEGER", NOT_NULL)
    val LOSS_ZERO = Column(++columnIndex, "loss_zero", "INTEGER", NOT_NULL)
    val POWER = Column(++columnIndex, "power", "INTEGER", NOT_NULL)
    val VOLTAGE = Column(++columnIndex, "voltage", "NUMBER", NOT_NULL)
    val VOLTAGE_OHMIC_PART = Column(++columnIndex, "voltage_ohmic_part", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "short_circuit_tests"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

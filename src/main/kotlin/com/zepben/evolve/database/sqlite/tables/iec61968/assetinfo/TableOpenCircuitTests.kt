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
class TableOpenCircuitTests : TableTransformerTest() {

    val ENERGISED_END_STEP = Column(++columnIndex, "energised_end_step", "INTEGER", NOT_NULL)
    val ENERGISED_END_VOLTAGE = Column(++columnIndex, "energised_end_voltage", "INTEGER", NOT_NULL)
    val OPEN_END_STEP = Column(++columnIndex, "open_end_step", "INTEGER", NOT_NULL)
    val OPEN_END_VOLTAGE = Column(++columnIndex, "open_end_voltage", "INTEGER", NOT_NULL)
    val PHASE_SHIFT = Column(++columnIndex, "phase_shift", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "open_circuit_tests"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

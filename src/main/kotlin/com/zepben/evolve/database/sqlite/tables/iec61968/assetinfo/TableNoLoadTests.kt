/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableNoLoadTests : TableTransformerTest() {

    val ENERGISED_END_VOLTAGE = Column(++columnIndex, "energised_end_voltage", "INTEGER", NULL)
    val EXCITING_CURRENT = Column(++columnIndex, "exciting_current", "NUMBER", NULL)
    val EXCITING_CURRENT_ZERO = Column(++columnIndex, "exciting_current_zero", "NUMBER", NULL)
    val LOSS = Column(++columnIndex, "loss", "INTEGER", NULL)
    val LOSS_ZERO = Column(++columnIndex, "loss_zero", "INTEGER", NULL)

    override fun name(): String {
        return "no_load_tests"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

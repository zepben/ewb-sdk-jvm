/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL

@Suppress("PropertyName")
class TableRatioTapChangers : TableTapChangers() {

    val TRANSFORMER_END_MRID = Column(++columnIndex, "transformer_end_mrid", "TEXT", NULL)
    val STEP_VOLTAGE_INCREMENT = Column(++columnIndex, "step_voltage_increment", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "ratio_tap_changers"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(TRANSFORMER_END_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

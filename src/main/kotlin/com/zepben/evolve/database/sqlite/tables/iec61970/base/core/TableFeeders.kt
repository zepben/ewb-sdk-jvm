/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.core

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableFeeders : TableEquipmentContainers() {

    val NORMAL_HEAD_TERMINAL_MRID: Column = Column(++columnIndex, "normal_head_terminal_mrid", "TEXT", NULL)
    val NORMAL_ENERGIZING_SUBSTATION_MRID: Column =
        Column(++columnIndex, "normal_energizing_substation_mrid", "TEXT", NULL)

    override fun name(): String {
        return "feeders"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(NORMAL_ENERGIZING_SUBSTATION_MRID))

        return cols
    }

    override val tableClass: Class<TableFeeders> = this.javaClass
    override val tableClassInstance: TableFeeders = this

}

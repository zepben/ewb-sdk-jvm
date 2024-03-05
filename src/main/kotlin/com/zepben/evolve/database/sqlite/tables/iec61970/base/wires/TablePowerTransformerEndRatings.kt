/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TablePowerTransformerEndRatings : SqliteTable() {

    val POWER_TRANSFORMER_END_MRID: Column = Column(++columnIndex, "power_transformer_end_mrid", "TEXT", NULL)
    val COOLING_TYPE: Column = Column(++columnIndex, "cooling_type", "TEXT", NOT_NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", "INTEGER", NOT_NULL)

    override fun name(): String {
        return "power_transformer_end_ratings"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(POWER_TRANSFORMER_END_MRID, COOLING_TYPE))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(POWER_TRANSFORMER_END_MRID))

        return cols
    }

    override val tableClass: Class<TablePowerTransformerEndRatings> = this.javaClass
    override val tableClassInstance: TablePowerTransformerEndRatings = this

}

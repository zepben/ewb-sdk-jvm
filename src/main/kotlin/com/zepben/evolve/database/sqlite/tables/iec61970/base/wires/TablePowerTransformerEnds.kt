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

@Suppress("PropertyName")
class TablePowerTransformerEnds : TableTransformerEnds() {

    val POWER_TRANSFORMER_MRID: Column = Column(++columnIndex, "power_transformer_mrid", "TEXT", NULL)
    val CONNECTION_KIND: Column = Column(++columnIndex, "connection_kind", "TEXT", NOT_NULL)
    val PHASE_ANGLE_CLOCK: Column = Column(++columnIndex, "phase_angle_clock", "INTEGER", NULL)
    val B: Column = Column(++columnIndex, "b", "NUMBER", NULL)
    val B0: Column = Column(++columnIndex, "b0", "NUMBER", NULL)
    val G: Column = Column(++columnIndex, "g", "NUMBER", NULL)
    val G0: Column = Column(++columnIndex, "g0", "NUMBER", NULL)
    val R: Column = Column(++columnIndex, "R", "NUMBER", NULL)
    val R0: Column = Column(++columnIndex, "R0", "NUMBER", NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", "INTEGER", NULL)
    val X: Column = Column(++columnIndex, "X", "NUMBER", NULL)
    val X0: Column = Column(++columnIndex, "X0", "NUMBER", NULL)

    override fun name(): String {
        return "power_transformer_ends"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(POWER_TRANSFORMER_MRID, END_NUMBER))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(POWER_TRANSFORMER_MRID))

        return cols
    }

    override val tableClass: Class<TablePowerTransformerEnds> = this.javaClass
    override val tableClassInstance: TablePowerTransformerEnds = this

}

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

    val POWER_TRANSFORMER_MRID = Column(++columnIndex, "power_transformer_mrid", "TEXT", NULL)
    val CONNECTION_KIND = Column(++columnIndex, "connection_kind", "TEXT", NOT_NULL)
    val PHASE_ANGLE_CLOCK = Column(++columnIndex, "phase_angle_clock", "INTEGER", NOT_NULL)
    val B = Column(++columnIndex, "b", "NUMBER", NOT_NULL)
    val B0 = Column(++columnIndex, "b0", "NUMBER", NOT_NULL)
    val G = Column(++columnIndex, "g", "NUMBER", NOT_NULL)
    val G0 = Column(++columnIndex, "g0", "NUMBER", NOT_NULL)
    val R = Column(++columnIndex, "R", "NUMBER", NULL)
    val R0 = Column(++columnIndex, "R0", "NUMBER", NULL)
    val RATED_S = Column(++columnIndex, "rated_s", "INTEGER", NOT_NULL)
    val RATED_U = Column(++columnIndex, "rated_u", "INTEGER", NOT_NULL)
    val X = Column(++columnIndex, "X", "NUMBER", NULL)
    val X0 = Column(++columnIndex, "X0", "NUMBER", NULL)

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

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

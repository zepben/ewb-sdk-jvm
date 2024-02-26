/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableTransformerStarImpedance : TableIdentifiedObjects() {

    val R: Column = Column(++columnIndex, "R", "NUMBER", NULL)
    val R0: Column = Column(++columnIndex, "R0", "NUMBER", NULL)
    val X: Column = Column(++columnIndex, "X", "NUMBER", NULL)
    val X0: Column = Column(++columnIndex, "X0", "NUMBER", NULL)
    val TRANSFORMER_END_INFO_MRID: Column = Column(++columnIndex, "transformer_end_info_mrid", "TEXT", NULL)

    override fun name(): String {
        return "transformer_star_impedance"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(TRANSFORMER_END_INFO_MRID))

        return cols
    }

    override val tableClass: Class<TableTransformerStarImpedance> = this.javaClass
    override val tableClassInstance: TableTransformerStarImpedance = this

}

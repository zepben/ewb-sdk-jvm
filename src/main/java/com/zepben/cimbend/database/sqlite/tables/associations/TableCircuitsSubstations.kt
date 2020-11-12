/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.associations

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableCircuitsSubstations : SqliteTable() {

    val CIRCUIT_MRID = Column(++columnIndex, "circuit_mrid", "TEXT", NOT_NULL)
    val SUBSTATION_MRID = Column(++columnIndex, "substation_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "circuits_substations"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(CIRCUIT_MRID, SUBSTATION_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(CIRCUIT_MRID))
        cols.add(listOf(SUBSTATION_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

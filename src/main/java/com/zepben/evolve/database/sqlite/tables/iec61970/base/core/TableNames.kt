/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.core

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import java.util.*

@Suppress("PropertyName")
class TableNames : SqliteTable() {

    val NAME = Column(++columnIndex, "name", "TEXT", NOT_NULL)
    val IDENTIFIED_OBJECT_MRID = Column(++columnIndex, "identified_object_mrid", "TEXT", NOT_NULL)
    val NAME_TYPE_NAME = Column(++columnIndex, "name_type_name", "TEXT", NOT_NULL)

    override fun name(): String {
        return "names"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols: MutableList<List<Column>> = ArrayList()

        cols.add(listOf(IDENTIFIED_OBJECT_MRID, NAME_TYPE_NAME, NAME))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(IDENTIFIED_OBJECT_MRID))
        cols.add(listOf(NAME))
        cols.add(listOf(NAME_TYPE_NAME))

        return cols
    }

}

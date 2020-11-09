/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.SqliteTable
import java.util.*

@Suppress("PropertyName")
abstract class TableIdentifiedObjects : SqliteTable() {

    val MRID = Column(++columnIndex, "mrid", "TEXT", NOT_NULL)
    val NAME = Column(++columnIndex, "name", "TEXT", NOT_NULL)
    val DESCRIPTION = Column(++columnIndex, "description", "TEXT", NOT_NULL)
    val NUM_DIAGRAM_OBJECTS = Column(++columnIndex, "num_diagram_objects", "INTEGER", NOT_NULL)

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols: MutableList<List<Column>> = ArrayList()

        cols.add(listOf(MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(NAME))

        return cols
    }

}

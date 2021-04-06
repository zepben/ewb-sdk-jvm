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
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableNameTypes : SqliteTable() {

    val NAME = Column(++columnIndex, "name", "TEXT", NOT_NULL)
    val DESCRIPTION = Column(++columnIndex, "description", "TEXT", NULL)

    override fun name(): String {
        return "name_types"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

    override fun uniqueIndexColumns(): MutableList<List<Column>> = mutableListOf(
        listOf(NAME)
    )

}

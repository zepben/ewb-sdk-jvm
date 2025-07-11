/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.SqlTable

/**
 * Represents a table in a Sqlite Database
 */
abstract class SqliteTable internal constructor() : SqlTable() {

    override val createTableSql: String by lazy { "CREATE TABLE $name (${columnSet.joinToString { it.sqlString() }})" }

    override val createIndexesSql: Collection<String> by lazy { uniqueIndexColumns.toCreateIndexSql(true) + nonUniqueIndexColumns.toCreateIndexSql(false) }

    private fun MutableList<List<Column>>.toCreateIndexSql(isUnique: Boolean): List<String> =
        map { indexCols ->
            "CREATE ${if (isUnique) "UNIQUE " else ""}INDEX " +
                "${name}_${indexCols.joinToString("_") { it.name }} " +
                "ON $name (${indexCols.joinToString { it.name }})"
        }

    private fun Column.sqlString(): String = (name + " " + type + " " + nullable.sqlString()).trim { it <= ' ' }
    private fun Column.Nullable.sqlString(): String = if (this == Column.Nullable.NULL) "NULL" else if (this == Column.Nullable.NOT_NULL) "NOT NULL" else ""

}

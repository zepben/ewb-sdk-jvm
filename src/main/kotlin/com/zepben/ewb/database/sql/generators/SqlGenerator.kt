/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.generators

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * Represents a table in a Postgres Database
 *
 * @param formatType A selector to convert a [Column.Type] into its correct database format.
 * @param formatNullable A selector to convert a [Column.Nullable] into its correct database format.
 */
abstract class SqlGenerator(
    private val formatType: (Column.Type) -> String,
    private val formatNullable: (Column.Nullable) -> String,
) {

    /**
     * Generate the SQL statement that should be executed to create the table in the database.
     * @param table The table being created.
     */
    fun createTableSql(table: SqlTable): String =
        "CREATE TABLE ${table.name} (${table.columnSet.joinToString { it.sqlString() }})"

    /**
     * Generate the SQL statements that should be executed to create the indexes for the table in the database.
     * @param table The table being created.
     */
    fun createIndexesSql(table: SqlTable): Collection<String> =
        table.uniqueIndexColumns.toCreateIndexSql(table, true) +
            table.nonUniqueIndexColumns.toCreateIndexSql(table, false)

    private fun List<List<Column>>.toCreateIndexSql(table: SqlTable, isUnique: Boolean): List<String> =
        map { indexCols ->
            "CREATE ${if (isUnique) "UNIQUE " else ""}INDEX " +
                "${table.name}_${indexCols.joinToString("_") { it.name }} " +
                "ON ${table.name} (${indexCols.joinToString { it.name }})"
        }

    private fun Column.sqlString(): String = "$name ${formatType(type)} ${formatNullable(nullable)}"


}

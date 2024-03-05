/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.evolve.database.sqlite.tables.associations.*
import com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.*
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.*
import java.sql.Connection
import java.sql.PreparedStatement
import kotlin.reflect.KClass

abstract class DatabaseTables {

    val tables: Map<KClass<out SqliteTable>, SqliteTable> by lazy { includedTables.associateBy { it::class } }
    val insertStatements = mutableMapOf<KClass<out SqliteTable>, PreparedStatement>()

    abstract val includedTables: Sequence<SqliteTable>

    /**
     * Helper function for getting the table of the specified type.
     *
     * @param T The type of table to get.
     * @return The requested table if it belongs to this database.
     * @throws MissingTableConfigException If the requested table doesn't belong to this database.
     */
    @Throws(MissingTableConfigException::class)
    inline fun <reified T : SqliteTable> getTable(): T {
        val table = tables[T::class]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No table has been registered for ${T::class.simpleName}. You might want to consider fixing that.")

        return table as T
    }

    @Throws(MissingTableConfigException::class)
    inline fun <reified T : SqliteTable> getInsert(): PreparedStatement {
        return insertStatements[T::class]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No prepared statement has been registered for ${T::class.simpleName}. You might want to consider fixing that.")
    }

    fun forEachTable(action: (SqliteTable) -> Unit) {
        tables.values.forEach(action)
    }

    fun prepareInsertStatements(connection: Connection) {
        insertStatements.clear()
        for ((key, value) in tables)
            insertStatements[key] = connection.prepareStatement(value.preparedInsertSql)
    }

}

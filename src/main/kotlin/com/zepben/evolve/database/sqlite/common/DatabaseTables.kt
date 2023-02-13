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

abstract class DatabaseTables {

    /**
     * Note this is no longer populated by reflection because the reflection was slow
     * and could make the tests take a long time to run.
     */
    abstract val tables: Map<Class<out SqliteTable>, SqliteTable>
    val insertStatements = mutableMapOf<Class<out SqliteTable>, PreparedStatement>()

    @Throws(MissingTableConfigException::class)
    inline fun <reified T : SqliteTable> getTable(): T {
        val table = tables[T::class.java]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No table has been registered for " + T::class.simpleName + ". You might want to consider fixing that.")

        return table as T
    }

    @Throws(MissingTableConfigException::class)
    inline fun <reified T : SqliteTable> getInsert(): PreparedStatement {
        return insertStatements[T::class.java]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No prepared statement has been registered for " + T::class.simpleName + ". You might want to consider fixing that.")
    }


    fun forEachTable(action: (SqliteTable) -> Unit) {
        tables.values.forEach(action)
    }

    fun prepareInsertStatements(connection: Connection, getPreparedStatement: (Connection, String) -> PreparedStatement) {
        insertStatements.clear()
        for ((key, value) in tables)
            insertStatements[key] = getPreparedStatement(connection, value.preparedInsertSql())
    }

}

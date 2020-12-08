/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite

import com.google.common.reflect.ClassPath
import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import java.lang.reflect.Modifier
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*


@Suppress("UnstableApiUsage")
class DatabaseTables {

    private val tables: MutableMap<Class<out SqliteTable>, SqliteTable> = mutableMapOf()
    private val insertStatements: MutableMap<Class<out SqliteTable>, PreparedStatement> = HashMap()

    init {
        ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.evolve.database.sqlite.tables")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { SqliteTable::class.java.isAssignableFrom(it) }
            .map { it.getConstructor().newInstance() as SqliteTable }
            .forEach { table -> tables[table.javaClass] = table }
    }

    @Throws(MissingTableConfigException::class)
    fun <T : SqliteTable> getTable(clazz: Class<T>): T {
        val table = tables[clazz]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No table has been registered for " + clazz.simpleName + ". You might want to consider fixing that.")

        return clazz.cast(table)
    }

    @Throws(MissingTableConfigException::class)
    fun getInsert(clazz: Class<out SqliteTable>): PreparedStatement {
        return insertStatements[clazz]
            ?: throw MissingTableConfigException("INTERNAL ERROR: No prepared statement has been registered for " + clazz.simpleName + ". You might want to consider fixing that.")
    }

    fun forEachTable(action: (SqliteTable) -> Unit) {
        tables.values.forEach(action)
    }

    fun prepareInsertStatements(connection: Connection, getPreparedStatement: (Connection, String) -> PreparedStatement) {
        insertStatements.clear()
        for ((key, value) in tables) insertStatements[key] = getPreparedStatement(connection, value.preparedInsertSql())
    }
}

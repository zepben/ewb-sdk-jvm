/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite

import com.google.common.reflect.ClassPath
import com.zepben.cimbend.database.MissingTableConfigException
import com.zepben.cimbend.database.sqlite.tables.SqliteTable
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
            .getTopLevelClassesRecursive("com.zepben.cimbend.database.sqlite.tables")
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

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables

import com.zepben.cimbend.database.Column
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.kotlinProperty

/**
 * Represents a table in an Sqlite Database
 */
abstract class SqliteTable {
    @JvmField
    protected var columnIndex = 0
    private var columnSet: SortedSet<Column>? = null
    private var createTableSql: String? = null
    private var preparedInsertSql: String? = null
    private var preparedUpdateSql: String? = null
    private var createIndexesSql: Collection<String>? = null
    private var selectSql: String? = null
    abstract fun name(): String
    open fun uniqueIndexColumns(): List<List<Column>> {
        return ArrayList()
    }

    open fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        return ArrayList()
    }

    fun columnSet(): SortedSet<Column> = columnSet ?: createColumnSet(tableClass, tableClassInstance)

    fun createTableSql(): String? = createTableSql ?: buildCreateTableSql()

    fun preparedInsertSql(): String = preparedInsertSql ?: buildPreparedInsertSql()

    fun createIndexesSql(): Collection<String> = createIndexesSql ?: buildCreateIndexSql()

    fun selectSql(): String = selectSql ?: buildSelectSql()

    fun preparedUpdateSql(): String = preparedUpdateSql ?: buildPreparedUpdateSql()

    protected abstract val tableClass: Class<*>
    protected abstract val tableClassInstance: Any

    @Suppress("UNCHECKED_CAST")
    private fun createColumnSet(clazz: Class<*>, instance: Any): SortedSet<Column> {
        // We sort by the queryIndex so insert and select statements can be addressed by a number
        val cols: SortedSet<Column> = TreeSet(Comparator.comparing { obj: Column -> obj.queryIndex() })
        var repeatedField: Boolean
        if (clazz.superclass != null) cols.addAll(createColumnSet(clazz.superclass, instance))
        for (field in clazz.declaredFields) {

            if (field.type == Column::class.java) {
                try {
                    repeatedField = !cols.add(field.get(instance) as Column)
                    if (repeatedField)
                        logger.error("INTERNAL ERROR: The field " + field.name + " in the SQL table class " + clazz.name + " is using an index that has already been used. Did you forget a ++?")
                } catch (e: IllegalAccessException) {
                    logger.debug("Trying to retrieve field " + field.name.toString() + "as Kotlin property.")
                    try {
                        val prop = field.kotlinProperty as? KProperty1<Any, Column>
                        if (prop == null) {
                            logger.error("INTERNAL ERROR: The field ${field.name} in the SQL table class ${clazz.name} couldn't be casted as a KProp as its kotlinProperty was null. It will be missing from the database.")
                            continue
                        }
                        repeatedField = !cols.add(prop.get(instance))
                        if (repeatedField)
                            logger.error("INTERNAL ERROR: The field " + field.name + " in the SQL table class " + clazz.name + " is using an index that has already been used. Did you forget a ++?")
                    } catch (e: IllegalAccessException) {
                        logger.error(
                            "Unable to retrieve field " + field.getName()
                                .toString() + ". It will be missing from the database: " + e.message
                        )
                    }
                }
            }
        }
        return Collections.unmodifiableSortedSet(cols)
    }

    private fun buildCreateTableSql(): String {
        val sb = StringBuilder()
        val joiner = StringJoiner(", ")
        sb.append("CREATE TABLE ").append(name()).append("(")
        for (c in columnSet()) {
            joiner.add(c.sqlString())
        }
        sb.append(joiner.toString())
        sb.append(")")
        return sb.toString()
    }

    private fun buildPreparedInsertSql(): String {
        val sb = StringBuilder()
        sb.append("INSERT INTO ").append(name()).append(" (")
        val cols = StringJoiner(", ")
        val places = StringJoiner(", ")
        for (c in columnSet()) {
            cols.add(c.name())
            places.add("?")
        }
        sb.append(cols.toString()).append(") VALUES (").append(places.toString()).append(")")
        return sb.toString()
    }

    private fun buildSelectSql(): String {
        val sb = StringBuilder()
        val joiner = StringJoiner(", ")
        sb.append("SELECT ")
        for (c in columnSet()) {
            joiner.add(c.name())
        }
        sb.append(joiner.toString()).append(" FROM ").append(name())
        return sb.toString()
    }

    private fun buildPreparedUpdateSql(): String {
        val sb = StringBuilder()
        val joiner = StringJoiner(", ")
        sb.append("UPDATE ").append(name()).append(" SET ")
        for (c in columnSet()) {
            joiner.add(c.name() + " = ?")
        }
        sb.append(joiner.toString())
        return sb.toString()
    }

    private fun buildCreateIndexSql(): Collection<String> {
        val statements: MutableList<String> = ArrayList()
        for (indexCol in uniqueIndexColumns()) statements.add(buildCreateIndexSql(indexCol, true))
        for (indexCol in nonUniqueIndexColumns()) statements.add(buildCreateIndexSql(indexCol, false))
        return statements
    }

    private fun buildCreateIndexSql(columns: List<Column>, isUnique: Boolean): String {
        val colJoiner = StringJoiner(", ")
        val idJoiner = StringJoiner("_")
        for (c in columns) {
            colJoiner.add(c.name())
            idJoiner.add(c.name())
        }
        val idString = name() + "_" + idJoiner.toString()
        val colString = colJoiner.toString()
        return String.format("CREATE %sINDEX %s ON %s (%s)", if (isUnique) "UNIQUE " else "", idString, name(), colString)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SqliteTable::class.java)
    }
}

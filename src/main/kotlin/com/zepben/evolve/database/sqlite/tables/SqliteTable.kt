/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.kotlinProperty

/**
 * Represents a table in a Sqlite Database
 */
abstract class SqliteTable {

    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmField
    protected var columnIndex: Int = 0

    abstract fun name(): String

    open fun uniqueIndexColumns(): MutableList<List<Column>> {
        return ArrayList()
    }

    open fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        return ArrayList()
    }

    private fun columnSet(): SortedSet<Column> = createColumnSet(tableClass, tableClassInstance)

    fun createTableSql(): String = buildCreateTableSql()

    fun preparedInsertSql(): String = buildPreparedInsertSql()

    fun createIndexesSql(): Collection<String> = buildCreateIndexSql()

    open fun selectSql(): String = buildSelectSql()

    fun preparedUpdateSql(): String = buildPreparedUpdateSql()

    protected abstract val tableClass: Class<out SqliteTable>
    protected abstract val tableClassInstance: SqliteTable

    @Suppress("UNCHECKED_CAST")
    private fun createColumnSet(clazz: Class<*>, instance: Any): SortedSet<Column> {
        // We sort by the queryIndex so insert and select statements can be addressed by a number
        val cols = sortedSetOf<Column>(Comparator.comparing { it.queryIndex })
        var repeatedField: Boolean

        if (clazz.superclass != null)
            cols.addAll(createColumnSet(clazz.superclass, instance))

        for (field in clazz.declaredFields) {
            if (field.type == Column::class.java) {
                try {
                    repeatedField = !cols.add(field.get(instance) as Column)
                    if (repeatedField)
                        logger.error("INTERNAL ERROR: The field ${field.name} in the SQL table class ${clazz.name} is using an index that has already been used. Did you forget a ++?")
                } catch (e: IllegalAccessException) {
                    logger.debug("Trying to retrieve field ${field.name}as Kotlin property.")
                    try {
                        val prop = field.kotlinProperty as? KProperty1<Any, Column>
                        if (prop == null) {
                            logger.error("INTERNAL ERROR: The field ${field.name} in the SQL table class ${clazz.name} couldn't be casted as a KProp as its kotlinProperty was null. It will be missing from the database.")
                            continue
                        }

                        repeatedField = !cols.add(prop.get(instance))
                        if (repeatedField)
                            logger.error("INTERNAL ERROR: The field ${field.name} in the SQL table class ${clazz.name} is using an index that has already been used. Did you forget a ++?")
                    } catch (e: IllegalAccessException) {
                        logger.error("Unable to retrieve field ${field.name}. It will be missing from the database: ${e.message}")
                    }
                }
            }
        }
        return Collections.unmodifiableSortedSet(cols)
    }

    private fun buildCreateTableSql(): String {
        val joiner = StringJoiner(", ")
        for (c in columnSet())
            joiner.add(c.sqlString())

        return "CREATE TABLE ${name()} ($joiner)"
    }

    private fun buildPreparedInsertSql(): String {
        val cols = StringJoiner(", ")
        val places = StringJoiner(", ")
        for (c in columnSet()) {
            cols.add(c.name)
            places.add("?")
        }

        return "INSERT INTO ${name()} ($cols) VALUES ($places)"
    }

    private fun buildSelectSql(): String {
        val joiner = StringJoiner(", ")
        for (c in columnSet())
            joiner.add(c.name)

        return "SELECT $joiner FROM ${name()}"
    }

    private fun buildPreparedUpdateSql(): String {
        val joiner = StringJoiner(", ")
        for (c in columnSet())
            joiner.add("${c.name} = ?")

        return "UPDATE ${name()} SET $joiner"
    }

    private fun buildCreateIndexSql(): Collection<String> =
        uniqueIndexColumns().map { buildCreateIndexSql(it, true) } +
            nonUniqueIndexColumns().map { buildCreateIndexSql(it, false) }

    private fun buildCreateIndexSql(columns: List<Column>, isUnique: Boolean): String {
        val colJoiner = StringJoiner(", ")
        val idJoiner = StringJoiner("_")
        for (c in columns) {
            colJoiner.add(c.name)
            idJoiner.add(c.name)
        }

        return "CREATE ${if (isUnique) "UNIQUE " else ""}INDEX ${name()}_$idJoiner ON ${name()} ($colJoiner)"
    }

}

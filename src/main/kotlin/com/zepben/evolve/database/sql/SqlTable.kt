/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * Represents a table in an SQL Database.
 *
 * By default, this class doesn't support creating schema creation statements, allowing support for database with external schema management.
 *
 * @property name The name of the table in the actual database.
 * @property description Readable description of the contents of the table for adding to logs.
 * @property createTableSql The SQL statement that should be executed to create the table in the database. By default, this will throw an
 * `UnsupportedOperationException`.
 * @property preparedInsertSql The SQL statement that should be used with a [PreparedStatement] to insert entries into the table.
 * @property createIndexesSql The SQL statement that should be executed to create the indexes for the table in the database. Should be executed after all
 *   entries are inserted into the table. By default, this will throw an `UnsupportedOperationException`.
 * @property selectSql The SQL statement that should be used to read the entries from the table in the database. This value is `open` to allow descendant
 *   classes to modify the SQL to add things such as ordering when that is important.
 * @property preparedUpdateSql The SQL statement that should be used with a [PreparedStatement] to update entries into the table.
 */
abstract class SqlTable internal constructor() {

    //
    // NOTE: `name` is defined as an abstract val, rather than a class member via the constructor, to avoid passing it between all abstract classes in the
    // table class hierarchy, which removes a lot of boilerplate.
    //
    abstract val name: String

    open val description: String by lazy { name.replace('_', ' ') }

    open val createTableSql: String get() = throw UnsupportedOperationException("Schema creation is not supported")

    open val preparedInsertSql: String by lazy { "INSERT INTO $name (${columnNames.joinToString()}) VALUES (${columnNames.joinToString { "?" }})" }

    open val createIndexesSql: Collection<String> get() = throw UnsupportedOperationException("Schema creation is not supported")

    open val selectSql: String by lazy { "SELECT ${columnNames.joinToString()} FROM $name" }

    open val preparedUpdateSql: String by lazy { "UPDATE $name SET ${columnNames.joinToString { "$it = ?" }}" }

    /**
     * The index of the previously added column. Should be used to increment column indexes to avoid massive rework when adding columns to base classes.
     *
     * e.g.
     * val MY_COL: Column = Column(++columnIndex, "my_col", "TEXT", NOT_NULL)
     */
    protected var columnIndex: Int = 0

    /**
     * A list of column groups that require a unique index in the database.
     */
    protected open val uniqueIndexColumns: MutableList<List<Column>> = mutableListOf()

    /**
     * A list of column groups that require a non-unique index in the database.
     */
    protected open val nonUniqueIndexColumns: MutableList<List<Column>> = mutableListOf()

    protected val columnSet: SortedSet<Column> by lazy { createColumnSet(this::class, this) }
    protected val columnNames: List<String> by lazy { columnSet.map { it.name } }

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun createColumnSet(klass: KClass<*>, instance: SqlTable): SortedSet<Column> {
        // We sort by the queryIndex so insert and select statements can be addressed by a number
        val cols = sortedSetOf<Column>(Comparator.comparing { it.queryIndex })
        var repeatedField: Boolean

        for (prop in klass.memberProperties) {
            if (prop.returnType.classifier == Column::class) {
                logger.debug("Trying to retrieve prop ${prop.name}as Kotlin property.")
                try {
                    repeatedField = !cols.add(prop.getter.call(instance) as Column)
                    if (repeatedField)
                        logger.error("INTERNAL ERROR: The prop ${prop.name} in the SQL table class ${klass.simpleName} is using an index that has already been used. Did you forget a ++?")
                } catch (e: IllegalAccessException) {
                    logger.error("Unable to retrieve prop ${prop.name}. It will be missing from the database: ${e.message}")
                }
            }
        }

        return Collections.unmodifiableSortedSet(cols)
    }

}

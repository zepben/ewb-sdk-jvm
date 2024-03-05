/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.extensions.executeConfiguredQuery
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * A base class for writing collections of object collections to a database.
 *
 * @property databaseTables The tables that are available in the database
 * @property getStatement A callback for getting access to a [Statement] that can be used for executing SQL queries. NOTE: This class will close each statement
 *   retrieved via this callback/
 */
abstract class BaseCollectionReader(
    val databaseTables: BaseDatabaseTables,
    protected val getStatement: () -> Statement
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun load(): Boolean

    protected inline fun <reified T : SqliteTable> Boolean.andLoadEach(crossinline processRow: (T, ResultSet, (String) -> String) -> Boolean): Boolean =
        this and loadEach(processRow)

    protected inline fun <reified T : SqliteTable> loadEach(crossinline processRow: (T, ResultSet, (String) -> String) -> Boolean): Boolean {
        return databaseTables.getTable<T>().loadAll() { results ->
            var lastIdentifier: String? = null
            val setLastIdentifier = { identifier: String -> lastIdentifier = identifier; identifier }

            try {
                var count = 0
                while (results.next()) {
                    if (processRow(this, results, setLastIdentifier)) {
                        ++count
                    }
                }

                return@loadAll count
            } catch (e: SQLException) {
                logger.error("Failed to load '$lastIdentifier' from '$name': ${e.message}")
                throw e
            }
        }
    }

    protected fun <T : SqliteTable> T.loadAll(processRows: T.(ResultSet) -> Int): Boolean {
        logger.info("Loading $description...")

        val thrown = try {
            val count = getStatement().use { statement ->
                statement.executeConfiguredQuery(selectSql).use { results ->
                    processRows(results)
                }
            }
            logger.info("Successfully loaded $count $description.")
            return true
        } catch (t: Throwable) {
            when (t) {
                is SQLException,
                is IllegalArgumentException,
                is MRIDLookupException,
                is DuplicateMRIDException -> t

                else -> throw t
            }
        }

        logger.error("Failed to read the $description from '$name': ${thrown.message}", thrown)
        return false
    }

}

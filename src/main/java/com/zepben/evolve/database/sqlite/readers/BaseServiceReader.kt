/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.readers

import com.zepben.evolve.database.executeConfiguredQuery
import com.zepben.evolve.database.sqlite.DatabaseTables
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * Base class for reading a service from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
open class BaseServiceReader constructor(protected val getStatement: () -> Statement) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)
    protected val databaseTables = DatabaseTables()

    protected inline fun <reified T : SqliteTable> loadEach(
        description: String,
        crossinline processRow: (T, ResultSet, (String) -> String) -> Boolean
    ): Boolean {
        return loadTable<T>(description) { table, results ->
            var lastMRID: String? = null
            val setLastMRID = { mrid: String -> lastMRID = mrid; mrid }

            try {
                var count = 0
                while (results.next()) {
                    if (processRow(table, results, setLastMRID)) {
                        ++count
                    }
                }

                return@loadTable count
            } catch (e: SQLException) {
                logger.error("Failed to load '" + lastMRID + "' from '" + table.name() + "': " + e.message)
                throw e
            }
        }
    }

    protected inline fun <reified T : SqliteTable> loadTable(
        description: String,
        processRows: (T, ResultSet) -> Int
    ): Boolean {
        logger.info("Loading $description...")

        val table = databaseTables.getTable(T::class.java)
        val thrown = try {
            val count = getStatement().use { statement ->
                statement.executeConfiguredQuery(table.selectSql()).use { results ->
                    processRows(table, results)
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

        logger.error("Failed to read the $description from '${table.name()}': ${thrown.message}", thrown)
        return false
    }

}

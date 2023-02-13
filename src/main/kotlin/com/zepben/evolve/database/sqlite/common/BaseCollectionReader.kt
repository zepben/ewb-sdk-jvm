/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2023 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.extensions.executeConfiguredQuery
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement


abstract class BaseCollectionReader(
    val databaseTables: DatabaseTables,
    protected val getStatement: () -> Statement
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    abstract fun load(): Boolean

    protected fun <T : SqliteTable> loadEach(
        description: String,
        table: T,
        processRow: (T, ResultSet, (String) -> String) -> Boolean
    ): Boolean {
        return loadTable(description, table) { results ->
            var lastIdentifier: String? = null
            val setLastIdentifier = { identifier: String -> lastIdentifier = identifier; identifier }

            try {
                var count = 0
                while (results.next()) {
                    if (processRow(table, results, setLastIdentifier)) {
                        ++count
                    }
                }

                return@loadTable count
            } catch (e: SQLException) {
                logger.error("Failed to load '" + lastIdentifier + "' from '" + table.name() + "': " + e.message)
                throw e
            }
        }
    }

    private fun <T : SqliteTable> loadTable(
        description: String,
        table: T,
        processRows: (ResultSet) -> Int
    ): Boolean {
        logger.info("Loading $description...")


        val thrown = try {
            val count = getStatement().use { statement ->
                statement.executeConfiguredQuery(table.selectSql()).use { results ->
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

        logger.error("Failed to read the $description from '${table.name()}': ${thrown.message}", thrown)
        return false
    }

}

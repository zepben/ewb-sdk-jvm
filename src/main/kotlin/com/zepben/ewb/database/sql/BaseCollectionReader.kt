/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql

import com.zepben.ewb.database.sql.extensions.executeConfiguredQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * A base class for reading collections of object collections from a database.
 *
 * @property T The type of collection used by this reader.
 * @property databaseTables The tables that are available in the database.
 * @property logger The logger to use for this collection reader.

 * @param connection The connection to the database to read.
 */
internal abstract class BaseCollectionReader<T>(
    val databaseTables: BaseDatabaseTables,
    protected val connection: Connection
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Read all the objects for the available collections.
     *
     * @param data The data to be populated with the read objects.
     *
     * @return true if the read was successful, otherwise false.
     */
    abstract fun read(data: T): Boolean

    /**
     * Read each row of a table.
     *
     * @param TTable The [SqlTable] to read the objects from.
     * @param processRow A callback for processing each row in the table. The callback will be provided with the table, the results for the row and a callback
     *   to set the identifier for the row, which returns the same value, so it can be used fluently. If a [ReaderException] is thrown within the callback,
     *   this method returns false.
     */
    protected inline fun <reified TTable : SqlTable> readEach(
        data: T,
        crossinline processRow: (T, TTable, ResultSet, setIdentifier: (String) -> String) -> Boolean,
        crossinline prepareSelectStatement: Connection.(TTable) -> PreparedStatement = { prepareStatement(it.selectSql) }
    ): Boolean {
        val table = databaseTables.getTable<TTable>()
        return table.readAll(connection.prepareSelectStatement(table)) { results ->
            var lastIdentifier: String? = null
            val setIdentifier = { identifier: String -> lastIdentifier = identifier; identifier }

            try {
                var count = 0
                while (results.next()) {
                    if (processRow(data, this, results, setIdentifier)) {
                        ++count
                    }
                }

                count
            } catch (e: SQLException) {
                logger.error("Failed to read '$lastIdentifier' from '$name': ${e.message}")
                throw e
            }
        }
    }

    /**
     * You really shouldn't need to use this function directly, use [readEach] instead.
     *
     * NOTE: This is marked protected rather than private to allow the inline reified functions above to work.
     */
    protected fun <TTable : SqlTable> TTable.readAll(selectStatement: PreparedStatement, processRows: TTable.(ResultSet) -> Int): Boolean {
        logger.info("Reading $description...")

        val thrown = try {
            val count = selectStatement.use {
                it.executeConfiguredQuery().use { results ->
                    processRows(results)
                }
            }
            logger.info("Successfully read $count $description.")
            return true
        } catch (t: Throwable) {
            when (t) {
                is SQLException,
                is IllegalArgumentException,
                is ReaderException -> t

                else -> throw t
            }
        }

        logger.error("Failed to read the $description from '$name': ${thrown.message}", thrown)
        return false
    }

}

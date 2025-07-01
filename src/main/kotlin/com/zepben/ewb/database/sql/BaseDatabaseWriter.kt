/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * A base class for writing objects to one of our databases.
 *
 * @param databaseTables The tables to create in the database.
 * @param getConnection Provider of the connection to the specified database.
 *
 * @property logger The logger to use for this database writer.
 */
abstract class BaseDatabaseWriter<TTables : BaseDatabaseTables, T> internal constructor(
    protected val databaseTables: TTables,
    private val getConnection: () -> Connection
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Write to the database.
     *
     * @param data The data to write to the database.
     *
     * @return true if the database was successfully written, otherwise false.
     */
    fun write(data: T): Boolean {
        try {
            if (!beforeConnect())
                return false

            return connect().use { connection ->
                afterConnectBeforePrepare(connection)
                    && versionMatches(connection)
                    && prepareInsertStatements(connection)
                    && writeData(data)
                    && afterWriteBeforeCommit(connection)
                    && commit(connection)
            }
        } catch (e: Exception) {
            when (e) {
                is SQLException, is MissingTableConfigException -> {
                    logger.error("Failed to write the database: " + e.message)
                    return false
                }

                else -> throw e
            }
        }
    }

    /**
     * Code to execute before a connection is made to the database.
     *
     * This is the ideal time to be doing things such as taking backups of files, or removing existing files if they should be replaced.
     *
     * @return true if the processing was successful, otherwise false.
     */
    protected abstract fun beforeConnect(): Boolean

    /**
     * Code to execute after the connection has been made to the database, but before any statements are prepared.
     *
     * This is the ideal time to do any schema creation.
     *
     * @param connection A [Connection] to the database.
     * @return true if the processing was successful, otherwise false.
     */
    protected abstract fun afterConnectBeforePrepare(connection: Connection): Boolean

    /**
     * Write the actual data to the database.
     *
     * @param data The data to write.
     * @return true if the processing was successful, otherwise false.
     */
    protected abstract fun writeData(data: T): Boolean

    /**
     * Code to execute after the data has been written to the database, but before the transaction is commited.
     *
     * This is the ideal time enable any indexes left out in the schema creation for performance reasons.
     *
     * @param connection A [Connection] to the database.
     * @return true if the processing was successful, otherwise false.
     */
    protected abstract fun afterWriteBeforeCommit(connection: Connection): Boolean

    private fun connect(): Connection {
        logger.info("Connecting to database...")

        return getConnection().apply {
            autoCommit = false
        }.also {
            logger.info("Connected.")
        }
    }

    private fun prepareInsertStatements(connection: Connection): Boolean {
        logger.info("Preparing insert statements...")

        databaseTables.prepareInsertStatements(connection)

        logger.info("Insert statements prepared.")
        return true
    }


    private fun versionMatches(connection: Connection): Boolean {
        val tableVersion = databaseTables.tables.values.filterIsInstance<TableVersion>().firstOrNull()
        if (tableVersion == null) {
            logger.error("INTERNAL ERROR: No version table defined, please make sure you add a version table to your database tables collection.")
            return false
        }
        val supportedVersion = tableVersion.supportedVersion

        return when (val version = tableVersion.getVersion(connection)) {
            null -> {
                logger.error("Missing version table in database file, cannot check compatibility")
                false
            }

            supportedVersion -> true
            else -> {
                logger.error("Unsupported version in database file (got $version, expected $supportedVersion).")
                false
            }
        }
    }

    private fun commit(connection: Connection): Boolean {
        logger.info("Committing...")

        connection.commit()

        logger.info("Done.")

        return true
    }

}

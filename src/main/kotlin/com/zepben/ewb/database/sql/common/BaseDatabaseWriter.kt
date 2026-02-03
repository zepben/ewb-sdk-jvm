/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.common

import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * A base class for writing objects to one of our databases.
 *
 * @property logger The logger to use for this database writer.
 * @property databaseTables The tables to create in the database.
 * @property databaseInitialiser The hooks used to initilise the database.
 */
abstract class BaseDatabaseWriter<TTables : BaseDatabaseTables> internal constructor() {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    protected abstract val databaseTables: TTables
    protected abstract val databaseInitialiser: DatabaseInitialiser<TTables>

    /**
     * Write to the database.
     *
     * @param writeData Function to perform writes to the database
     *
     * @return true if the database was successfully written, otherwise false.
     */
    fun connectAndWrite(writeData: () -> Boolean): Boolean {
        try {
            if (!databaseInitialiser.beforeConnect(logger))
                return false

            return connect().use { connection ->
                databaseInitialiser.afterConnectBeforePrepare(connection, databaseTables, logger)
                    && versionMatches(connection)
                    && prepareInsertStatements(connection)
                    && writeData()
                    && databaseInitialiser.afterWriteBeforeCommit(connection, databaseTables, logger)
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

    private fun connect(): Connection {
        logger.info("Connecting to database...")

        return databaseInitialiser.connect().apply {
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

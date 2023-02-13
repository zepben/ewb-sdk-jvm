/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.sql.Connection
import java.sql.SQLException

/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 */
abstract class DatabaseReader<T : BaseCollectionReader>(
    val databaseTables: DatabaseTables,
    private val reader: T,
    private val databaseFile: String,
    private val metadataCollectionReader: MetadataCollectionReader,
    private val upgradeRunner: UpgradeRunner,
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"

    private lateinit var loadConnection: Connection

    private var hasBeenUsed: Boolean = false

    protected open fun postLoad(): Boolean {
        return true
    }

    fun load(): Boolean {
        if (hasBeenUsed) {
            logger.error("You can only use the database reader once.")
            return false
        }
        hasBeenUsed = true

        val databaseVersion = preLoad()
        if (databaseVersion == null) {
            closeConnection()
            return false
        }

        logger.info("Loading from database version v$databaseVersion")
        return try {
            metadataCollectionReader.load() and
                reader.load() and
                postLoad()
        } catch (e: MissingTableConfigException) {
            logger.error("Unable to load database: " + e.message)
            false
        } finally {
            closeConnection()
        }
    }

    private fun preLoad(): Int? {
        return try {
            upgradeRunner.connectAndUpgrade(databaseDescriptor, Paths.get(databaseFile))
                .also { loadConnection = it.connection }
                .version
        } catch (e: UpgradeRunner.UpgradeException) {
            logger.error("Failed to connect to the database for reading: " + e.message, e)
            closeConnection()
            null
        }
    }

    private fun closeConnection() {
        try {
            if (::loadConnection.isInitialized)
                loadConnection.close()
        } catch (e: SQLException) {
            logger.error("Failed to close connection to database: " + e.message)
        }
    }


}

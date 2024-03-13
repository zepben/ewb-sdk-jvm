/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.upgrade.EwbDatabaseType
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.sql.Connection
import java.sql.SQLException

/**
 * A base class for reading objects from one of our databases.
 *
 * @property databaseFile the filename of the database to read.
 * @property createMetadataReader Callback to create the reader for the [MetadataCollection] included in this database using the provided connection.
 * @property createServiceReader Callback to create the reader for the [BaseService] supported by this database using the provided connection.
 * @property service The [BaseService] that will be populated by the [BaseServiceReader]. Used for post-processing.
 * @property upgradeRunner The [UpgradeRunner] used to ensure this database is on the correct schema version.
 *
 * @property logger The [Logger] to use for this reader.
 * @property databaseDescriptor The JDBC descriptor for connecting to the database.
 */
abstract class BaseDatabaseReader(
    private val databaseFile: String,
    private val createMetadataReader: (Connection) -> MetadataCollectionReader,
    private val createServiceReader: (Connection) -> BaseServiceReader,
    protected open val service: BaseService,
    private val upgradeRunner: UpgradeRunner,
    private val databaseType: EwbDatabaseType
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"
    private lateinit var loadConnection: Connection
    private var hasBeenUsed: Boolean = false

    /**
     * Customisable function for performing actions after the database has been loaded.
     */
    protected open fun postLoad(): Boolean {
        logger.info("Ensuring all references resolved...")
        service.unresolvedReferences().forEach {
            throw IllegalStateException(
                "Unresolved references found in ${service.name} service after load - this should not occur. Failing reference was from " +
                    "${it.from.typeNameAndMRID()} resolving ${it.resolver.toClass.simpleName} ${it.toMrid}"
            )
        }
        logger.info("Unresolved references were all resolved during load.")

        return true
    }

    /**
     * Load the database.
     */
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
            createMetadataReader(loadConnection).load() and
                createServiceReader(loadConnection).load() and
                postLoad()
        } catch (e: MissingTableConfigException) {
            logger.error("Unable to load database: " + e.message)
            false
        } finally {
            closeConnection()
        }
    }

    private fun preLoad(): Int? =
        try {
            upgradeRunner.connectAndUpgrade(databaseDescriptor, Paths.get(databaseFile), databaseType)
                .also { loadConnection = it.connection }
                .version
        } catch (e: UpgradeRunner.UpgradeException) {
            logger.error("Failed to connect to the database for reading: " + e.message, e)
            closeConnection()
            null
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

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection

/**
 * A base class for reading objects from one of our databases.
 *
 * @property databaseFile the filename of the database to read.
 * @property createMetadataReader Callback to create the reader for the [MetadataCollection] included in this database using the provided connection.
 * @property createServiceReader Callback to create the reader for the [BaseService] supported by this database using the provided connection.
 * @property service The [BaseService] that will be populated by the [BaseServiceReader]. Used for post-processing.
 *
 * @property logger The [Logger] to use for this reader.
 * @property databaseDescriptor The JDBC descriptor for connecting to the database.
 */
abstract class BaseDatabaseReader(
    private val databaseFile: String,
    private val createMetadataReader: (Connection) -> MetadataCollectionReader,
    private val createServiceReader: (Connection) -> BaseServiceReader,
    protected open val service: BaseService,
    private val createConnection: (String) -> Connection,
    private val tableVersion: TableVersion
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"
    private var hasBeenUsed: Boolean = false

    private val supportedVersion = tableVersion.SUPPORTED_VERSION

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

        return preLoad()?.use { connection ->
            try {
                // NOTE: We only want to post process if we correctly read from the database.
                loadFromReaders(connection)
                    && postLoad()
            } catch (e: MissingTableConfigException) {
                logger.error("Unable to load database: " + e.message)
                false
            }
        } ?: false
    }

    private fun preLoad(): Connection? =
        try {
            val connection = createConnection(databaseDescriptor)
            if (connection.checkVersion())
                connection
            else {
                connection.close()
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to connect to the database for reading: " + e.message, e)
            null
        }

    private fun loadFromReaders(connection: Connection) =
        createMetadataReader(connection).load() and
            createServiceReader(connection).load()

    private fun Connection.checkVersion(): Boolean =
        createStatement().use { statement ->
            val version = tableVersion.getVersion(statement)
            if (version == supportedVersion) {
                logger.info("Loading from database version v$version")
                true
            } else {
                logger.error(formatVersionError(version))
                false
            }
        }

    private fun formatVersionError(version: Int?): String =
        when {
            version == null -> "Failed to read the version number form the selected database. Are you sure it is a EWB database?"
            version < supportedVersion -> unexpectedVersion(version, "Consider using the UpgradeRunner if you wish to support this database.")
            else -> unexpectedVersion(version, "You need to use a newer version of the SDK to load this database.")
        }


    private fun unexpectedVersion(version: Int?, action: String) =
        "Unable to load from database $databaseFile [found v$version, expected v$supportedVersion]. $action"

}

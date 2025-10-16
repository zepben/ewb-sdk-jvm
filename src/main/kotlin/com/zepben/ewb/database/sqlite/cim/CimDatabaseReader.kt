/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim

import com.zepben.ewb.database.sqlite.cim.metadata.MetadataCollectionReader
import com.zepben.ewb.database.sqlite.common.SqliteTableVersion
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.meta.MetadataCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection

/**
 * A base class for reading objects from one of our CIM databases.
 *
 * @property connection The connection to the database.
 * @property databaseDescription The description of the database for logging (e.g. filename).
 * @property databaseTables The collection of tables that will be read by this reader.
 * @property createMetadataReader Factory for the reader of the [MetadataCollection] included in the database.
 * @property createServiceReader Factory for the reader of the [BaseService] supported by the database.
 *
 * @property logger The [Logger] to use for this reader.
 */
abstract class CimDatabaseReader<TTables : CimDatabaseTables, TService : BaseService> internal constructor(
    private val connection: Connection,
    private val databaseDescription: String,
    private val databaseTables: TTables,
    private val createMetadataReader: (TTables, Connection) -> MetadataCollectionReader,
    private val createServiceReader: (TTables, Connection) -> BaseServiceReader<TService>,
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private var hasBeenUsed: Boolean = false

    /**
     * Customisable function for performing actions after the database has been read.
     */
    protected open fun afterServiceRead(service: TService): Boolean {
        logger.info("Ensuring all references resolved...")
        service.unresolvedReferences().forEach {
            throw IllegalStateException(
                "Unresolved references were found in ${service.name} service after read - this should not occur. Failing reference was from " +
                    "${it.from.typeNameAndMRID()} resolving ${it.resolver.toClass.simpleName} ${it.toMrid}"
            )
        }
        logger.info("Unresolved references were all resolved during read.")

        return true
    }

    /**
     * Read the database.
     */
    fun read(service: TService, preformAfterReadProcessing: Boolean = true): Boolean =
        try {
            require(!hasBeenUsed) { "You can only use the database reader once." }
            hasBeenUsed = true

            beforeRead()
                && readService(service)
                && if (afterServiceReadFunction ?: true) afterServiceRead(service) else true
        } catch (e: Exception) {
            logger.error("Unable to read database: " + e.message)
            false
        }

    private fun beforeRead(): Boolean =
        try {
            val versionTable = databaseTables.getTable<SqliteTableVersion>()
            val supportedVersion = versionTable.supportedVersion
            val version = versionTable.getVersion(connection)

            if (version == supportedVersion) {
                logger.info("Reading from database version v$version")
                true
            } else {
                logger.error(formatVersionError(version, supportedVersion))
                false
            }
        } catch (e: Exception) {
            logger.error("Failed to connect to the database for reading: " + e.message, e)
            false
        }

    private fun readService(service: TService) =
        createMetadataReader(databaseTables, connection).read(service.metadata) and
            createServiceReader(databaseTables, connection).read(service)

    private fun formatVersionError(version: Int?, supportedVersion: Int): String =
        when {
            version == null -> "Failed to read the version number from the selected database. Are you sure it is a EWB database?"
            version < supportedVersion -> unexpectedVersion(version, supportedVersion, "Consider using the UpgradeRunner if you wish to support this database.")
            else -> unexpectedVersion(version, supportedVersion, "You need to use a newer version of the SDK to read this database.")
        }


    private fun unexpectedVersion(version: Int?, supportedVersion: Int, action: String) =
        "Unable to read from database $databaseDescription [found v$version, expected v$supportedVersion]. $action"

}

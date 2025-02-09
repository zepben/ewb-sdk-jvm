/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim

import com.zepben.evolve.database.sqlite.cim.metadata.MetadataCollectionWriter
import com.zepben.evolve.database.sqlite.common.BaseDatabaseWriter
import com.zepben.evolve.database.sqlite.common.SqliteTableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.services.common.BaseService
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * A base class for writing objects to one of our CIM databases.
 *
 * @param TTables The type of [CimDatabaseTables] supported by this writer.
 * @param TService The type of [BaseService] supported by this writer.
 *
 * @param databaseFile The filename of the database to write.
 * @param databaseTables The tables to create in the database.
 * @param getConnection Provider of the connection to the specified database.
 * @param createMetadataWriter Factory for creating the [MetadataCollectionWriter] to use.
 * @param createServiceWriter Factory for creating the [BaseServiceWriter] to use.
 *
 * @property logger The logger to use for this database writer.
 */
abstract class CimDatabaseWriter<TTables : CimDatabaseTables, TService : BaseService> internal constructor(
    private val databaseFile: String,
    databaseTables: TTables,
    private val createMetadataWriter: (TTables) -> MetadataCollectionWriter,
    private val createServiceWriter: (TTables) -> BaseServiceWriter<TService>
) : BaseDatabaseWriter<TTables, TService>(databaseTables, { DriverManager.getConnection("jdbc:sqlite:$databaseFile").configureBatch() }) {

    private var hasBeenUsed: Boolean = false

    override fun beforeConnect(): Boolean {
        if (hasBeenUsed) {
            logger.error("You can only use the database writer once.")
            return false
        }
        hasBeenUsed = true

        return try {
            Files.deleteIfExists(Paths.get(databaseFile))
            true
        } catch (e: IOException) {
            logger.error("Unable to write database, failed to remove previous instance: " + e.message)
            false
        }
    }

    override fun afterConnectBeforePrepare(connection: Connection): Boolean =
        try {
            val versionTable = databaseTables.getTable<SqliteTableVersion>()
            logger.info("Creating database schema v${versionTable.supportedVersion}...")

            connection.createStatement().use { statement ->
                statement.queryTimeout = 2

                databaseTables.forEachTable {
                    statement.executeUpdate(it.createTableSql)
                }

                // Add the version number to the database.
                connection.prepareStatement(versionTable.preparedInsertSql).use { insert ->
                    insert.setInt(versionTable.VERSION.queryIndex, versionTable.supportedVersion)
                    insert.executeUpdate()
                }

                connection.commit()
                logger.info("Schema created.")
            }
            true
        } catch (e: SQLException) {
            logger.error("Failed to create database schema: " + e.message)
            false
        }

    /**
     * Write metadata and service.
     */
    override fun writeData(data: TService): Boolean =
        createMetadataWriter(databaseTables).write(data.metadata) and createServiceWriter(databaseTables).write(data)

    override fun afterWriteBeforeCommit(connection: Connection): Boolean {
        logger.info("Adding indexes...")

        connection.createStatement().use { statement ->
            databaseTables.forEachTable { table ->
                table.createIndexesSql.forEach { sql ->
                    statement.execute(sql)
                }
            }
        }

        logger.info("Indexes added.")

        return true
    }

}

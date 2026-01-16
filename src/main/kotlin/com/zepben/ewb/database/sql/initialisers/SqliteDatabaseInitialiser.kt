/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.initialisers

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.extensions.configureBatch
import org.slf4j.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * A database initialiser for managing SQLite databases.
 *
 * @property databaseFile The database file. Used for connections and file management.
 */
class SqliteDatabaseInitialiser<TTables : BaseDatabaseTables> @Suppress("KDocMissingDocumentation") internal constructor(
    val databaseFile: String,
    private val deleteFile: (Path) -> Unit,
    private val getConnection: (String) -> Connection,
) : DatabaseInitialiser<TTables> {

    /**
     * @param databaseFile The database file. Used for connections and file management.
     */
    constructor(
        databaseFile: String,
    ) : this(databaseFile, Files::deleteIfExists, DriverManager::getConnection)

    override fun beforeConnect(logger: Logger): Boolean {
        return try {
            deleteFile(Paths.get(databaseFile))
            true
        } catch (e: IOException) {
            logger.error("Unable to write database, failed to remove previous instance: " + e.message)
            false
        }
    }

    override fun connect(): Connection = getConnection("jdbc:sqlite:$databaseFile").configureBatch()

    override fun afterConnectBeforePrepare(connection: Connection, databaseTables: TTables, logger: Logger): Boolean =
        try {
            val versionTable = databaseTables.getTable<TableVersion>()
            logger.info("Creating database schema v${versionTable.supportedVersion}...")

            connection.createStatement().use { statement ->
                statement.queryTimeout = 2

                databaseTables.forEachTable {
                    statement.executeUpdate(databaseTables.sqlGenerator.createTableSql(it))
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

    override fun afterWriteBeforeCommit(connection: Connection, databaseTables: TTables, logger: Logger): Boolean {
        logger.info("Adding indexes...")

        connection.createStatement().use { statement ->
            databaseTables.forEachTable { table ->
                databaseTables.sqlGenerator.createIndexesSql(table).forEach { sql ->
                    statement.execute(sql)
                }
            }
        }

        logger.info("Indexes added.")

        return true
    }

}

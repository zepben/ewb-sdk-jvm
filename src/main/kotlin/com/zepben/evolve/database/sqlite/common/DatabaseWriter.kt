/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.TableVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement


/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 * @property getPreparedStatement provider of prepared statements for the connection.
 */
abstract class DatabaseWriter<T : BaseCollectionWriter>(
    private val databaseTables: DatabaseTables,
    private val writer: T,
    private val hasCommon: (String) -> Boolean,
    private val addCommon: (String) -> Boolean,
    private val metadataCollectionWriter: MetadataCollectionWriter,
    private val databaseFile: String,
    private val getConnection: (String) -> Connection,
    private val getStatement: (Connection) -> Statement,
    private val getPreparedStatement: (Connection, String) -> PreparedStatement,
    private val metadataTables: DatabaseTables = metadataDatabaseTables,
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"

    private lateinit var saveConnection: Connection

    private var hasBeenUsed: Boolean = false

    //todo remove initialCreation and use separate file per database.
    fun save(initialCreation: Boolean): Boolean {
        if (hasBeenUsed) {
            logger.error("You can only use the database writer once.")
            return false
        }
        hasBeenUsed = true

        if (!preSave(initialCreation)) {
            closeConnection()
            return false
        }

        val status = try {
            metadataCollectionWriter.save() and
                writer.save()
        } catch (e: MissingTableConfigException) {
            logger.error("Unable to save database: " + e.message, e)
            false
        }

        return status and postSave()

    }

    private fun preSave(initialCreation: Boolean): Boolean {
        return removeExisting(initialCreation)
            && connect()
            && create()
            && prepareInsertStatements()
    }

    private fun removeExisting(initialCreation: Boolean): Boolean {
        if (initialCreation) {
            return try {
                Files.deleteIfExists(Paths.get(databaseFile))
                true
            } catch (e: IOException) {
                logger.error("Unable to save database, failed to remove previous instance: " + e.message)
                false
            }
        }
        return true
    }

    private fun connect(): Boolean {
        return try {
            saveConnection = getConnection(databaseDescriptor).configureBatch(getStatement)
            true
        } catch (e: SQLException) {
            logger.error("Failed to connect to the database for saving: " + e.message)
            closeConnection()
            false
        }
    }

    private fun prepareInsertStatements(): Boolean {
        return try {
            databaseTables.prepareInsertStatements(saveConnection, getPreparedStatement)
            metadataTables.prepareInsertStatements(saveConnection, getPreparedStatement)
            true
        } catch (e: SQLException) {
            logger.error("Failed to prepare insert statements: " + e.message, e)
            closeConnection()
            false
        }
    }

    private fun create(): Boolean {
        try {
            val versionTable = metadataTables.getTable<TableVersion>()
            logger.info("Creating database schema v${versionTable.SUPPORTED_VERSION}...")

            getStatement(saveConnection).use { statement ->
                statement.queryTimeout = 2

                metadataTables.forEachTable {
                    if (!hasCommon(it::class.simpleName!!)) {
                        addCommon(it::class.simpleName!!)
                        statement.executeUpdate(it.createTableSql())
                    }
                }

                databaseTables.forEachTable {
                    if (!hasCommon(it::class.simpleName!!)) {
                        addCommon(it::class.simpleName!!)
                        statement.executeUpdate(it.createTableSql())
                    }
                }

                // Add the version number to the database.
                if (!hasCommon("TableVersion:version_number")) {

                    addCommon("TableVersion:version_number")
                    getPreparedStatement(saveConnection, versionTable.preparedInsertSql()).use { insert ->
                        insert.setInt(versionTable.VERSION.queryIndex, versionTable.SUPPORTED_VERSION)
                        insert.executeUpdate()
                    }
                }

                saveConnection.commit()
                logger.info("Schema created.")
            }
        } catch (e: SQLException) {
            logger.error("Failed to create database schema: " + e.message)
            return false
        }

        return true
    }

    private fun closeConnection() {
        try {
            if (::saveConnection.isInitialized)
                saveConnection.close()
        } catch (e: SQLException) {
            logger.error("Failed to close connection to database: " + e.message)
        }
    }

    private fun postSave(): Boolean {
        try {
            logger.info("Adding indexes...")

            saveConnection.createStatement().use { statement ->
                databaseTables.forEachTable { table ->
                    table.createIndexesSql().forEach { sql ->
                        if (!hasCommon("${table::class.simpleName!!}-index")) {
                            statement.execute(sql)
                            addCommon("${table::class.simpleName!!}-index")
                        }
                    }
                }
            }

            logger.info("Indexes added.")
            logger.info("Committing...")

            saveConnection.commit()

            logger.info("Done.")
        } catch (e: SQLException) {
            logger.error("Failed to finalise the database: " + e.message)
            closeConnection()
            return false
        }

        closeConnection()
        return true
    }

}

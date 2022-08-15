/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.writers.*
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.*


/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 * @property getPreparedStatement provider of prepared statements for the connection.
 */
class DatabaseWriter @JvmOverloads constructor(
    private val databaseFile: String,
    private val getConnection: (String) -> Connection = DriverManager::getConnection,
    private val getStatement: (Connection) -> Statement = Connection::createStatement,
    private val getPreparedStatement: (Connection, String) -> PreparedStatement = Connection::prepareStatement
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val databaseDescriptor: String = "jdbc:sqlite:$databaseFile"

    private lateinit var saveConnection: Connection
    private val databaseTables = DatabaseTables()

    private val savedCommonMRIDs = mutableSetOf<String>()
    private var hasBeenUsed: Boolean = false

    /**
     * Will attempt to save the provided [BaseService]s to the connected database.
     * Can only be called once per [DatabaseWriter]. Currently supports NetworkService, DiagramService, CustomerService,
     * and MeasurementService. Multiple of each type can be passed and will be merged in the database. This is not
     * well supported however, and not recommended. Merge services prior to calling save (for the moment :))
     */
    fun save(metadataCollection: MetadataCollection, services: List<BaseService>): Boolean {
        if (services.isEmpty()) {
            logger.warn("No services were provided, therefore there is nothing to save")
            return false
        }

        if (hasBeenUsed) {
            logger.error("You can only use the database writer once.")
            return false
        }
        hasBeenUsed = true

        if (!preSave()) {
            closeConnection()
            return false
        }

        var status = MetadataCollectionWriter().save(metadataCollection, MetaDataEntryWriter(databaseTables))
        services.forEach {
            status = status and try {
                when (it) {
                    is NetworkService -> NetworkServiceWriter(::hasCommon, ::addCommon).save(
                        it,
                        NetworkCIMWriter(databaseTables)
                    )
                    is CustomerService -> CustomerServiceWriter(::hasCommon, ::addCommon).save(
                        it,
                        CustomerCIMWriter(databaseTables)
                    )
                    is DiagramService -> DiagramServiceWriter(::hasCommon, ::addCommon).save(
                        it,
                        DiagramCIMWriter(databaseTables)
                    )
                    else -> run { logger.error("Unsupported service of type ${it.javaClass.simpleName} couldn't be saved."); false }
                }
            } catch (e: MissingTableConfigException) {
                logger.error("Unable to save database: " + e.message)
                false
            }
        }

        return status and postSave()
    }

    private fun addCommon(mRID: String) = savedCommonMRIDs.add(mRID)
    private fun hasCommon(mRID: String) = savedCommonMRIDs.contains(mRID)

    private fun preSave(): Boolean {
        return removeExisting()
                && connect()
                && create()
                && prepareInsertStatements()
    }

    private fun removeExisting(): Boolean {
        return try {
            Files.deleteIfExists(Paths.get(databaseFile))
            true
        } catch (e: IOException) {
            logger.error("Unable to save database, failed to remove previous instance: " + e.message)
            false
        }
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
            true
        } catch (e: SQLException) {
            logger.error("Failed to prepare insert statements: " + e.message)
            closeConnection()
            false
        }
    }

    private fun create(): Boolean {
        try {
            val versionTable = databaseTables.getTable(TableVersion::class.java)
            logger.info("Creating database schema v${versionTable.SUPPORTED_VERSION}")

            getStatement(saveConnection).use { statement ->
                statement.queryTimeout = 2

                databaseTables.forEachTable { statement.executeUpdate(it.createTableSql()) }

                // Add the version number to the database.
                getPreparedStatement(saveConnection, versionTable.preparedInsertSql()).use { insert ->
                    insert.setInt(versionTable.VERSION.queryIndex, versionTable.SUPPORTED_VERSION)
                    insert.executeUpdate()
                }

                saveConnection.commit()
                logger.info("Database saved.")
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
            saveConnection.createStatement().use { statement ->
                databaseTables.forEachTable { table ->
                    table.createIndexesSql().forEach { sql -> statement.execute(sql) }
                }
            }

            saveConnection.commit()
        } catch (e: SQLException) {
            logger.error("Failed to finalise the database: " + e.message)
            closeConnection()
            return false
        }

        closeConnection()
        return true
    }

}

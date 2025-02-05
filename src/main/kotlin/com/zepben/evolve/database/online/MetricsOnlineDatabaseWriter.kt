/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.online

import com.zepben.evolve.database.sqlite.common.SchemaUtils
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.metrics.MetricsDatabaseTables
import com.zepben.evolve.database.sqlite.metrics.MetricsWriter
import com.zepben.evolve.metrics.IngestionJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

/**
 * Class for writing metrics to an online SQL database.
 */
class MetricsOnlineDatabaseWriter internal constructor(
    private val getConnection: () -> Connection,
    private val databaseTables: MetricsDatabaseTables,
    private val schemaUtils: SchemaUtils = SchemaUtils(databaseTables),
    private val createMetricsWriter: (IngestionJob, MetricsDatabaseTables) -> MetricsWriter = { job, tables -> MetricsWriter(job, tables) }
) {

    /**
     * @param getConnection Provider of the connection to the specified database.
     */
    constructor(getConnection: () -> Connection) : this(getConnection, MetricsDatabaseTables())

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val versionTable = databaseTables.getTable<TableVersion>()

    fun save(job: IngestionJob): Boolean = getConnection().use { connection ->
        connection.configureBatch()
        val localVersion = versionTable.supportedVersion
        val status = when (val remoteVersion = schemaUtils.getVersion(connection)) {
            null -> schemaUtils.createSchema(connection) && schemaUtils.createIndexes(connection)
            localVersion -> true
            else -> {
                logger.error("Incompatible version in remote metrics database: expected v$localVersion, found v$remoteVersion. " +
                    "Please ${if (localVersion > remoteVersion) "upgrade the remote database" else "use a newer version of the SDK"}.")
                false
            }
        }

        return status && populateTables(connection, job) && postSave(connection)
    }

    private fun populateTables(connection: Connection, job: IngestionJob): Boolean {
        databaseTables.prepareInsertStatements(connection)
        return createMetricsWriter(job, databaseTables).save()
    }

    private fun postSave(connection: Connection): Boolean = try {
        connection.commit()
        true
    } catch (e: SQLException) {
        logger.error("Failed to commit changes to the online metrics database: {}", e.message)
        false
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.sqlite.common.SchemaUtils
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.metrics.IngestionJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

/**
 * Class for writing metrics to an arbitrary datasource.
 */
class MetricsDataSourceWriter(
    private val dataSource: DataSource,
    private val databaseTables: MetricsDatabaseTables = MetricsDatabaseTables()
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val schemaUtils = SchemaUtils(databaseTables, logger)
    private val versionTable = databaseTables.getTable<TableVersion>()

    @Throws(IncompatibleVersionException::class)
    fun save(job: IngestionJob): Boolean {
        return dataSource.connection.use { connection ->
            connection.configureBatch()
            val localVersion = versionTable.supportedVersion
            when (val remoteVersion = schemaUtils.getVersion(connection)) {
                null -> schemaUtils.createSchema(connection) && populateTables(connection, job) && postSave(connection)
                localVersion -> populateTables(connection, job) && postSave(connection)
                else -> throw IncompatibleVersionException(localVersion, remoteVersion)
            }
        }
    }

    internal fun populateTables(connection: Connection, job: IngestionJob): Boolean {
        databaseTables.prepareInsertStatements(connection)
        return MetricsWriter(job, databaseTables).save()
    }

    private fun postSave(connection: Connection): Boolean {
        connection.commit()
        return true
    }

}

/**
 * Indicates a difference between the local and remote version of the metrics database.
 *
 * @property localVersion The locally-supported version of the metrics database in the SDK.
 * @property remoteVersion The version of the remote metrics database.
 */
class IncompatibleVersionException(
    val localVersion: Int,
    val remoteVersion: Int
) : Exception("Incompatible version in remote metrics database: expected v$localVersion, found v$remoteVersion. " +
    "Please ${if (localVersion > remoteVersion) "upgrade the remote database" else "use a newer version of the SDK"}.")

/**
 * Thrown if the version table in the remote metrics database has no entry.
 */
object MissingVersionException : Exception("Version table present in remote metrics database, but it missing an entry.")

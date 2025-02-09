/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.sqlite.common.BaseDatabaseWriter
import com.zepben.evolve.database.sqlite.common.SqliteTableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.metrics.IngestionJob
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.io.path.absolute
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries

internal const val JOB_ID_FILE_EXTENSION = "zjid"

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to a metrics database.
 *
 * @param databaseFile The filename of the metrics database.
 * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
 *                  named using the UUID of the ingestion job.
 * @param databaseTables The tables in the database.
 * @param createMetricsWriter Factory for the metrics writer to use.
 */
class MetricsDatabaseWriter internal constructor(
    private val databaseFile: String,
    private val modelPath: Path?,
    private val createMetricsWriter: (MetricsDatabaseTables) -> MetricsWriter
) : BaseDatabaseWriter<MetricsDatabaseTables, IngestionJob>(
    MetricsDatabaseTables(),
    { DriverManager.getConnection("jdbc:sqlite:$databaseFile").configureBatch() }
) {

    @JvmOverloads
    constructor(
        databaseFile: String,
        modelPath: Path? = null
    ) : this(databaseFile, modelPath, { MetricsWriter(it) })

    private var fileExists = false

    override fun beforeConnect(): Boolean {
        fileExists = Files.exists(Paths.get(databaseFile))
        return true
    }

    override fun afterConnectBeforePrepare(connection: Connection): Boolean {
        if (fileExists)
            return true

        //
        // NOTE: Duplicated from the CIM database writer as it is expected to be short-lived, so not going through the hassle of moving it to a common area.
        //
        return try {
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

                logger.info("Adding indexes...")

                databaseTables.forEachTable { table ->
                    table.createIndexesSql.forEach { sql ->
                        statement.execute(sql)
                    }
                }

                logger.info("Indexes added.")

                connection.commit()
                logger.info("Schema created.")
            }
            true
        } catch (e: SQLException) {
            logger.error("Failed to create database schema: " + e.message)
            false
        }
    }

    /**
     * Write the ingestion job (and associated data).
     */
    override fun writeData(data: IngestionJob): Boolean = createMetricsWriter(databaseTables).write(data) && createJobIdFile(data)

    override fun afterWriteBeforeCommit(connection: Connection): Boolean = true

    private fun createJobIdFile(job: IngestionJob): Boolean {
        if (modelPath == null) return true

        // To avoid multiple job ID files in a single directory, we delete any leftover from previous runs
        modelPath.listDirectoryEntries("*.$JOB_ID_FILE_EXTENSION").forEach { jobIdFile ->
            try {
                jobIdFile.deleteIfExists()
            } catch (e: IOException) {
                logger.error("Could not delete existing job ID file at ${jobIdFile.absolute()}. Please ensure the program has the correct permissions.", e)
            }
        }

        val newJobIdFile = modelPath.resolve("${job.id}.$JOB_ID_FILE_EXTENSION")
        try {
            newJobIdFile.createFile()
        } catch (e: IOException) {
            logger.error("Could not create job ID file at ${newJobIdFile.absolute()}. Please ensure the program has the correct permissions.", e)
            return false
        }
        return true
    }

}

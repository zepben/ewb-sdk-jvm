/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.postgres.metrics

import com.zepben.evolve.database.sql.BaseDatabaseWriter
import com.zepben.evolve.metrics.IngestionJob
import java.io.IOException
import java.nio.file.Path
import java.sql.Connection
import kotlin.io.path.absolute
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries

internal const val JOB_ID_FILE_EXTENSION = "zjid"

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to a metrics database.
 */
class MetricsDatabaseWriter internal constructor(
    getConnection: () -> Connection,
    databaseTables: MetricsDatabaseTables,
    private val modelPath: Path?,
    private val createMetricsWriter: (MetricsDatabaseTables) -> MetricsWriter
) : BaseDatabaseWriter<MetricsDatabaseTables, IngestionJob>(
    databaseTables,
    getConnection
) {

    /**
     * @param getConnection Provider of the connection to the metrics database.
     * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
     *                  named using the UUID of the ingestion job.
     */
    @JvmOverloads
    constructor(
        getConnection: () -> Connection,
        modelPath: Path? = null
    ) : this(getConnection, MetricsDatabaseTables(), modelPath, { MetricsWriter(it) })

    override fun beforeConnect(): Boolean = true

    // Schema will be created by EAS using Liquibase, instead of creating it on-demand here.
    override fun afterConnectBeforePrepare(connection: Connection): Boolean = true

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

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.BaseDatabaseWriter
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.NoOpDatabaseInitialiser
import com.zepben.ewb.metrics.IngestionJob
import com.zepben.ewb.metrics.variants.VariantMetrics
import com.zepben.ewb.services.metrics.MetricsService
import java.io.IOException
import java.nio.file.Path
import java.sql.Connection
import kotlin.io.path.absolute
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries

internal const val JOB_ID_FILE_EXTENSION = "zjid"

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to a metrics database.
 * @property databaseTables The tables to create in the database.
 * @property databaseInitialiser The hooks used to initilise the database.
 * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
 *                  named using the UUID of the ingestion job.
 * @param createMetricsWriter Factory for creating the [MetricsServiceWriter] to use.
 */
class MetricsDatabaseWriter internal constructor(
    override val databaseTables: MetricsDatabaseTables,
    override val databaseInitialiser: DatabaseInitialiser<MetricsDatabaseTables>,
    private val modelPath: Path?,
    private val createMetricsWriter: (MetricsDatabaseTables) -> MetricsWriter,
) : BaseDatabaseWriter<MetricsDatabaseTables>() {

    /**
     * @param getConnection Provider of the connection to the metrics database.
     * @param modelPath The directory containing the output model files for the ingestion jobs. If specified, a file will be created in this directory for each
     *                  injection job in the metrics service and named using the UUID of the ingestion job.
     */
    @JvmOverloads
    constructor(
        getConnection: () -> Connection,
        modelPath: Path? = null
    ) : this(
        MetricsDatabaseTables(),
        NoOpDatabaseInitialiser(getConnection),
        modelPath,
        { MetricsWriter(it) },
    )

    /**
     * Write the ingestion job (and associated data).
     *
     * @param data The [MetricsService] to write.
     * @return true if the [MetricsService] was successfully written, otherwise false.
     */
    fun write(data: IngestionJob): Boolean = connectAndWrite { createMetricsWriter(databaseTables).write(data) } && createJobIdFile(data)

    fun write(data: VariantMetrics): Boolean = connectAndWrite { createMetricsWriter(databaseTables).write(data) }

    private fun createJobIdFile(job: IngestionJob): Boolean {
        if (modelPath == null) return true

        try {
            modelPath.resolve("someFile").createParentDirectories()
        } catch (e: IOException) {
            logger.error("Could not ensure directory $modelPath exists. Please ensure the program has the correct permissions.", e)
            return false
        }

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

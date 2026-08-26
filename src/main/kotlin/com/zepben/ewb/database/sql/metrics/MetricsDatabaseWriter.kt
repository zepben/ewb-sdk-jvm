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
import com.zepben.ewb.metrics.dataquality.DataQualityIssue
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCallout
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCategory
import com.zepben.ewb.metrics.variants.VariantMetrics
import java.io.IOException
import java.nio.file.Path
import java.sql.Connection
import kotlin.io.path.*

internal const val JOB_ID_FILE_EXTENSION = "zjid"

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to a metrics database.
 * @property databaseTables The tables to create in the database.
 * @property databaseInitialiser The hooks used to initilise the database.
 * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
 *                  named using the UUID of the ingestion job.
 * @param createMetricsWriter Factory for creating the [MetricsWriter] to use.
 */
class MetricsDatabaseWriter internal constructor(
    override val databaseTables: MetricsDatabaseTables,
    override val databaseInitialiser: DatabaseInitialiser<MetricsDatabaseTables>,
    private val modelPath: Path?,
    private val createMetricsWriter: (MetricsDatabaseTables) -> MetricsWriter,
) : BaseDatabaseWriter<MetricsDatabaseTables>() {

    /**
     * @param getConnection Provider of the connection to the metrics database.
     * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
     *                  named using the UUID of the ingestion job.
     */
    @JvmOverloads
    constructor(
        getConnection: () -> Connection,
        modelPath: Path? = null,
    ) : this(
        MetricsDatabaseTables(),
        NoOpDatabaseInitialiser(getConnection),
        modelPath,
        { MetricsWriter(it) },
    )

    /**
     * Write the ingestion job (and associated data).
     *
     * @param data The [IngestionJob] to write.
     * @return true if the [IngestionJob] was successfully written, otherwise false.
     */
    fun write(data: IngestionJob): Boolean = connectAndWrite { createMetricsWriter(databaseTables).write(data) } && createJobIdFile(data)

    /**
     * Write the variant metrics related to a particular network project stage id.
     *
     * @param variantMetrics The [VariantMetrics] to write.
     * @return true if the [VariantMetrics] was successfully written, otherwise false.
     */
    fun write(variantMetrics: VariantMetrics): Boolean = connectAndWrite { createMetricsWriter(databaseTables).write(variantMetrics) }

    /**
     * Write a data quality issue category.
     *
     * @param category The [DataQualityIssueCategory] to write.
     * @return true if the [category] was successfully written, otherwise false.
     */
    fun write(category: DataQualityIssueCategory): Boolean = connectAndWrite { createMetricsWriter(databaseTables).write(category) }

    /**
     * Write a data quality issue along with its associated assets and callouts.
     *
     * @param issue The [DataQualityIssue] to write. Associated assets from [DataQualityIssue.associatedAssets] are written automatically.
     * @param callouts The [DataQualityIssueCallout]s to write alongside the issue.
     * @return true if the issue and all related data were successfully written, otherwise false.
     */
    @JvmOverloads
    fun write(issue: DataQualityIssue, callouts: List<DataQualityIssueCallout> = emptyList()): Boolean =
        connectAndWrite { createMetricsWriter(databaseTables).write(issue, callouts) }

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

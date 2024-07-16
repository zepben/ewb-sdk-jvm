/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.sqlite.common.BaseDatabaseWriter
import com.zepben.evolve.metrics.IngestionJob
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to a metrics database.
 *
 * @param databaseFile The filename of the metrics database.
 * @param job The ingestion job to write.
 * @param databaseTables The tables in the database.
 * @param metricsWriter The metrics writer to use.
 * @param modelPath The directory containing the output model files for the ingestion job. If specified, a file will be created in this directory and
 *                  named using the UUID of the ingestion job.
 * @param getConnection Provider of the connection to the specified database.
 */
class MetricsDatabaseWriter @JvmOverloads constructor(
    databaseFile: String,
    private val job: IngestionJob,
    databaseTables: MetricsDatabaseTables = MetricsDatabaseTables(),
    private val metricsWriter: MetricsWriter = MetricsWriter(job, databaseTables),
    private val modelPath: Path? = null,
    getConnection: (String) -> Connection = DriverManager::getConnection
) : BaseDatabaseWriter(databaseFile, databaseTables, getConnection, persistFile = true) {

    /**
     * Save the ingestion job (and associated data).
     */
    override fun saveSchema(): Boolean = metricsWriter.save() && run {
        createJobIdFile()
        true
    }

    private fun createJobIdFile() = modelPath?.resolve(job.id.toString())?.toFile()?.createNewFile()

}

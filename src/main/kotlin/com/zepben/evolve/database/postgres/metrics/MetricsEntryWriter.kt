/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.postgres.metrics

import com.zepben.evolve.database.sql.BaseEntryWriter
import com.zepben.evolve.database.sql.extensions.setInstant
import com.zepben.evolve.database.postgres.metrics.tables.TableJobSources
import com.zepben.evolve.database.postgres.metrics.tables.TableJobs
import com.zepben.evolve.database.postgres.metrics.tables.TableNetworkContainerMetrics
import com.zepben.evolve.metrics.*
import java.util.*

/**
 * Class for writing entries to the metrics database.
 *
 * @param databaseTables The tables in the metrics database.
 */
internal class MetricsEntryWriter(
    private val databaseTables: MetricsDatabaseTables
) : BaseEntryWriter() {

    /**
     * Write an [IngestionMetadata] to the `jobs` table.
     *
     * @param jobId The ID of the job being writen.
     * @param metadata the [IngestionMetadata] to write.
     * @return true if the [metadata] writen successfully.
     */
    fun write(jobId: UUID, metadata: IngestionMetadata): Boolean {
        val table = databaseTables.getTable<TableJobs>()
        val insert = databaseTables.getInsert<TableJobs>()

        insert.setString(table.JOB_ID.queryIndex, jobId.toString())
        insert.setInstant(table.INGEST_TIME.queryIndex, metadata.startTime)
        insert.setString(table.SOURCE.queryIndex, metadata.source)
        insert.setString(table.APPLICATION.queryIndex, metadata.application)
        insert.setString(table.APPLICATION_VERSION.queryIndex, metadata.applicationVersion)

        return insert.tryExecuteSingleUpdate("job")
    }

    /**
     * Write a [JobSource] to the `job_sources` table.
     *
     * @param jobId The ID of the job being writen.
     * @param jobSource The [JobSource] to write.
     * @return true if the [jobSource] was writen successfully.
     */
    fun writeSource(jobId: UUID, jobSource: JobSource): Boolean {
        val table = databaseTables.getTable<TableJobSources>()
        val insert = databaseTables.getInsert<TableJobSources>()
        val (sourceName, sourceMetadata) = jobSource

        insert.setString(table.JOB_ID.queryIndex, jobId.toString())
        insert.setString(table.DATA_SOURCE.queryIndex, sourceName)
        insert.setInstant(table.SOURCE_TIME.queryIndex, sourceMetadata.timestamp)
        insert.setObject(table.FILE_SHA.queryIndex, sourceMetadata.fileHash)

        return insert.tryExecuteSingleUpdate("job source")
    }

    /**
     * Write a [NetworkMetric] to the `job_sources` table.
     *
     * @param jobId The ID of the job being writen.
     * @param networkMetric The [NetworkMetric] to write.
     * @return true if the [networkMetric] was writen successfully.
     */
    fun writeMetric(jobId: UUID, networkMetric: NetworkMetric): Boolean {
        val table = databaseTables.getTable<TableNetworkContainerMetrics>()
        val insert = databaseTables.getInsert<TableNetworkContainerMetrics>()
        val (container, containerMetric) = networkMetric

        insert.setString(table.JOB_ID.queryIndex, jobId.toString())
        when (container) {
            is TotalNetworkContainer -> {
                insert.setString(table.HIERARCHY_ID.queryIndex, "GLOBAL")
                insert.setString(table.HIERARCHY_NAME.queryIndex, "")
                insert.setString(table.CONTAINER_TYPE.queryIndex, "TOTAL")
            }

            is PartialNetworkContainer -> {
                insert.setString(table.HIERARCHY_ID.queryIndex, container.mRID)
                insert.setString(table.HIERARCHY_NAME.queryIndex, container.name)
                insert.setString(table.CONTAINER_TYPE.queryIndex, container.level.name)
            }
        }

        containerMetric.entries.forEach { (metricName, metricValue) ->
            insert.setString(table.METRIC_NAME.queryIndex, metricName)
            insert.setDouble(table.METRIC_VALUE.queryIndex, metricValue)
            insert.addBatch()
        }

        return insert.executeBatch().none { it < 0 }
    }

}

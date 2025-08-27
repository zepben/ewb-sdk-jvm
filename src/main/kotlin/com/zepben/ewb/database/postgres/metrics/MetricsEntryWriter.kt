/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.metrics

import com.zepben.ewb.database.postgres.metrics.tables.TableJobSources
import com.zepben.ewb.database.postgres.metrics.tables.TableJobs
import com.zepben.ewb.database.postgres.metrics.tables.TableNetworkContainerMetrics
import com.zepben.ewb.database.sql.BaseEntryWriter
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.metrics.*
import java.sql.Timestamp
import java.sql.Types.VARCHAR
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
     * @param jobId The ID of the job being written.
     * @param metadata the [IngestionMetadata] to write.
     * @return true if the [metadata] written successfully.
     */
    fun write(jobId: UUID, metadata: IngestionMetadata): Boolean {
        val table = databaseTables.getTable<TableJobs>()
        val insert = databaseTables.getInsert<TableJobs>()

        insert.setObject(table.JOB_ID.queryIndex, jobId)
        insert.setTimestamp(table.INGEST_TIME.queryIndex, Timestamp.from(metadata.startTime))
        insert.setString(table.SOURCE.queryIndex, metadata.source)
        insert.setString(table.APPLICATION.queryIndex, metadata.application)
        insert.setString(table.APPLICATION_VERSION.queryIndex, metadata.applicationVersion)

        return insert.tryExecuteSingleUpdate("job")
    }

    /**
     * Write a [JobSource] to the `job_sources` table.
     *
     * @param jobId The ID of the job being written.
     * @param jobSource The [JobSource] to write.
     * @return true if the [jobSource] was written successfully.
     */
    fun writeSource(jobId: UUID, jobSource: JobSource): Boolean {
        val table = databaseTables.getTable<TableJobSources>()
        val insert = databaseTables.getInsert<TableJobSources>()
        val (sourceName, sourceMetadata) = jobSource

        insert.setObject(table.JOB_ID.queryIndex, jobId)
        insert.setString(table.DATA_SOURCE.queryIndex, sourceName)
        insert.setTimestamp(table.SOURCE_TIME.queryIndex, sourceMetadata.timestamp?.let { Timestamp.from(it) })
        insert.setObject(table.FILE_SHA.queryIndex, sourceMetadata.fileHash)

        return insert.tryExecuteSingleUpdate("job source")
    }

    /**
     * Write a [NetworkMetric] to the `job_sources` table.
     *
     * @param jobId The ID of the job being written.
     * @param networkMetric The [NetworkMetric] to write.
     * @return true if the [networkMetric] was written successfully.
     */
    fun writeMetric(jobId: UUID, networkMetric: NetworkMetric): Boolean {
        val table = databaseTables.getTable<TableNetworkContainerMetrics>()
        val insert = databaseTables.getInsert<TableNetworkContainerMetrics>()
        val (container, containerMetric) = networkMetric

        insert.setObject(table.JOB_ID.queryIndex, jobId)
        when (container) {
            is TotalNetworkContainer -> {
                insert.setString(table.HIERARCHY_ID.queryIndex, "GLOBAL")
                insert.setNull(table.HIERARCHY_NAME.queryIndex, VARCHAR)
                insert.setString(table.CONTAINER_TYPE.queryIndex, "TOTAL")
            }

            is PartialNetworkContainer -> {
                insert.setString(table.HIERARCHY_ID.queryIndex, container.mRID)
                insert.setNullableString(table.HIERARCHY_NAME.queryIndex, container.name)
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

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.BaseEntryWriter
import com.zepben.ewb.database.sql.extensions.setNullableInt
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.database.sql.metrics.tables.*
import com.zepben.ewb.metrics.*
import com.zepben.ewb.metrics.dataquality.DataQualityIssue
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCallout
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCategory
import com.zepben.ewb.metrics.variants.VariantMetricEntry
import kotlinx.serialization.json.Json
import java.sql.Timestamp
import java.sql.Types.VARCHAR
import java.util.*

/**
 * Class for writing entries to the metrics database.
 *
 * @param databaseTables The tables in the metrics database.
 * @param jsonMapper Used to encode objects to JSON Strings
 */
internal class MetricsEntryWriter(
    private val databaseTables: MetricsDatabaseTables,
    private val jsonMapper: Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
        isLenient = false
    },
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

    /**
     * Write a [VariantMetricEntry] to the `variant_metrics` table.
     *
     * @param networkModelProjectId The network model project ID of the variant metric to be written.
     * @param networkModelProjectStageId The network model project stage ID of the variant metric to be written.
     * @param baseModelVersion The base model version of the variant metric to be written.
     * @param baseModelVersion The change set of the variant metric to be written.
     * @param variantMetricEntry The [VariantMetricEntry] to write.
     * @return true if the [VariantMetricEntry] was written successfully.
     */
    fun writeVariantMetricEntry(
        networkModelProjectId: String,
        networkModelProjectStageId: String,
        baseModelVersion: String,
        changeSetId: String,
        variantMetricEntry: VariantMetricEntry,
    ): Boolean {
        val table = databaseTables.getTable<TableVariantMetrics>()
        val insert = databaseTables.getInsert<TableVariantMetrics>()


        insert.setString(table.NETWORK_MODEL_PROJECT_ID.queryIndex, networkModelProjectId)
        insert.setString(table.NETWORK_MODEL_PROJECT_STAGE_ID.queryIndex, networkModelProjectStageId)
        insert.setString(table.BASE_MODEL_VERSION.queryIndex, baseModelVersion)
        insert.setString(table.CHANGE_SET_ID.queryIndex, changeSetId)
        insert.setString(table.TYPE.queryIndex, variantMetricEntry.type.name)
        insert.setString(table.NAME.queryIndex, variantMetricEntry.name)
        insert.setInt(table.METRIC_VALUE.queryIndex, variantMetricEntry.value)
        insert.setString(table.METADATA.queryIndex, jsonMapper.encodeToString(variantMetricEntry.metadata))

        return insert.tryExecuteSingleUpdate("variant metric")
    }

    /**
     * Write a [DataQualityIssueCategory] to the `data_quality_issue_categories` table.
     *
     * @param category The [DataQualityIssueCategory] to write.
     * @return true if the [category] was written successfully.
     */
    fun writeDataQualityIssueCategory(category: DataQualityIssueCategory): Boolean {
        val table = databaseTables.getTable<TableDataQualityIssueCategories>()
        val insert = databaseTables.getInsert<TableDataQualityIssueCategories>()

        insert.setObject(table.ID.queryIndex, UUID.fromString(category.id))
        insert.setString(table.NAME.queryIndex, category.name)
        insert.setNullableString(table.DESCRIPTION.queryIndex, category.description)

        return insert.tryExecuteSingleUpdate("data quality issue category")
    }

    /**
     * Write a [DataQualityIssue] to the `data_quality_issues` table.
     *
     * @param issue The [DataQualityIssue] to write.
     * @return true if the [issue] was written successfully.
     */
    fun writeDataQualityIssue(issue: DataQualityIssue): Boolean {
        val table = databaseTables.getTable<TableDataQualityIssues>()
        val insert = databaseTables.getInsert<TableDataQualityIssues>()

        insert.setObject(table.ID.queryIndex, UUID.fromString(issue.id))
        insert.setString(table.STATUS.queryIndex, issue.status.name)
        insert.setTimestamp(table.CREATED_AT.queryIndex, Timestamp.valueOf(issue.createdAt.replace("T", " ")))
        insert.setString(table.CREATED_BY.queryIndex, issue.createdBy)
        insert.setTimestamp(table.UPDATED_AT.queryIndex, Timestamp.valueOf(issue.updatedAt.replace("T", " ")))
        insert.setString(table.UPDATED_BY.queryIndex, issue.updatedBy)
        insert.setString(table.NETWORK_MODEL_CREATED_AGAINST.queryIndex, issue.networkModelCreatedAgainst)
        insert.setNullableString(table.NETWORK_MODEL_RESOLVED_AGAINST.queryIndex, issue.networkModelResolvedAgainst)
        insert.setString(table.NAME.queryIndex, issue.name)
        insert.setString(table.DESCRIPTION.queryIndex, issue.description)
        insert.setNullableString(table.SUGGESTED_RESOLUTION.queryIndex, issue.suggestedResolution)
        insert.setString(table.ANNOTATION_GEO_JSON.queryIndex, issue.annotationGeoJson)
        insert.setObject(table.CATEGORY.queryIndex, UUID.fromString(issue.categoryId))
        insert.setNullableString(table.EXTERNAL_REFERENCE.queryIndex, issue.externalReference)
        insert.setInt(table.SEVERITY.queryIndex, issue.severity)
        insert.setInt(table.PRIORITY.queryIndex, issue.priority)

        return insert.tryExecuteSingleUpdate("data quality issue")
    }

    /**
     * Write an asset association row to the `data_quality_issue_assets` table.
     *
     * @param issueId The parent issue ID.
     * @param assetMrid The mRID of the associated asset.
     * @return true if the row was written successfully.
     */
    fun writeDataQualityIssueAsset(issueId: String, assetMrid: String): Boolean {
        val table = databaseTables.getTable<TableDataQualityIssueAssets>()
        val insert = databaseTables.getInsert<TableDataQualityIssueAssets>()

        insert.setObject(table.ID.queryIndex, UUID.randomUUID())
        insert.setObject(table.DATA_QUALITY_ISSUE_ID.queryIndex, UUID.fromString(issueId))
        insert.setString(table.ASSET_MRID.queryIndex, assetMrid)

        return insert.tryExecuteSingleUpdate("data quality issue asset")
    }

    /**
     * Write a [DataQualityIssueCallout] to the `data_quality_issue_callouts` table.
     *
     * @param callout The [DataQualityIssueCallout] to write.
     * @return true if the [callout] was written successfully.
     */
    fun writeDataQualityIssueCallout(callout: DataQualityIssueCallout): Boolean {
        val table = databaseTables.getTable<TableDataQualityIssueCallouts>()
        val insert = databaseTables.getInsert<TableDataQualityIssueCallouts>()

        insert.setObject(table.ID.queryIndex, UUID.fromString(callout.id))
        insert.setObject(table.DATA_QUALITY_ISSUE_ID.queryIndex, UUID.fromString(callout.dataQualityIssueId))
        insert.setDouble(table.LONGITUDE.queryIndex, callout.longitude)
        insert.setDouble(table.LATITUDE.queryIndex, callout.latitude)
        insert.setDouble(table.POSITION_X.queryIndex, callout.positionX)
        insert.setDouble(table.POSITION_Y.queryIndex, callout.positionY)
        insert.setDouble(table.WIDTH.queryIndex, callout.width)
        insert.setDouble(table.HEIGHT.queryIndex, callout.height)
        insert.setNullableString(table.LABEL.queryIndex, callout.label)
        insert.setNullableString(table.DESCRIPTION.queryIndex, callout.description)
        insert.setString(table.COLOUR.queryIndex, callout.colour)

        return insert.tryExecuteSingleUpdate("data quality issue callout")
    }
}

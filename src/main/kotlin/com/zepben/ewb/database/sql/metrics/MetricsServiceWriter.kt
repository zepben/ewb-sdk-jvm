package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.BaseCollectionWriter
import com.zepben.ewb.metrics.IngestionJob
import com.zepben.ewb.metrics.variants.VariantMetrics
import com.zepben.ewb.services.metrics.MetricsService


/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to the tables in a metrics database.
 *
 * @param databaseTables The tables in the database. Insert statements should be prepared for each table.
 * @param writer the [MetricsEntryWriter] to use for writing each entry.
 */
internal class MetricsServiceWriter(
    databaseTables: MetricsDatabaseTables,
    private val writer: MetricsEntryWriter = MetricsEntryWriter(databaseTables)
) : BaseCollectionWriter<MetricsService>() {

    override fun write(data: MetricsService): Boolean {
        var successful = true
        data.variantMetrics.forEach { if (!write(it)) successful = false }
        data.variantMetrics.forEach { if (!write(it)) successful = false }
        return successful
    }

    fun write(data: IngestionJob): Boolean =
        writer.write(data.id, data.metadata) and
            writeEach(data.sources.entries, { writer.writeSource(data.id, it) }) { jobSource, e ->
                logger.error("Failed to write job source $jobSource: ${e.message}")
            } and
            writeEach(data.networkMetrics.entries, { writer.writeMetric(data.id, it) }) { metric, e ->
                logger.error("Failed to write metric $metric: ${e.message}")
            }

    fun write(data: VariantMetrics): Boolean =
        writeEach(data.metrics, { writer.writeVariantMetricEntry(data.networkModelProjectId, data.networkModelProjectStageId, data.baseModelVersion, it) }) { metricEntry, e ->
            logger.error("Failed to write metric entry $metricEntry: ${e.message}")
        }
}

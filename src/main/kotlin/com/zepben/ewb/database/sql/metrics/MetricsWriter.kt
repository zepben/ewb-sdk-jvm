/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.BaseCollectionWriter
import com.zepben.ewb.metrics.IngestionJob
import com.zepben.ewb.metrics.variants.VariantMetrics

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to the tables in a metrics database.
 *
 * @param databaseTables The tables in the database. Insert statements should be prepared for each table.
 * @param writer the [MetricsEntryWriter] to use for writing each entry.
 */
internal class MetricsWriter(
    databaseTables: MetricsDatabaseTables,
    private val writer: MetricsEntryWriter = MetricsEntryWriter(databaseTables)
) : BaseCollectionWriter() {

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

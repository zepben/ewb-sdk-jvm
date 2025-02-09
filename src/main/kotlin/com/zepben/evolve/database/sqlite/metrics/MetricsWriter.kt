/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.sqlite.common.BaseCollectionWriter
import com.zepben.evolve.metrics.IngestionJob

/**
 * Class for writing an ingestion job (and associated metadata, metrics, and sources) to the tables in a metrics database.
 *
 * @param databaseTables The tables in the database. Insert statements should be prepared for each table.
 * @param writer the [MetricsEntryWriter] to use for writing each entry.
 */
internal class MetricsWriter(
    databaseTables: MetricsDatabaseTables,
    private val writer: MetricsEntryWriter = MetricsEntryWriter(databaseTables)
) : BaseCollectionWriter<IngestionJob>() {

    override fun write(data: IngestionJob): Boolean =
        writer.write(data.id, data.metadata) and
            writeEach(data.sources.entries, { writer.writeSource(data.id, it) }) { jobSource, e ->
                logger.error("Failed to write job source $jobSource: ${e.message}")
            } and
            writeEach(data.networkMetrics.entries, { writer.writeMetric(data.id, it) }) { metric, e ->
                logger.error("Failed to write metric $metric: ${e.message}")
            }

}

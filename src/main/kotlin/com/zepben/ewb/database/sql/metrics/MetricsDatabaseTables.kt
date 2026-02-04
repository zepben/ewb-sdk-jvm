/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.generators.PostgresGenerator
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.metrics.tables.*

/**
 * The collection of tables for our metrics databases.
 */
class MetricsDatabaseTables internal constructor(
    override val sqlGenerator: SqlGenerator = PostgresGenerator
) : BaseDatabaseTables() {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            tableMetricsVersion,
            TableJobs(),
            TableJobSources(),
            TableNetworkContainerMetrics(),
            TableVariantMetrics()
        )

}

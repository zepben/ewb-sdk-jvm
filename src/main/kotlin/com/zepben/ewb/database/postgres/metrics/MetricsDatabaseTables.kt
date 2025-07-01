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
import com.zepben.ewb.database.postgres.metrics.tables.tableMetricsVersion
import com.zepben.ewb.database.sql.BaseDatabaseTables
import com.zepben.ewb.database.sql.SqlTable

/**
 * The collection of tables for our metrics databases.
 */
class MetricsDatabaseTables internal constructor() : BaseDatabaseTables() {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            tableMetricsVersion,
            TableJobs(),
            TableJobSources(),
            TableNetworkContainerMetrics()
        )

}

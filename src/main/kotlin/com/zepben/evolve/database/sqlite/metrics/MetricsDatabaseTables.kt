/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.sql.SqlTable
import com.zepben.evolve.database.sqlite.common.BaseDatabaseTables
import com.zepben.evolve.database.sqlite.metrics.tables.TableJobSources
import com.zepben.evolve.database.sqlite.metrics.tables.TableJobs
import com.zepben.evolve.database.sqlite.metrics.tables.TableNetworkContainerMetrics
import com.zepben.evolve.database.sqlite.metrics.tables.tableMetricsVersion

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

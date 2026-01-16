/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics.tables

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `NetworkMetric` columns required for the database table.
 *
 * @property HIERARCHY_ID The ID of the hierarchy area these metrics are for.
 * @property HIERARCHY_NAME The name of the hierarchy area these metrics are for.
 * @property CONTAINER_TYPE The container level name.
 * @property METRIC_NAME The key for the metric.
 * @property METRIC_VALUE The value for the metric.
 */
@Suppress("PropertyName")
class TableNetworkContainerMetrics : MultiJobTable() {

    val HIERARCHY_ID: Column = Column(++columnIndex, "hierarchy_id", Column.Type.STRING, NOT_NULL)
    val HIERARCHY_NAME: Column = Column(++columnIndex, "hierarchy_name", Column.Type.STRING, NULL)
    val CONTAINER_TYPE: Column = Column(++columnIndex, "container_type", Column.Type.STRING, NOT_NULL)
    val METRIC_NAME: Column = Column(++columnIndex, "metric_name", Column.Type.STRING, NOT_NULL)
    val METRIC_VALUE: Column = Column(++columnIndex, "metric_value", Column.Type.DOUBLE, NOT_NULL)

    override val name: String = "network_container_metrics"

    init {
        addUniqueIndexes(
            listOf(JOB_ID, HIERARCHY_ID, CONTAINER_TYPE, METRIC_NAME)
        )

        addNonUniqueIndexes(
            listOf(HIERARCHY_ID, CONTAINER_TYPE, METRIC_NAME)
        )
    }

}

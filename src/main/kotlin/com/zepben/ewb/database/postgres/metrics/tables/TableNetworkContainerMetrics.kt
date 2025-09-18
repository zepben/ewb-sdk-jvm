/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.metrics.tables

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableNetworkContainerMetrics : MultiJobTable() {

    val HIERARCHY_ID: Column = Column(++columnIndex, "hierarchy_id", "TEXT", NOT_NULL)
    val HIERARCHY_NAME: Column = Column(++columnIndex, "hierarchy_name", "TEXT", NULL)
    val CONTAINER_TYPE: Column = Column(++columnIndex, "container_type", "TEXT", NOT_NULL)
    val METRIC_NAME: Column = Column(++columnIndex, "metric_name", "TEXT", NOT_NULL)
    val METRIC_VALUE: Column = Column(++columnIndex, "metric_value", "DOUBLE PRECISION", NOT_NULL)

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

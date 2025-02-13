/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.postgres.metrics.tables

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableNetworkContainerMetrics : MultiJobTable() {

    val HIERARCHY_ID: Column = Column(++columnIndex, "hierarchy_id", "TEXT", NOT_NULL)
    val HIERARCHY_NAME: Column = Column(++columnIndex, "hierarchy_name", "TEXT", NOT_NULL)
    val CONTAINER_TYPE: Column = Column(++columnIndex, "container_type", "TEXT", NOT_NULL)
    val METRIC_NAME: Column = Column(++columnIndex, "metric_name", "TEXT", NOT_NULL)
    val METRIC_VALUE: Column = Column(++columnIndex, "metric_value", "DOUBLE PRECISION", NOT_NULL)

    override val uniqueIndexColumns: MutableList<List<Column>> = mutableListOf(
        listOf(JOB_ID, HIERARCHY_ID, CONTAINER_TYPE, METRIC_NAME)
    )

    override val nonUniqueIndexColumns: MutableList<List<Column>> = mutableListOf(
        listOf(HIERARCHY_ID, CONTAINER_TYPE, METRIC_NAME)
    )

    override val name: String = "network_container_metrics"

}

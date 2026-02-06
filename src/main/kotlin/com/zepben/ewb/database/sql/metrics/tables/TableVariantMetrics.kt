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
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `VariantMetrics` columns required for the database table.
 *
 * @property NETWORK_MODEL_PROJECT_ID The ID of the network model project this metric is for.
 * @property NETWORK_MODEL_PROJECT_STAGE_ID The ID of the network model stage this metric is for.
 * @property BASE_MODEL_VERSION The base network model this network project stage was generated against.
 * @property TYPE The type of metric [CONFLICT|ASSET].
 * @property NAME The name of the metric.
 * @property METRIC_VALUE The value of the metric.
 * @property METADATA List of strings containing extra information related to the metric.
 */
@Suppress("PropertyName")
class TableVariantMetrics : SqlTable() {

    val NETWORK_MODEL_PROJECT_ID: Column = Column(++columnIndex, "network_model_project_id", Column.Type.STRING, NOT_NULL)
    val NETWORK_MODEL_PROJECT_STAGE_ID: Column = Column(++columnIndex, "network_model_project_stage_id", Column.Type.STRING, NOT_NULL)
    val BASE_MODEL_VERSION: Column = Column(++columnIndex, "base_model_version", Column.Type.STRING, NOT_NULL)
    val TYPE: Column = Column(++columnIndex, "type", Column.Type.STRING, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val METRIC_VALUE: Column = Column(++columnIndex, "metric_value", Column.Type.INTEGER, NOT_NULL)
    val METADATA: Column = Column(++columnIndex, "metadata", Column.Type.STRING, NOT_NULL)

    override val name: String = "variant_metrics"

    init {
        addUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_ID, NETWORK_MODEL_PROJECT_STAGE_ID, BASE_MODEL_VERSION, TYPE, NAME)
        )

        addNonUniqueIndexes()
    }
}

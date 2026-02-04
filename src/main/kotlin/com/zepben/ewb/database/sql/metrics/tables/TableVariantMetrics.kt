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
 * A class representing the `VarientMetrics` columns required for the database table.
 *
 * @property NETWORK_MODEL_PROJECT_ID The time the source was exported from the source system.
 * @property NETWORK_MODEL_PROJECT_STAGE_ID The time the source was exported from the source system.
 * @property BASE_MODEL_VERSION The time the source was exported from the source system.
 * @property TYPE The time the source was exported from the source system.
 * @property NAME The time the source was exported from the source system.
 * @property VALUE The time the source was exported from the source system.
 * @property METADATA The time the source was exported from the source system.
 */
@Suppress("PropertyName")
class TableVariantMetrics : SqlTable() {

    val NETWORK_MODEL_PROJECT_ID: Column = Column(++columnIndex, "network_model_project_id", Column.Type.STRING, NOT_NULL)
    val NETWORK_MODEL_PROJECT_STAGE_ID: Column = Column(++columnIndex, "network_model_project_stage_id", Column.Type.STRING, NOT_NULL)
    val BASE_MODEL_VERSION: Column = Column(++columnIndex, "base_model_version", Column.Type.STRING, NOT_NULL)
    val TYPE: Column = Column(++columnIndex, "type", Column.Type.STRING, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val VALUE: Column = Column(++columnIndex, "value", Column.Type.INTEGER, NOT_NULL)
    val METADATA: Column = Column(++columnIndex, "metadata", Column.Type.STRING, NOT_NULL) // TODO: nullable?

    override val name: String = "variant_metrics"

    init {
        addUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_ID, NETWORK_MODEL_PROJECT_STAGE_ID, BASE_MODEL_VERSION, TYPE, NAME) // TODO: type?
        )

        addNonUniqueIndexes()
    }
}

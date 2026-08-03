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
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `data_quality_issues` columns required for the database table.
 *
 * @property ID Unique identifier for the issue.
 * @property STATUS Current issue status (CREATED, IN_PROGRESS, BLOCKED, RESOLVED).
 * @property CREATED_AT Timestamp when the issue was created.
 * @property CREATED_BY Email of the user who created the issue.
 * @property UPDATED_AT Timestamp when the issue was last updated.
 * @property UPDATED_BY Email of the user who last updated the issue.
 * @property NETWORK_MODEL_CREATED_AGAINST Identifier of the network model the issue was created against.
 * @property NETWORK_MODEL_RESOLVED_AGAINST Identifier of the network model the issue was resolved against.
 * @property NAME Name/title of the issue.
 * @property DESCRIPTION Detailed description of the issue.
 * @property SUGGESTED_RESOLUTION Optional suggested resolution.
 * @property ANNOTATION_GEO_JSON GeoJSON string representing geographic annotations.
 * @property CATEGORY FK to the data_quality_issue_categories table.
 * @property EXTERNAL_REFERENCE Optional external reference (e.g. ticket ID).
 * @property SEVERITY Numeric severity level.
 * @property PRIORITY Optional numeric priority.
 */
@Suppress("PropertyName")
class TableDataQualityIssues : SqlTable() {

    val ID: Column = Column(++columnIndex, "id", Column.Type.UUID, NOT_NULL)
    val STATUS: Column = Column(++columnIndex, "status", Column.Type.STRING, NOT_NULL)
    val CREATED_AT: Column = Column(++columnIndex, "created_at", Column.Type.TIMESTAMP, NOT_NULL)
    val CREATED_BY: Column = Column(++columnIndex, "created_by", Column.Type.STRING, NOT_NULL)
    val UPDATED_AT: Column = Column(++columnIndex, "updated_at", Column.Type.TIMESTAMP, NOT_NULL)
    val UPDATED_BY: Column = Column(++columnIndex, "updated_by", Column.Type.STRING, NOT_NULL)
    val NETWORK_MODEL_CREATED_AGAINST: Column = Column(++columnIndex, "network_model_created_against", Column.Type.STRING, NOT_NULL)
    val NETWORK_MODEL_RESOLVED_AGAINST: Column = Column(++columnIndex, "network_model_resolved_against", Column.Type.STRING, NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NOT_NULL)
    val SUGGESTED_RESOLUTION: Column = Column(++columnIndex, "suggested_resolution", Column.Type.STRING, NULL)
    val ANNOTATION_GEO_JSON: Column = Column(++columnIndex, "annotation_geo_json", Column.Type.STRING, NOT_NULL)
    val CATEGORY: Column = Column(++columnIndex, "category", Column.Type.UUID, NOT_NULL)
    val EXTERNAL_REFERENCE: Column = Column(++columnIndex, "external_reference", Column.Type.STRING, NULL)
    val SEVERITY: Column = Column(++columnIndex, "severity", Column.Type.INTEGER, NOT_NULL)
    val PRIORITY: Column = Column(++columnIndex, "priority", Column.Type.INTEGER, NOT_NULL)

    override val name: String = "data_quality_issues"

    init {
        addUniqueIndexes(
            listOf(ID),
        )

        addNonUniqueIndexes(
            listOf(STATUS),
            listOf(CATEGORY),
            listOf(SEVERITY),
        )
    }
}

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
 * A class representing the `data_quality_issue_callouts` table, following the
 * callout pattern from ednar.
 *
 * @property ID Unique identifier for the callout.
 * @property DATA_QUALITY_ISSUE_ID FK to the parent data quality issue.
 * @property LONGITUDE Geographic longitude where the callout stem points.
 * @property LATITUDE Geographic latitude where the callout stem points.
 * @property POSITION_X Screen x position of the callout box as a percentage of width.
 * @property POSITION_Y Screen y position of the callout box as a percentage of height.
 * @property WIDTH Width of the callout box in pixels.
 * @property HEIGHT Height of the callout box in pixels.
 * @property LABEL Optional short label for the callout.
 * @property DESCRIPTION Optional free text description.
 * @property COLOUR Hex colour code for the callout.
 */
@Suppress("PropertyName")
class TableDataQualityIssueCallouts : SqlTable() {

    val ID: Column = Column(++columnIndex, "id", Column.Type.UUID, NOT_NULL)
    val DATA_QUALITY_ISSUE_ID: Column = Column(++columnIndex, "data_quality_issue_id", Column.Type.UUID, NOT_NULL)
    val LONGITUDE: Column = Column(++columnIndex, "longitude", Column.Type.DOUBLE, NOT_NULL)
    val LATITUDE: Column = Column(++columnIndex, "latitude", Column.Type.DOUBLE, NOT_NULL)
    val POSITION_X: Column = Column(++columnIndex, "position_x", Column.Type.DOUBLE, NOT_NULL)
    val POSITION_Y: Column = Column(++columnIndex, "position_y", Column.Type.DOUBLE, NOT_NULL)
    val WIDTH: Column = Column(++columnIndex, "width", Column.Type.DOUBLE, NOT_NULL)
    val HEIGHT: Column = Column(++columnIndex, "height", Column.Type.DOUBLE, NOT_NULL)
    val LABEL: Column = Column(++columnIndex, "label", Column.Type.STRING, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NULL)
    val COLOUR: Column = Column(++columnIndex, "colour", Column.Type.STRING, NOT_NULL)

    override val name: String = "data_quality_issue_callouts"

    init {
        addUniqueIndexes(
            listOf(ID),
        )

        addNonUniqueIndexes(
            listOf(DATA_QUALITY_ISSUE_ID),
        )
    }
}

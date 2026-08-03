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
 * A class representing the `data_quality_issue_assets` join table linking issues to asset mRIDs.
 *
 * @property ID Unique identifier for the row.
 * @property DATA_QUALITY_ISSUE_ID FK to the parent data quality issue.
 * @property ASSET_MRID The mRID of the associated network asset.
 */
@Suppress("PropertyName")
class TableDataQualityIssueAssets : SqlTable() {

    val ID: Column = Column(++columnIndex, "id", Column.Type.UUID, NOT_NULL)
    val DATA_QUALITY_ISSUE_ID: Column = Column(++columnIndex, "data_quality_issue_id", Column.Type.UUID, NOT_NULL)
    val ASSET_MRID: Column = Column(++columnIndex, "asset_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "data_quality_issue_assets"

    init {
        addUniqueIndexes(
            listOf(ID),
        )

        addNonUniqueIndexes(
            listOf(DATA_QUALITY_ISSUE_ID),
        )
    }
}

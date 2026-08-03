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
 * A class representing the `data_quality_issue_categories` columns required for the database table.
 *
 * @property ID Unique identifier for the category.
 * @property NAME Name of the category.
 * @property DESCRIPTION Optional description providing additional details about the category.
 */
@Suppress("PropertyName")
class TableDataQualityIssueCategories : SqlTable() {
    
    val ID: Column = Column(++columnIndex, "id", Column.Type.UUID, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NULL)

    override val name: String = "data_quality_issue_categories"

    init {
        addUniqueIndexes(
            listOf(ID),
        )

        addNonUniqueIndexes(
            listOf(NAME),
        )
    }
}

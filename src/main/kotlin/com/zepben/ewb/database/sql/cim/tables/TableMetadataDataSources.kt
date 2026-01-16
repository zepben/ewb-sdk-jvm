/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `Metadata.DataSource` columns required for the database table.
 *
 * @property SOURCE The name of the data source.
 * @property VERSION The version of the data source.
 * @property TIMESTAMP The date/time when the data source was added.
 */
@Suppress("PropertyName")
class TableMetadataDataSources : SqlTable() {

    val SOURCE: Column = Column(++columnIndex, "source", Column.Type.STRING, NOT_NULL)
    val VERSION: Column = Column(++columnIndex, "version", Column.Type.STRING, NOT_NULL)
    val TIMESTAMP: Column = Column(++columnIndex, "timestamp", Column.Type.STRING, NOT_NULL)

    override val name: String = "metadata_data_sources"

}

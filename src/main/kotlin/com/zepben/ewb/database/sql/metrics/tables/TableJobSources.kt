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
 * A class representing the `JobSource` columns required for the database table.
 *
 * @property DATA_SOURCE The name of the data that was processed.
 * @property SOURCE_TIME The time the source was exported from the source system.
 * @property FILE_SHA SHA-256 of the file, if applicable.
 */
@Suppress("PropertyName")
class TableJobSources : MultiJobTable() {

    val DATA_SOURCE: Column = Column(++columnIndex, "data_source", Column.Type.STRING, NOT_NULL)
    val SOURCE_TIME: Column = Column(++columnIndex, "source_time", Column.Type.TIMESTAMP, NULL)
    val FILE_SHA: Column = Column(++columnIndex, "file_sha", Column.Type.BYTES, NULL)

    override val name: String = "job_sources"

    init {
        addUniqueIndexes(
            listOf(JOB_ID, DATA_SOURCE)
        )

        addNonUniqueIndexes(
            listOf(FILE_SHA)
        )
    }

}

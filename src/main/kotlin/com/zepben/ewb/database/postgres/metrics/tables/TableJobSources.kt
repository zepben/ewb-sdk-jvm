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
class TableJobSources : MultiJobTable() {

    val DATA_SOURCE: Column = Column(++columnIndex, "data_source", "TEXT", NOT_NULL)
    val SOURCE_TIME: Column = Column(++columnIndex, "source_time", "TIMESTAMP", NULL)
    val FILE_SHA: Column = Column(++columnIndex, "file_sha", "BYTEA", NULL)

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

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

@Suppress("PropertyName")
class TableJobs : MultiJobTable() {

    val INGEST_TIME: Column = Column(++columnIndex, "ingest_time", Column.Type.TIMESTAMP, NOT_NULL)
    val SOURCE: Column = Column(++columnIndex, "source", Column.Type.STRING, NOT_NULL)
    val APPLICATION: Column = Column(++columnIndex, "application", Column.Type.STRING, NOT_NULL)
    val APPLICATION_VERSION: Column = Column(++columnIndex, "application_version", Column.Type.STRING, NOT_NULL)

    override val name: String = "jobs"

    init {
        addUniqueIndexes(
            listOf(JOB_ID)
        )

        addNonUniqueIndexes(
            listOf(INGEST_TIME),
            listOf(SOURCE),
            listOf(APPLICATION, APPLICATION_VERSION)
        )
    }

}

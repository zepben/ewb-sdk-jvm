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

/**
 * A class representing the `IngestionMetadata` columns required for the database table.
 *
 * @property INGEST_TIME Timestamp for when the run started.
 * @property SOURCE A string describing the source data for the run (e.g. "ExampleEnergy full HV/LV 2024-01-02 cut").
 * @property APPLICATION The application used to ingest the source data.
 * @property APPLICATION_VERSION The version of the ingestion application.
 */
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

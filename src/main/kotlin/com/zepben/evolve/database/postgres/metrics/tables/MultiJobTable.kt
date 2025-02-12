/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.postgres.metrics.tables

import com.zepben.evolve.database.postgres.common.PostgresTable
import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NOT_NULL

/**
 * Table that has ingestion job ID as its primary key or part of its composite key.
 */
@Suppress("PropertyName")
abstract class MultiJobTable : PostgresTable() {

    val JOB_ID: Column = Column(++columnIndex, "job_id", "TEXT", NOT_NULL)

}

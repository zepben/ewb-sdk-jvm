/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
abstract class TableTownDetails : SqliteTable() {

    val TOWN_NAME: Column = Column(++columnIndex, "town_name", "TEXT", NULL)
    val STATE_OR_PROVINCE: Column = Column(++columnIndex, "state_or_province", "TEXT", NULL)

}

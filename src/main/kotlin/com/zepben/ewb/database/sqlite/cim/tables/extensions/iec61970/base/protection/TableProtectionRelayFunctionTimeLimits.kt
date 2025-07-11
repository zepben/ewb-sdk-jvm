/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
class TableProtectionRelayFunctionTimeLimits : SqliteTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val TIME_LIMIT: Column = Column(++columnIndex, "time_limit", "NUMBER", NOT_NULL)

    override val name: String = "protection_relay_function_time_limits"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(PROTECTION_RELAY_FUNCTION_MRID, SEQUENCE_NUMBER))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(PROTECTION_RELAY_FUNCTION_MRID))
        }

}

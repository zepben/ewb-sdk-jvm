/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableProtectionRelayFunctionTimeLimits : SqliteTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val TIME_LIMIT: Column = Column(++columnIndex, "time_limit", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "protection_relay_function_time_limits"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_FUNCTION_MRID, SEQUENCE_NUMBER))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_FUNCTION_MRID))

        return cols
    }

    override val tableClass: Class<TableProtectionRelayFunctionTimeLimits> = this.javaClass
    override val tableClassInstance: TableProtectionRelayFunctionTimeLimits = this

}

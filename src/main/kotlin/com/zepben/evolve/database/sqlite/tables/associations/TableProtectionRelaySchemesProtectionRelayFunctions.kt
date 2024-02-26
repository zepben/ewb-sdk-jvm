/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.associations

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableProtectionRelaySchemesProtectionRelayFunctions : SqliteTable() {

    val PROTECTION_RELAY_SCHEME_MRID: Column = Column(++columnIndex, "protection_relay_scheme_mrid", "TEXT", NOT_NULL)
    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "protection_relay_schemes_protection_relay_functions"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_SCHEME_MRID, PROTECTION_RELAY_FUNCTION_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_SCHEME_MRID))
        cols.add(listOf(PROTECTION_RELAY_FUNCTION_MRID))

        return cols
    }

    override val tableClass: Class<TableProtectionRelaySchemesProtectionRelayFunctions> = this.javaClass
    override val tableClassInstance: TableProtectionRelaySchemesProtectionRelayFunctions = this

}

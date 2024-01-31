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

class TableProtectionRelayFunctionsSensors : SqliteTable() {

    val PROTECTION_RELAY_FUNCTION_MRID = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)
    val SENSOR_MRID = Column(++columnIndex, "sensor_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "protection_relay_functions_sensors"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_FUNCTION_MRID, SENSOR_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(PROTECTION_RELAY_FUNCTION_MRID))
        cols.add(listOf(SENSOR_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
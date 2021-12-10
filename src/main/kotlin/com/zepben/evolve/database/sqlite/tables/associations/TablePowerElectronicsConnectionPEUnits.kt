/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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
class TablePowerElectronicsConnectionPEUnits : SqliteTable() {

    val POWER_ELECTRONICS_CONNECTION_MRID = Column(++columnIndex, "power_electronics_connection_mrid", "TEXT", NOT_NULL)
    val POWER_ELECTRONICS_UNIT_MRID = Column(++columnIndex, "power_electronics_unit_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "power_electronics_connections_power_electronics_units"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(POWER_ELECTRONICS_CONNECTION_MRID, POWER_ELECTRONICS_UNIT_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(POWER_ELECTRONICS_CONNECTION_MRID))
        cols.add(listOf(POWER_ELECTRONICS_UNIT_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

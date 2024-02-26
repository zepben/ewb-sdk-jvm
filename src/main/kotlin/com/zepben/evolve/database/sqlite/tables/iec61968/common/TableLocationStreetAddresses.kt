/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.common

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableLocationStreetAddresses : TableStreetAddresses() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", "TEXT", NOT_NULL)
    val ADDRESS_FIELD: Column = Column(++columnIndex, "address_field", "TEXT", NOT_NULL)

    override fun name(): String {
        return "location_street_addresses"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(LOCATION_MRID, ADDRESS_FIELD))

        return super.uniqueIndexColumns()
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(LOCATION_MRID))

        return cols
    }

    override val tableClass: Class<TableLocationStreetAddresses> = this.javaClass
    override val tableClassInstance: TableLocationStreetAddresses = this

}

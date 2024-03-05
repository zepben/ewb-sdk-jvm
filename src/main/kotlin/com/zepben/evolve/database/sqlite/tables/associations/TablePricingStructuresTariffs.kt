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
class TablePricingStructuresTariffs : SqliteTable() {

    val PRICING_STRUCTURE_MRID: Column = Column(++columnIndex, "pricing_structure_mrid", "TEXT", NOT_NULL)
    val TARIFF_MRID: Column = Column(++columnIndex, "tariff_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "pricing_structures_tariffs"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(PRICING_STRUCTURE_MRID, TARIFF_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(PRICING_STRUCTURE_MRID))
        cols.add(listOf(TARIFF_MRID))

        return cols
    }

    override val tableClass: Class<TablePricingStructuresTariffs> = this.javaClass
    override val tableClassInstance: TablePricingStructuresTariffs = this

}

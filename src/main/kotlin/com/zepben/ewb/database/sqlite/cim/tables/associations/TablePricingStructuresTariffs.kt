/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.associations

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * A class representing the association between PricingStructures and Tariffs.
 *
 * @property PRICING_STRUCTURE_MRID A column storing the mRID of PricingStructures.
 * @property TARIFF_MRID A column storing the mRID of Tariffs.
 */
@Suppress("PropertyName")
class TablePricingStructuresTariffs : SqliteTable() {

    val PRICING_STRUCTURE_MRID: Column = Column(++columnIndex, "pricing_structure_mrid", "TEXT", NOT_NULL)
    val TARIFF_MRID: Column = Column(++columnIndex, "tariff_mrid", "TEXT", NOT_NULL)

    override val name: String = "pricing_structures_tariffs"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(PRICING_STRUCTURE_MRID, TARIFF_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(PRICING_STRUCTURE_MRID))
            add(listOf(TARIFF_MRID))
        }

}

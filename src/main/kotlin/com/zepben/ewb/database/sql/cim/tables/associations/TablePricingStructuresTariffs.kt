/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between PricingStructures and Tariffs.
 *
 * @property PRICING_STRUCTURE_MRID The mRID of PricingStructures.
 * @property TARIFF_MRID The mRID of Tariffs.
 */
@Suppress("PropertyName")
class TablePricingStructuresTariffs : SqlTable() {

    val PRICING_STRUCTURE_MRID: Column = Column(++columnIndex, "pricing_structure_mrid", Column.Type.STRING, NOT_NULL)
    val TARIFF_MRID: Column = Column(++columnIndex, "tariff_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "pricing_structures_tariffs"

    init {
        addUniqueIndexes(
            listOf(PRICING_STRUCTURE_MRID, TARIFF_MRID)
        )

        addNonUniqueIndexes(
            listOf(PRICING_STRUCTURE_MRID),
            listOf(TARIFF_MRID)
        )
    }

}

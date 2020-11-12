/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.associations

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableCustomerAgreementsPricingStructures : SqliteTable() {

    val CUSTOMER_AGREEMENT_MRID = Column(++columnIndex, "customer_agreement_mrid", "TEXT", NOT_NULL)
    val PRICING_STRUCTURE_MRID = Column(++columnIndex, "pricing_structure_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "customer_agreements_pricing_structures"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(CUSTOMER_AGREEMENT_MRID, PRICING_STRUCTURE_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(CUSTOMER_AGREEMENT_MRID))
        cols.add(listOf(PRICING_STRUCTURE_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

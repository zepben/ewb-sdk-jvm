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
 * A class representing the association between CustomerAgreements and PricingStructures.
 *
 * @property CUSTOMER_AGREEMENT_MRID The mRID of CustomerAgreements.
 * @property PRICING_STRUCTURE_MRID The mRID of PricingStructures.
 */
@Suppress("PropertyName")
class TableCustomerAgreementsPricingStructures : SqlTable() {

    val CUSTOMER_AGREEMENT_MRID: Column = Column(++columnIndex, "customer_agreement_mrid", Column.Type.STRING, NOT_NULL)
    val PRICING_STRUCTURE_MRID: Column = Column(++columnIndex, "pricing_structure_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "customer_agreements_pricing_structures"

    init {
        addUniqueIndexes(
            listOf(CUSTOMER_AGREEMENT_MRID, PRICING_STRUCTURE_MRID)
        )

        addNonUniqueIndexes(
            listOf(CUSTOMER_AGREEMENT_MRID),
            listOf(PRICING_STRUCTURE_MRID)
        )
    }

}

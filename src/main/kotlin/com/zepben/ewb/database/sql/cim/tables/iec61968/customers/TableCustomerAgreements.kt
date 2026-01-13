/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.customers

import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableAgreements
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `CustomerAgreement` columns required for the database table.
 *
 * @property CUSTOMER_MRID Customer for this agreement.
 */
@Suppress("PropertyName")
class TableCustomerAgreements : TableAgreements() {

    val CUSTOMER_MRID: Column = Column(++columnIndex, "customer_mrid", Column.Type.STRING, NULL)

    override val name: String = "customer_agreements"

    init {
        addNonUniqueIndexes(
            listOf(CUSTOMER_MRID)
        )
    }

}

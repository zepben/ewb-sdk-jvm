/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.customers

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableAgreements

@Suppress("PropertyName")
class TableCustomerAgreements : TableAgreements() {

    val CUSTOMER_MRID: Column = Column(++columnIndex, "customer_mrid", "TEXT", NULL)

    override val name: String = "customer_agreements"

    init {
        addNonUniqueIndexes(
            listOf(CUSTOMER_MRID)
        )
    }

}

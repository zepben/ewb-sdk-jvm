/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.common.TableElectronicAddresses

@Suppress("PropertyName")
class TableContactDetailsElectronicAddresses : TableElectronicAddresses() {

    val CONTACT_DETAILS_ID: Column = Column(++columnIndex, "contact_details_id", "TEXT", NOT_NULL)

    override val name: String = "contact_details_electronic_addresses"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(CONTACT_DETAILS_ID, EMAIL_1))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(CONTACT_DETAILS_ID))
        }

}

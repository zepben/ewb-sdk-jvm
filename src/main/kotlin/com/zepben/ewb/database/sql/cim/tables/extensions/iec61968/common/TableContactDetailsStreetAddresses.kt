/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61968.common

import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableStreetAddresses
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING

/**
 * A class representing the `ContactDetails` to `StreetAddress` association columns required for the database table.
 *
 * @property CONTACT_DETAILS_ID A column that stores the identifier of the contact details associated with the street address.
 */
@Suppress("PropertyName")
class TableContactDetailsStreetAddresses : TableStreetAddresses() {

    val CONTACT_DETAILS_ID: Column = Column(++columnIndex, "contact_details_id", STRING, NOT_NULL)

    override val name: String = "contact_details_street_addresses"

    init {
        addNonUniqueIndexes(
            listOf(CONTACT_DETAILS_ID)
        )
    }

}

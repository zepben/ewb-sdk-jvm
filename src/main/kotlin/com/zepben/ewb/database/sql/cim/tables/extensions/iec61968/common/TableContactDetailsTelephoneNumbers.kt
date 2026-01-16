/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61968.common

import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableTelephoneNumbers
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING

/**
 * A class representing the `ContactDetails` to `TelephoneNumber` association columns required for the database table.
 *
 * @property CONTACT_DETAILS_ID A column that stores the identifier of the contact details associated with the telephone number.
 */
@Suppress("PropertyName")
class TableContactDetailsTelephoneNumbers : TableTelephoneNumbers() {

    val CONTACT_DETAILS_ID: Column = Column(++columnIndex, "contact_details_id", STRING, NOT_NULL)

    override val name: String = "contact_details_telephone_numbers"

    init {
        addNonUniqueIndexes(
            listOf(CONTACT_DETAILS_ID)
        )
    }

}

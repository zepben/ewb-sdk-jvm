/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL

/**
 * A class representing the `Location.StreetAddress` columns required for the database table.
 *
 * @property LOCATION_MRID The location this address is for.
 * @property ADDRESS_FIELD Which address of the location this represents.
 */
@Suppress("PropertyName")
class TableLocationStreetAddresses : TableStreetAddresses() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", Column.Type.STRING, NOT_NULL)
    val ADDRESS_FIELD: Column = Column(++columnIndex, "address_field", Column.Type.STRING, NOT_NULL)

    override val name: String = "location_street_addresses"

    init {
        addUniqueIndexes(
            listOf(LOCATION_MRID, ADDRESS_FIELD)
        )

        addNonUniqueIndexes(
            listOf(LOCATION_MRID)
        )
    }

}

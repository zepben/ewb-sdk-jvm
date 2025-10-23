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
 * A class representing the association between ProtectionRelaySchemes and ProtectionRelayFunctions.
 *
 * @property PROTECTION_RELAY_SCHEME_MRID A column storing the mRID of ProtectionRelaySchemes.
 * @property PROTECTION_RELAY_FUNCTION_MRID A column storing the mRID of ProtectionRelayFunctions.
 */
@Suppress("PropertyName")
class TableProtectionRelaySchemesProtectionRelayFunctions : SqliteTable() {

    val PROTECTION_RELAY_SCHEME_MRID: Column = Column(++columnIndex, "protection_relay_scheme_mrid", Column.Type.STRING, NOT_NULL)
    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "protection_relay_schemes_protection_relay_functions"

    init {
        addUniqueIndexes(
            listOf(PROTECTION_RELAY_SCHEME_MRID, PROTECTION_RELAY_FUNCTION_MRID)
        )

        addNonUniqueIndexes(
            listOf(PROTECTION_RELAY_SCHEME_MRID),
            listOf(PROTECTION_RELAY_FUNCTION_MRID)
        )
    }

}

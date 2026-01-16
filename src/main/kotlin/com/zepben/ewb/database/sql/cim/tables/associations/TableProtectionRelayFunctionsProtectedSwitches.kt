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
 * A class representing the association between ProtectionRelayFunctions and ProtectedSwitches.
 *
 * @property PROTECTION_RELAY_FUNCTION_MRID The mRID of ProtectionRelayFunctions.
 * @property PROTECTED_SWITCH_MRID The mRID of ProtectedSwitches.
 */
@Suppress("PropertyName")
class TableProtectionRelayFunctionsProtectedSwitches : SqlTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", Column.Type.STRING, NOT_NULL)
    val PROTECTED_SWITCH_MRID: Column = Column(++columnIndex, "protected_switch_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "protection_relay_functions_protected_switches"

    init {
        addUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID, PROTECTED_SWITCH_MRID)
        )

        addNonUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID),
            listOf(PROTECTED_SWITCH_MRID)
        )
    }

}

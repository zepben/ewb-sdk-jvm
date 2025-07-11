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
 * A class representing the association between ProtectionRelayFunctions and ProtectedSwitches.
 *
 * @property PROTECTION_RELAY_FUNCTION_MRID A column storing the mRID of ProtectionRelayFunctions.
 * @property PROTECTED_SWITCH_MRID A column storing the mRID of ProtectedSwitches.
 */
@Suppress("PropertyName")
class TableProtectionRelayFunctionsProtectedSwitches : SqliteTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)
    val PROTECTED_SWITCH_MRID: Column = Column(++columnIndex, "protected_switch_mrid", "TEXT", NOT_NULL)

    override val name: String = "protection_relay_functions_protected_switches"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(PROTECTION_RELAY_FUNCTION_MRID, PROTECTED_SWITCH_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(PROTECTION_RELAY_FUNCTION_MRID))
            add(listOf(PROTECTED_SWITCH_MRID))
        }

}

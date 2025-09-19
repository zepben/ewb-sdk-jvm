/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
class TableProtectionRelayFunctionThresholds : SqliteTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val UNIT_SYMBOL: Column = Column(++columnIndex, "unit_symbol", "TEXT", NOT_NULL)
    val VALUE: Column = Column(++columnIndex, "value", "NUMBER", NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", "TEXT", NULL)

    override val name: String = "protection_relay_function_thresholds"

    init {
        addUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID, SEQUENCE_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID)
        )
    }

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `ProtectionRelayFunctionTimeLimit` columns required for the database table.
 *
 * @property PROTECTION_RELAY_FUNCTION_MRID The ProtectionRelayFunction this time limit applies to.
 * @property SEQUENCE_NUMBER  Order of entries corresponds to the order of entries in thresholds.
 * @property TIME_LIMIT [ZBEX] The time limit (in seconds) for this relay function.
 */
@Suppress("PropertyName")
class TableProtectionRelayFunctionTimeLimits : SqlTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", Column.Type.STRING, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)
    val TIME_LIMIT: Column = Column(++columnIndex, "time_limit", Column.Type.DOUBLE, NOT_NULL)

    override val name: String = "protection_relay_function_time_limits"

    init {
        addUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID, SEQUENCE_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(PROTECTION_RELAY_FUNCTION_MRID)
        )
    }

}

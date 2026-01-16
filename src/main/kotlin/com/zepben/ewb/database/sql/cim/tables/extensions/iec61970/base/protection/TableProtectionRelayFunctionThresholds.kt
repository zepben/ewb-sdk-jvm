/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `ProtectionRelayFunctionThreshold` columns required for the database table.
 *
 * @property PROTECTION_RELAY_FUNCTION_MRID The ProtectionRelayFunction this threshold applies to.
 * @property SEQUENCE_NUMBER The order of thresholds corresponding to the order of time limits.
 * @property UNIT_SYMBOL The unit of the value.
 * @property VALUE The value of the setting, e.g voltage, current, etc.
 * @property NAME The name of the setting.
 */
@Suppress("PropertyName")
class TableProtectionRelayFunctionThresholds : SqlTable() {

    val PROTECTION_RELAY_FUNCTION_MRID: Column = Column(++columnIndex, "protection_relay_function_mrid", Column.Type.STRING, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)
    val UNIT_SYMBOL: Column = Column(++columnIndex, "unit_symbol", Column.Type.STRING, NOT_NULL)
    val VALUE: Column = Column(++columnIndex, "value", Column.Type.DOUBLE, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NULL)

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

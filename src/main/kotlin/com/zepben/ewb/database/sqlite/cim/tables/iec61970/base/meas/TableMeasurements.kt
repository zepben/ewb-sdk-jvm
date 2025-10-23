/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.meas

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
abstract class TableMeasurements : TableIdentifiedObjects() {

    val POWER_SYSTEM_RESOURCE_MRID: Column = Column(++columnIndex, "power_system_resource_mrid", Column.Type.STRING, NULL)
    val REMOTE_SOURCE_MRID: Column = Column(++columnIndex, "remote_source_mrid", Column.Type.STRING, NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)
    val PHASES: Column = Column(++columnIndex, "phases", Column.Type.STRING, NOT_NULL)
    val UNIT_SYMBOL: Column = Column(++columnIndex, "unit_symbol", Column.Type.STRING, NOT_NULL)

    init {
        addNonUniqueIndexes(
            listOf(POWER_SYSTEM_RESOURCE_MRID),
            listOf(REMOTE_SOURCE_MRID),
            listOf(TERMINAL_MRID)
        )
    }

}

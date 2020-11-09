/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
abstract class TableMeasurements : TableIdentifiedObjects() {

    val POWER_SYSTEM_RESOURCE_MRID = Column(++columnIndex, "power_system_resource_mrid", "TEXT", NULL)
    val REMOTE_SOURCE_MRID = Column(++columnIndex, "remote_source_mrid", "TEXT", NULL)
    val TERMINAL_MRID = Column(++columnIndex, "terminal_mrid", "TEXT", NULL)
    val PHASES = Column(++columnIndex, "phases", "TEXT", NOT_NULL)
    val UNIT_SYMBOL = Column(++columnIndex, "unit_symbol", "TEXT", NOT_NULL)

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols: MutableList<List<Column>> = super.nonUniqueIndexColumns()

        cols.add(listOf(POWER_SYSTEM_RESOURCE_MRID))
        cols.add(listOf(REMOTE_SOURCE_MRID))
        cols.add(listOf(TERMINAL_MRID))

        return cols
    }

}

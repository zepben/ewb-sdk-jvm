/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.meas

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects


abstract class TableMeasurements : TableIdentifiedObjects() {
    val POWER_SYSTEM_RESOURCE_MRID = Column(
        ++columnIndex,
        "power_system_resource_mrid",
        "TEXT",
        NULL
    )
    val REMOTE_SOURCE_MRID = Column(
        ++columnIndex,
        "remote_source_mrid",
        "TEXT",
        NULL
    )
    val TERMINAL_MRID = Column(
        ++columnIndex,
        "terminal_mrid",
        "TEXT",
        NULL
    )
    val PHASES = Column(
        ++columnIndex,
        "phases",
        "TEXT",
        NOT_NULL
    )
    val UNIT_SYMBOL = Column(
        ++columnIndex,
        "unit_symbol",
        "TEXT",
        NOT_NULL
    )

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols: MutableList<List<Column>> = super.nonUniqueIndexColumns()
        cols.add(listOf(POWER_SYSTEM_RESOURCE_MRID))
        cols.add(listOf(REMOTE_SOURCE_MRID))
        cols.add(listOf(TERMINAL_MRID))
        return cols
    }
}
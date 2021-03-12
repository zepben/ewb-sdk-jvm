/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
abstract class TableTransformerEnds : TableIdentifiedObjects() {

    val END_NUMBER = Column(++columnIndex, "end_number", "INTEGER", NOT_NULL)
    val TERMINAL_MRID = Column(++columnIndex, "terminal_mrid", "TEXT", NULL)
    val BASE_VOLTAGE_MRID = Column(++columnIndex, "base_voltage_mrid", "TEXT", NULL)
    val GROUNDED = Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL)
    val R_GROUND = Column(++columnIndex, "r_ground", "NUMBER", NOT_NULL)
    val X_GROUND = Column(++columnIndex, "x_ground", "NUMBER", NOT_NULL)
    val STAR_IMPEDANCE_MRID = Column(++columnIndex, "star_impedance_mrid", "TEXT", NULL)

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(STAR_IMPEDANCE_MRID))

        return cols
    }

}

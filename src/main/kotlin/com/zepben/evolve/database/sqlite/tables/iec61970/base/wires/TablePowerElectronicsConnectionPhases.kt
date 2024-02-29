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
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
class TablePowerElectronicsConnectionPhases : TablePowerSystemResources() {

    val POWER_ELECTRONICS_CONNECTION_MRID: Column = Column(++columnIndex, "power_electronics_connection_mrid", "TEXT", NULL)
    val P: Column = Column(++columnIndex, "p", "NUMBER", NULL)
    val PHASE: Column = Column(++columnIndex, "phase", "TEXT", NOT_NULL)
    val Q: Column = Column(++columnIndex, "q", "NUMBER", NULL)

    override fun name(): String {
        return "power_electronics_connection_phase"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()
        cols.add(listOf(POWER_ELECTRONICS_CONNECTION_MRID))
        return cols
    }

    override val tableClass: Class<TablePowerElectronicsConnectionPhases> = this.javaClass
    override val tableClassInstance: TablePowerElectronicsConnectionPhases = this

}

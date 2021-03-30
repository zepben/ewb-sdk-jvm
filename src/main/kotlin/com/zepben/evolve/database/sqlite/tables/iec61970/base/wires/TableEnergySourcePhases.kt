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
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
class TableEnergySourcePhases : TablePowerSystemResources() {

    val ENERGY_SOURCE_MRID = Column(++columnIndex, "energy_source_mrid", "TEXT", NOT_NULL)
    val PHASE = Column(++columnIndex, "phase", "TEXT", NOT_NULL)

    override fun name(): String {
        return "energy_source_phases"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(ENERGY_SOURCE_MRID, PHASE))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(ENERGY_SOURCE_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

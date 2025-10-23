/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
class TableEnergySourcePhases : TablePowerSystemResources() {

    val ENERGY_SOURCE_MRID: Column = Column(++columnIndex, "energy_source_mrid", Column.Type.STRING, NOT_NULL)
    val PHASE: Column = Column(++columnIndex, "phase", Column.Type.STRING, NOT_NULL)

    override val name: String = "energy_source_phases"

    init {
        addUniqueIndexes(
            listOf(ENERGY_SOURCE_MRID, PHASE)
        )

        addNonUniqueIndexes(
            listOf(ENERGY_SOURCE_MRID)
        )
    }

}

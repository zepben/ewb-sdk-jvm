/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TablePowerSystemResources
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL

/**
 * A class representing the `EnergySourcePhase` columns required for the database table.
 *
 * @property ENERGY_SOURCE_MRID The energy source to which the phase belongs.
 * @property PHASE Phase of this energy source component. If the energy source wye connected, the connection is from the indicated phase
 *                 to the central ground or neutral point.  If the energy source is delta connected, the phase indicates an energy source connected
 *                 from the indicated phase to the next logical non-neutral phase.
 */
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

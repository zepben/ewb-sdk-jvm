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
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PowerElectronicsConnectionPhase` columns required for the database table.
 *
 * @property POWER_ELECTRONICS_CONNECTION_MRID The power electronics connection to which the phase belongs.
 * @property P Active power injection. Load sign convention is used, i.e. positive sign means flow into the equipment from the network.
 * @property PHASE Phase of this energy producer component. If the energy producer is wye connected, the connection is from the indicated phase to the central
 *                 ground or neutral point. If the energy producer is delta connected, the phase indicates an energy producer connected from the indicated phase to the next
 *                 logical non-neutral phase.
 * @property Q Reactive power injection. Load sign convention is used, i.e. positive sign means flow into the equipment from the network.
 */
@Suppress("PropertyName")
class TablePowerElectronicsConnectionPhases : TablePowerSystemResources() {

    val POWER_ELECTRONICS_CONNECTION_MRID: Column = Column(++columnIndex, "power_electronics_connection_mrid", Column.Type.STRING, NULL)
    val P: Column = Column(++columnIndex, "p", Column.Type.DOUBLE, NULL)
    val PHASE: Column = Column(++columnIndex, "phase", Column.Type.STRING, NOT_NULL)
    val Q: Column = Column(++columnIndex, "q", Column.Type.DOUBLE, NULL)

    override val name: String = "power_electronics_connection_phases"

    init {
        addNonUniqueIndexes(
            listOf(POWER_ELECTRONICS_CONNECTION_MRID)
        )
    }

}

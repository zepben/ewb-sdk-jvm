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
 * A class representing the `EnergyConsumerPhase` columns required for the database table.
 *
 * @property ENERGY_CONSUMER_MRID The energy consumer to which this phase belongs.
 * @property PHASE Phase of this energy consumer component. If the energy consumer is wye connected, the connection is
 *                 from the indicated phase to the central ground or neutral point.  If the energy consumer is delta
 *                 connected, the phase indicates an energy consumer connected from the indicated phase to the next
 *                 logical non-neutral phase.
 * @property P Active power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage.
 *             Starting value for a steady state solution.
 * @property P_FIXED Active power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 * @property Q Reactive power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage.
 *             Starting value for a steady state solution.
 * @property Q_FIXED Reactive power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 */
@Suppress("PropertyName")
class TableEnergyConsumerPhases : TablePowerSystemResources() {

    val ENERGY_CONSUMER_MRID: Column = Column(++columnIndex, "energy_consumer_mrid", Column.Type.STRING, NOT_NULL)
    val PHASE: Column = Column(++columnIndex, "phase", Column.Type.STRING, NOT_NULL)
    val P: Column = Column(++columnIndex, "p", Column.Type.DOUBLE, NULL)
    val Q: Column = Column(++columnIndex, "q", Column.Type.DOUBLE, NULL)
    val P_FIXED: Column = Column(++columnIndex, "p_fixed", Column.Type.DOUBLE, NULL)
    val Q_FIXED: Column = Column(++columnIndex, "q_fixed", Column.Type.DOUBLE, NULL)

    override val name: String = "energy_consumer_phases"

    init {
        addUniqueIndexes(
            listOf(ENERGY_CONSUMER_MRID, PHASE)
        )

        addNonUniqueIndexes(
            listOf(ENERGY_CONSUMER_MRID)
        )
    }

}

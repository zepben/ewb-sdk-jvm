/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `EnergyConsumer` columns required for the database table.
 *
 * @property CUSTOMER_COUNT Number of individual customers represented by this demand.
 * @property GROUNDED Used for Yn and Zn connections. True if the neutral is solidly grounded.
 * @property P Active power of the load. Load sign convention is used, i.e. positive sign means flow out from a node
 *             For voltage dependent loads the value is at rated voltage. Starting value for a steady state solution.
 * @property P_FIXED Active power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 * @property PHASE_CONNECTION The type of phase connection, such as wye or delta.
 * @property Q Reactive power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage. Starting value for a steady state solution.
 * @property Q_FIXED power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 */
@Suppress("PropertyName")
class TableEnergyConsumers : TableEnergyConnections() {

    val CUSTOMER_COUNT: Column = Column(++columnIndex, "customer_count", Column.Type.INTEGER, NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", Column.Type.BOOLEAN, NULL)
    val P: Column = Column(++columnIndex, "p", Column.Type.DOUBLE, NULL)
    val Q: Column = Column(++columnIndex, "q", Column.Type.DOUBLE, NULL)
    val P_FIXED: Column = Column(++columnIndex, "p_fixed", Column.Type.DOUBLE, NULL)
    val Q_FIXED: Column = Column(++columnIndex, "q_fixed", Column.Type.DOUBLE, NULL)
    val PHASE_CONNECTION: Column = Column(++columnIndex, "phase_connection", Column.Type.STRING, NOT_NULL)

    override val name: String = "energy_consumers"

}

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
import com.zepben.ewb.database.sql.Column.Nullable.NULL

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

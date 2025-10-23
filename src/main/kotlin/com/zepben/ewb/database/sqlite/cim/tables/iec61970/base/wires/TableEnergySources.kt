/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableEnergySources : TableEnergyConnections() {

    val ACTIVE_POWER: Column = Column(++columnIndex, "active_power", Column.Type.DOUBLE, NULL)
    val REACTIVE_POWER: Column = Column(++columnIndex, "reactive_power", Column.Type.DOUBLE, NULL)
    val VOLTAGE_ANGLE: Column = Column(++columnIndex, "voltage_angle", Column.Type.DOUBLE, NULL)
    val VOLTAGE_MAGNITUDE: Column = Column(++columnIndex, "voltage_magnitude", Column.Type.DOUBLE, NULL)
    val P_MAX: Column = Column(++columnIndex, "p_max", Column.Type.DOUBLE, NULL)
    val P_MIN: Column = Column(++columnIndex, "p_min", Column.Type.DOUBLE, NULL)
    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "r0", Column.Type.DOUBLE, NULL)
    val RN: Column = Column(++columnIndex, "rn", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "x0", Column.Type.DOUBLE, NULL)
    val XN: Column = Column(++columnIndex, "xn", Column.Type.DOUBLE, NULL)
    val IS_EXTERNAL_GRID: Column = Column(++columnIndex, "is_external_grid", Column.Type.BOOLEAN, NULL)
    val R_MIN: Column = Column(++columnIndex, "r_min", Column.Type.DOUBLE, NULL)
    val RN_MIN: Column = Column(++columnIndex, "rn_min", Column.Type.DOUBLE, NULL)
    val R0_MIN: Column = Column(++columnIndex, "r0_min", Column.Type.DOUBLE, NULL)
    val X_MIN: Column = Column(++columnIndex, "x_min", Column.Type.DOUBLE, NULL)
    val XN_MIN: Column = Column(++columnIndex, "xn_min", Column.Type.DOUBLE, NULL)
    val X0_MIN: Column = Column(++columnIndex, "x0_min", Column.Type.DOUBLE, NULL)
    val R_MAX: Column = Column(++columnIndex, "r_max", Column.Type.DOUBLE, NULL)
    val RN_MAX: Column = Column(++columnIndex, "rn_max", Column.Type.DOUBLE, NULL)
    val R0_MAX: Column = Column(++columnIndex, "r0_max", Column.Type.DOUBLE, NULL)
    val X_MAX: Column = Column(++columnIndex, "x_max", Column.Type.DOUBLE, NULL)
    val XN_MAX: Column = Column(++columnIndex, "xn_max", Column.Type.DOUBLE, NULL)
    val X0_MAX: Column = Column(++columnIndex, "x0_max", Column.Type.DOUBLE, NULL)

    override val name: String = "energy_sources"

}

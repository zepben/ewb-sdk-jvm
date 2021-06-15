/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableEnergySources : TableEnergyConnections() {

    val ACTIVE_POWER = Column(++columnIndex, "active_power", "NUMBER", NULL)
    val REACTIVE_POWER = Column(++columnIndex, "reactive_power", "NUMBER", NULL)
    val VOLTAGE_ANGLE = Column(++columnIndex, "voltage_angle", "NUMBER", NULL)
    val VOLTAGE_MAGNITUDE = Column(++columnIndex, "voltage_magnitude", "NUMBER", NULL)
    val P_MAX = Column(++columnIndex, "p_max", "NUMBER", NULL)
    val P_MIN = Column(++columnIndex, "p_min", "NUMBER", NULL)
    val R = Column(++columnIndex, "r", "NUMBER", NULL)
    val R0 = Column(++columnIndex, "r0", "NUMBER", NULL)
    val RN = Column(++columnIndex, "rn", "NUMBER", NULL)
    val X = Column(++columnIndex, "x", "NUMBER", NULL)
    val X0 = Column(++columnIndex, "x0", "NUMBER", NULL)
    val XN = Column(++columnIndex, "xn", "NUMBER", NULL)

    override fun name(): String {
        return "energy_sources"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

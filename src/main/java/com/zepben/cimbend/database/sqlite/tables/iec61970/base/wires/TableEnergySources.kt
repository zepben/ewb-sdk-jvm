/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableEnergySources : TableEnergyConnections() {

    val ACTIVE_POWER = Column(++columnIndex, "active_power", "NUMBER", NOT_NULL)
    val REACTIVE_POWER = Column(++columnIndex, "reactive_power", "NUMBER", NOT_NULL)
    val VOLTAGE_ANGLE = Column(++columnIndex, "voltage_angle", "NUMBER", NOT_NULL)
    val VOLTAGE_MAGNITUDE = Column(++columnIndex, "voltage_magnitude", "NUMBER", NOT_NULL)
    val P_MAX = Column(++columnIndex, "p_max", "NUMBER", NOT_NULL)
    val P_MIN = Column(++columnIndex, "p_min", "NUMBER", NOT_NULL)
    val R = Column(++columnIndex, "r", "NUMBER", NOT_NULL)
    val R0 = Column(++columnIndex, "r0", "NUMBER", NOT_NULL)
    val RN = Column(++columnIndex, "rn", "NUMBER", NOT_NULL)
    val X = Column(++columnIndex, "x", "NUMBER", NOT_NULL)
    val X0 = Column(++columnIndex, "x0", "NUMBER", NOT_NULL)
    val XN = Column(++columnIndex, "xn", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "energy_sources"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

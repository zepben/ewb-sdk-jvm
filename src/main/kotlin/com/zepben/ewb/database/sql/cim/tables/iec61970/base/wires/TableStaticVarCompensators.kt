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
 * A class representing the `StaticVarCompensator` columns required for the database table.
 *
 * @property CAPACITIVE_RATING Capacitive reactance in Ohms at maximum capacitive reactive power. Shall always be positive.
 * @property INDUCTIVE_RATING Inductive reactance in Ohms at maximum inductive reactive power. Shall always be negative.
 * @property Q Reactive power injection in VAr. Load sign convention is used, i.e. positive sign means flow out from a node. Starting value for a steady state solution.
 * @property SVC_CONTROL_MODE SVC control mode.
 * @property VOLTAGE_SET_POINT The reactive power output of the SVC is proportional to the difference between the voltage at the regulated bus and the voltage set-point.
 *                           When the regulated bus voltage is equal to the voltage set-point, the reactive power output is zero. Must be in volts.
 */
@Suppress("PropertyName")
class TableStaticVarCompensators : TableRegulatingCondEq() {

    val CAPACITIVE_RATING: Column = Column(++columnIndex, "capacitive_rating", Column.Type.DOUBLE, NULL)
    val INDUCTIVE_RATING: Column = Column(++columnIndex, "inductive_rating", Column.Type.DOUBLE, NULL)
    val Q: Column = Column(++columnIndex, "q", Column.Type.DOUBLE, NULL)
    val SVC_CONTROL_MODE: Column = Column(++columnIndex, "svc_control_mode", Column.Type.STRING, NOT_NULL)
    val VOLTAGE_SET_POINT: Column = Column(++columnIndex, "voltage_set_point", Column.Type.INTEGER, NULL)

    override val name: String = "static_var_compensators"

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `EnergySource` columns required for the database table.
 *
 * @property ACTIVE_POWER The high voltage source active injection. Load sign convention is used, i.e. positive sign means flow out from a node. Starting value for steady state solutions.
 * @property REACTIVE_POWER The high voltage source reactive injection. Load sign convention is used, i.e. positive sign means flow out from a node. Starting value for steady state solutions.
 * @property VOLTAGE_ANGLE The phase angle of a-phase open circuit used when voltage characteristics need to be imposed at the node associated with the terminal of the energy source, such as when voltages and angles from the transmission level are used as input to the distribution network. The attribute shall be a positive value or zero.
 * @property VOLTAGE_MAGNITUDE The phase-to-phase open circuit voltage magnitude used when voltage characteristics need to be imposed at the node associated with the terminal of the energy source, such as when voltages and angles from the transmission level are used as input to the distribution network. The attribute shall be a positive value or zero.
 * @property P_MAX The maximum active power that can be produced by the source. Load sign convention is used, i.e. positive sign means flow out from a TopologicalNode (bus) into the conducting equipment.
 * @property P_MIN The minimum active power that can be produced by the source. Load sign convention is used, i.e. positive sign means flow out from a TopologicalNode (bus) into the conducting equipment.
 * @property R The positive sequence Thevenin resistance.
 * @property R0 The zero sequence Thevenin resistance.
 * @property RN The negative sequence Thevenin resistance.
 * @property X The positive sequence Thevenin reactance.
 * @property X0 The zero sequence Thevenin reactance.
 * @property XN The negative sequence Thevenin reactance.
 * @property IS_EXTERNAL_GRID True if this energy source represents the higher-level power grid connection to an external grid that normally is modelled as the slack bus for power flow calculations.
 * @property R_MIN The minimum positive sequence Thevenin resistance.
 * @property RN_MIN The minimum negative sequence Thevenin resistance
 * @property R0_MIN The minimum zero sequence Thevenin resistance.
 * @property X_MIN The minimum positive sequence Thevenin reactance.
 * @property XN_MIN The minimum negative sequence Thevenin reactance.
 * @property X0_MIN The minimum zero sequence Thevenin reactance.
 * @property R_MAX The maximum positive sequence Thevenin resistance.
 * @property RN_MAX The maximum negative sequence Thevenin resistance.
 * @property R0_MAX The maximum zero sequence Thevenin resistance.
 * @property X_MAX The maximum positive sequence Thevenin reactance.
 * @property XN_MAX The maximum negative sequence Thevenin resistance.
 * @property X0_MAX The maximum zero sequence Thevenin reactance.
 */
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

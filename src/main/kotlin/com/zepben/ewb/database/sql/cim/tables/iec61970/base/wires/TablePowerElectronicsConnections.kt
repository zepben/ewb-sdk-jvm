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
 * A class representing the `PowerElectronicsConnection` columns required for the database table.
 *
 * @property MAX_I_FAULT Maximum fault current this device will contribute, in per-unit of rated current, before the converter protection
 *                     will trip or bypass.
 * @property MAX_Q Maximum reactive power limit. This is the maximum (nameplate) limit for the unit.
 * @property MIN_Q Minimum reactive power limit for the unit. This is the minimum (nameplate) limit for the unit.
 * @property P Active power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property Q Reactive power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property RATED_S Nameplate apparent power rating for the unit. The attribute shall have a positive value.
 * @property RATED_U Rated voltage (nameplate data, Ur in IEC 60909-0). It is primarily used for short circuit data exchange according
 *                  to IEC 60909. The attribute shall be a positive value.
 * @property INVERTER_STANDARD The standard this inverter follows, such as AS4777.2:2020
 * @property SUSTAIN_OP_OVERVOLT_LIMIT Indicates the sustained operation overvoltage limit in volts, when the average voltage for a 10-minute period exceeds the V¬nom-max.
 * @property STOP_AT_OVER_FREQ Over frequency (stop) in Hz. Permitted range is between 51 and 52 (inclusive)
 * @property STOP_AT_UNDER_FREQ Under frequency (stop) in Hz Permitted range is between 47 and 49 (inclusive)
 * @property INV_VOLT_WATT_RESP_MODE Volt-Watt response mode allows an inverter to reduce is real power output depending on the measured voltage. This mode is further described in AS4777.2:2015, section 6.3.2.2. True implies the mode is enabled.
 * @property INV_WATT_RESP_V1 Set point 1 in volts for inverter Volt-Watt response mode. Permitted range is between 200 and 300 (inclusive).
 * @property INV_WATT_RESP_V2 Set point 2 in volts for inverter Volt-Watt response mode. Permitted range is between 216 and 230 (inclusive).
 * @property INV_WATT_RESP_V3 Set point 3 in volts for inverter Volt-Watt response mode. Permitted range is between 235 and 255 (inclusive).
 * @property INV_WATT_RESP_V4 Set point 4 in volts for inverter Volt-Watt response mode. Permitted range is between 244 and 265 (inclusive).
 * @property INV_WATT_RESP_P_AT_V1 Power output set point 1 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property INV_WATT_RESP_P_AT_V2 Power output set point 2 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property INV_WATT_RESP_P_AT_V3 Power output set point 3 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 1 (inclusive).
 * @property INV_WATT_RESP_P_AT_V4 Power output set point 4 as a percentage of rated output for inverter Volt-Watt response mode. Permitted range is between 0 and 0.2 (inclusive).
 * @property INV_VOLT_VAR_RESP_MODE Volt-VAr response mode allows an inverter to consume (sink) or produce (source) reactive power depending on the measured voltage. This mode is further described in AS4777.2:2015, section 6.3.2.3. True implies the mode is enabled.
 * @property INV_VAR_RESP_V1 Set point 1 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property INV_VAR_RESP_V2 Set point 2 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property INV_VAR_RESP_V3 Set point 3 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property INV_VAR_RESP_V4 Set point 4 in volts for inverter Volt-VAr response mode. Permitted range is between 200 and 300 (inclusive).
 * @property INV_VAR_RESP_Q_AT_V1 Power output set point 1 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between 0 and 0.6 (inclusive).
 * @property INV_VAR_RESP_Q_AT_V2 Power output set point 2 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -1 and 1 (inclusive) with a negative number referring to a sink.
 * @property INV_VAR_RESP_Q_AT_V3 Power output set point 3 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -1 and 1 (inclusive) with a negative number referring to a sink.
 * @property INV_VAR_RESP_Q_AT_V4 Power output set point 4 as a percentage of rated output for inverter Volt-VAr response mode. Permitted range is between -0.6 and 0 (inclusive) with a negative number referring to a sink.
 * @property INV_REACTIVE_POWER_MODE If true, enables Static Reactive Power mode on the inverter. Note: It must be false if invVoltVarRespMode or InvVoltWattRespMode is true.
 * @property INV_FIX_REACTIVE_POWER Static Reactive Power, specified in a percentage output of the system. Permitted range is between -1.0 and 1.0 (inclusive), with a negative sign referring to “sink”.
 */
@Suppress("PropertyName")
class TablePowerElectronicsConnections : TableRegulatingCondEq() {

    val MAX_I_FAULT: Column = Column(++columnIndex, "max_i_fault", Column.Type.INTEGER, NULL)
    val MAX_Q: Column = Column(++columnIndex, "max_q", Column.Type.DOUBLE, NULL)
    val MIN_Q: Column = Column(++columnIndex, "min_q", Column.Type.DOUBLE, NULL)
    val P: Column = Column(++columnIndex, "p", Column.Type.DOUBLE, NULL)
    val Q: Column = Column(++columnIndex, "q", Column.Type.DOUBLE, NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", Column.Type.INTEGER, NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", Column.Type.INTEGER, NULL)
    val INVERTER_STANDARD: Column = Column(++columnIndex, "inverter_standard", Column.Type.STRING, NULL)
    val SUSTAIN_OP_OVERVOLT_LIMIT: Column = Column(++columnIndex, "sustain_op_overvolt_limit", Column.Type.INTEGER, NULL)
    val STOP_AT_OVER_FREQ: Column = Column(++columnIndex, "stop_at_over_freq", Column.Type.DOUBLE, NULL)
    val STOP_AT_UNDER_FREQ: Column = Column(++columnIndex, "stop_at_under_freq", Column.Type.DOUBLE, NULL)
    val INV_VOLT_WATT_RESP_MODE: Column = Column(++columnIndex, "inv_volt_watt_resp_mode", Column.Type.BOOLEAN, NULL)
    val INV_WATT_RESP_V1: Column = Column(++columnIndex, "inv_watt_resp_v1", Column.Type.INTEGER, NULL)
    val INV_WATT_RESP_V2: Column = Column(++columnIndex, "inv_watt_resp_v2", Column.Type.INTEGER, NULL)
    val INV_WATT_RESP_V3: Column = Column(++columnIndex, "inv_watt_resp_v3", Column.Type.INTEGER, NULL)
    val INV_WATT_RESP_V4: Column = Column(++columnIndex, "inv_watt_resp_v4", Column.Type.INTEGER, NULL)
    val INV_WATT_RESP_P_AT_V1: Column = Column(++columnIndex, "inv_watt_resp_p_at_v1", Column.Type.DOUBLE, NULL)
    val INV_WATT_RESP_P_AT_V2: Column = Column(++columnIndex, "inv_watt_resp_p_at_v2", Column.Type.DOUBLE, NULL)
    val INV_WATT_RESP_P_AT_V3: Column = Column(++columnIndex, "inv_watt_resp_p_at_v3", Column.Type.DOUBLE, NULL)
    val INV_WATT_RESP_P_AT_V4: Column = Column(++columnIndex, "inv_watt_resp_p_at_v4", Column.Type.DOUBLE, NULL)
    val INV_VOLT_VAR_RESP_MODE: Column = Column(++columnIndex, "inv_volt_var_resp_mode", Column.Type.BOOLEAN, NULL)
    val INV_VAR_RESP_V1: Column = Column(++columnIndex, "inv_var_resp_v1", Column.Type.INTEGER, NULL)
    val INV_VAR_RESP_V2: Column = Column(++columnIndex, "inv_var_resp_v2", Column.Type.INTEGER, NULL)
    val INV_VAR_RESP_V3: Column = Column(++columnIndex, "inv_var_resp_v3", Column.Type.INTEGER, NULL)
    val INV_VAR_RESP_V4: Column = Column(++columnIndex, "inv_var_resp_v4", Column.Type.INTEGER, NULL)
    val INV_VAR_RESP_Q_AT_V1: Column = Column(++columnIndex, "inv_var_resp_q_at_v1", Column.Type.DOUBLE, NULL)
    val INV_VAR_RESP_Q_AT_V2: Column = Column(++columnIndex, "inv_var_resp_q_at_v2", Column.Type.DOUBLE, NULL)
    val INV_VAR_RESP_Q_AT_V3: Column = Column(++columnIndex, "inv_var_resp_q_at_v3", Column.Type.DOUBLE, NULL)
    val INV_VAR_RESP_Q_AT_V4: Column = Column(++columnIndex, "inv_var_resp_q_at_v4", Column.Type.DOUBLE, NULL)
    val INV_REACTIVE_POWER_MODE: Column = Column(++columnIndex, "inv_reactive_power_mode", Column.Type.BOOLEAN, NULL)
    val INV_FIX_REACTIVE_POWER: Column = Column(++columnIndex, "inv_fix_reactive_power", Column.Type.DOUBLE, NULL)

    override val name: String = "power_electronics_connections"

}

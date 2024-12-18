/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets.network

import com.zepben.evolve.database.getNullableDouble
import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet57NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 57) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('tap_changer_control_mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3, 4, true, 5, 6, 7, 'terminal_mrid', 8, true, 9, 10, 11, 12, true, 13, true)",
        "INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, per_length_sequence_impedance_mrid) VALUES ('acls1', '', '', 1, 'loc1', 10, true, true, 'comm1', 'bv1', 'plsi_mrid_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('tap_changer_control_2_mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3, 4, true, 5, 6, 7, 'terminal_mrid', 8, 9, 10, true, 11, 12, 13, 14, true, 15, true)",
        "INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, charging_rate, discharging_rate, reserve_percent, control_mode) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3.0, 4.0, true, 5.0, 6.0, 7.0, 8.0, 9.0, 'terminal_mrid', 10.0, 11.0, 12.0, 'control_mode');",
        "INSERT INTO pan_demand_response_functions (mrid, name, description, num_diagram_objects, enabled, kind, appliance) VALUES ('mrid', 'name', 'description', 1, true, 'kind', 2);",
        "INSERT INTO static_var_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, capacitive_rating, inductive_rating, q, svc_control_mode, voltage_set_point) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, true, 'commissioned_date', 'base_voltage_mrid', true, 'regulating_control_mrid', 3.0, 4.0, 5.0, 'svc_control_mode', 6);",
        "INSERT INTO battery_units_battery_controls (battery_unit_mrid, battery_control_mrid) VALUES ('battery_unit_mrid', 'battery_control_mrid');",
        "INSERT INTO end_devices_end_device_functions (end_device_mrid, end_device_function_mrid) VALUES ('end_device_mrid', 'end_device_function_mrid');",
        "INSERT INTO per_length_phase_impedances (mrid, name, description, num_diagram_objects) VALUES ('plpi_mrid', 'name', 'description', 0);",
        "INSERT INTO phase_impedance_data (per_length_phase_impedance_mrid, from_phase, to_phase, b, g, r, x) VALUES ('plpi_mrid', 'A', 'B', 1.0, 2.0, 3.0, 4.0);",
        "INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, per_length_impedance_mrid) VALUES ('acls2', '', '', 1, 'loc1', 10, true, true, 'comm1', 'bv1', 'plpi_mrid_2');",

        )

    override fun validateChanges(statement: Statement) {
        ensureModifiedTapChangerControls(statement)
        ensureAddedBatteryControls(statement)
        ensureAddedPanDemandResponseFunctions(statement)
        ensureAddedStaticVarCompensators(statement)
        ensureAddedBatteryUnitsBatteryControls(statement)
        ensureAddedEndDevicesEndDeviceFunctions(statement)
        ensureAddedPerLengthPhaseImpedances(statement)
        ensureAddedPhaseImpedanceData(statement)
        ensureRenamedPerLengthImpedance(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM tap_changer_controls;",
            "DELETE FROM battery_controls;",
            "DELETE FROM pan_demand_response_functions;",
            "DELETE FROM static_var_compensators;",
            "DELETE FROM battery_units_battery_controls;",
            "DELETE FROM end_devices_end_device_functions;",
            "DELETE FROM per_length_phase_impedances;",
            "DELETE FROM phase_impedance_data;",
            "DELETE FROM ac_line_segments;"
        )

    private fun ensureModifiedTapChangerControls(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM tap_changer_controls",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("tap_changer_control_mrid"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("description"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid"))
                assertThat(rs.getInt("num_controls"), equalTo(2))
                assertThat(rs.getBoolean("discrete"), equalTo(true))
                assertThat(rs.getString("mode"), equalTo("mode"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase"))
                assertThat(rs.getFloat("target_deadband"), equalTo(3.0f))
                assertThat(rs.getDouble("target_value"), equalTo(4.0))
                assertThat(rs.getBoolean("enabled"), equalTo(true))
                assertThat(rs.getDouble("max_allowed_target_value"), equalTo(5.0))
                assertThat(rs.getDouble("min_allowed_target_value"), equalTo(6.0))
                assertThat(rs.getDouble("rated_current"), equalTo(7.0))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid"))
                assertThat(rs.getNullableDouble("ct_primary"), nullValue())
                assertThat(rs.getNullableDouble("min_target_deadband"), nullValue())
                assertThat(rs.getInt("limit_voltage"), equalTo(8))
                assertThat(rs.getBoolean("line_drop_compensation"), equalTo(true))
                assertThat(rs.getDouble("line_drop_r"), equalTo(9.0))
                assertThat(rs.getDouble("line_drop_x"), equalTo(10.0))
                assertThat(rs.getDouble("reverse_line_drop_r"), equalTo(11.0))
                assertThat(rs.getDouble("reverse_line_drop_x"), equalTo(12.0))
                assertThat(rs.getBoolean("forward_ldc_blocking"), equalTo(true))
                assertThat(rs.getDouble("time_delay"), equalTo(13.0))
                assertThat(rs.getBoolean("co_generation_enabled"), equalTo(true))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("tap_changer_control_2_mrid"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("description"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid"))
                assertThat(rs.getInt("num_controls"), equalTo(2))
                assertThat(rs.getBoolean("discrete"), equalTo(true))
                assertThat(rs.getString("mode"), equalTo("mode"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase"))
                assertThat(rs.getFloat("target_deadband"), equalTo(3.0f))
                assertThat(rs.getDouble("target_value"), equalTo(4.0))
                assertThat(rs.getBoolean("enabled"), equalTo(true))
                assertThat(rs.getDouble("max_allowed_target_value"), equalTo(5.0))
                assertThat(rs.getDouble("min_allowed_target_value"), equalTo(6.0))
                assertThat(rs.getDouble("rated_current"), equalTo(7.0))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid"))
                assertThat(rs.getDouble("ct_primary"), equalTo(8.0))
                assertThat(rs.getDouble("min_target_deadband"), equalTo(9.0))
                assertThat(rs.getInt("limit_voltage"), equalTo(10))
                assertThat(rs.getBoolean("line_drop_compensation"), equalTo(true))
                assertThat(rs.getDouble("line_drop_r"), equalTo(11.0))
                assertThat(rs.getDouble("line_drop_x"), equalTo(12.0))
                assertThat(rs.getDouble("reverse_line_drop_r"), equalTo(13.0))
                assertThat(rs.getDouble("reverse_line_drop_x"), equalTo(14.0))
                assertThat(rs.getBoolean("forward_ldc_blocking"), equalTo(true))
                assertThat(rs.getDouble("time_delay"), equalTo(15.0))
                assertThat(rs.getBoolean("co_generation_enabled"), equalTo(true))
            }
        )
    }

    private fun ensureAddedBatteryControls(statement: Statement) {
        ensureTables(statement, "battery_controls")
        ensureIndexes(statement, "battery_controls_mrid")
    }

    private fun ensureAddedPanDemandResponseFunctions(statement: Statement) {
        ensureTables(statement, "pan_demand_response_functions")
        ensureIndexes(statement, "pan_demand_response_functions_mrid")
    }

    private fun ensureAddedStaticVarCompensators(statement: Statement) {
        ensureTables(statement, "static_var_compensators")
        ensureIndexes(statement, "static_var_compensators_mrid")
    }

    private fun ensureAddedBatteryUnitsBatteryControls(statement: Statement) {
        ensureTables(statement, "battery_units_battery_controls")
        ensureIndexes(
            statement,
            "battery_units_mrid_battery_controls_mrid",
            "battery_units_battery_controls_battery_unit_mrid",
            "battery_units_battery_controls_battery_control_mrid"
        )
    }

    private fun ensureAddedEndDevicesEndDeviceFunctions(statement: Statement) {
        ensureTables(statement, "end_devices_end_device_functions")
        ensureIndexes(
            statement,
            "battery_units_mrid_battery_controls_mrid",
            "end_devices_end_device_functions_end_device_mrid",
            "end_devices_end_device_functions_end_device_function_mrid"
        )
    }

    private fun ensureAddedPerLengthPhaseImpedances(statement: Statement) {
        ensureTables(statement, "per_length_phase_impedances")
        ensureIndexes(statement, "per_length_phase_impedances_mrid")
    }

    private fun ensureAddedPhaseImpedanceData(statement: Statement) {
        ensureTables(statement, "phase_impedance_data")
        ensureIndexes(
            statement,
            "phase_impedance_data_per_length_phase_impedance_mrid",
            "phase_impedance_data_from_phase_to_phase_per_length_phase_impedance_mrid"
        )
    }

    private fun ensureRenamedPerLengthImpedance(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM ac_line_segments",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("acls1"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("commissioned_date"), equalTo("comm1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getString("per_length_impedance_mrid"), equalTo("plsi_mrid_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("acls2"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("commissioned_date"), equalTo("comm1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getString("per_length_impedance_mrid"), equalTo("plpi_mrid_2"))
            }
        )
    }
}

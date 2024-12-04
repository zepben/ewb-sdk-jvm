/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets.network

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import java.sql.Statement

object ChangeSet57NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 57) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('tap_changer_control_mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3, 4, true, 5, 6, 7, 'terminal_mrid', 8, true, 9, 10, 11, 12, true, 13, true)"
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('tap_changer_control_2_mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3, 4, true, 5, 6, 7, 'terminal_mrid', 8, 9, 10, true, 11, 12, 13, 14, true, 15, true)",
        "INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, battery_unit_mrid, charging_Rate, discharging_rate, reserve_percent, control_mode) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3.0, 4.0, true, 5.0, 6.0, 7.0, 8.0, 9.0, 'terminal_mrid', 'battery_unit_mrid', 10.0, 11.0, 12.0, 'control_mode');",
        "INSERT INTO pan_demand_response_functions (mrid, name, description, num_diagram_objects, end_device_mrid, enabled, kind, appliance) VALUES ('mrid', 'name', 'description', 1, 'end_device_mrid', true, 'kind', 2);",
        "INSERT INTO static_var_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, capacitive_rating, inductive_rating, q, svc_control_mode, voltage_set_point) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, true, 'commissioned_date', 'base_voltage_mrid', true, 'regulating_control_mrid', 3.0, 4.0, 5.0, 'svc_control_mode', 6);",
    )

    override fun validateChanges(statement: Statement) {

        //Ensure new tables are added
        ensureAddedBatteryControls(statement)
        ensureAddedPanDemandResponseFunctions(statement)
        ensureAddedStaticVarCompensators(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM battery_controls;",
            "DELETE FROM pan_demand_response_functions;",
            "DELETE FROM static_var_compensators;",
            "DELETE FROM tap_changer_controls;"
        )

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

}

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

    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, battery_unit_mrid, charging_Rate, discharging_rate, reserve_percent, control_mode) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, 'mode', 'monitored_phase', 3.0, 4.0, true, 5.0, 6.0, 7.0, 'terminal_mrid', 'battery_unit_mrid', 8.0, 9.0, 10.0, 'control_mode');",
        "INSERT INTO pan_demand_response_functions (mrid, name, description, num_diagram_objects, end_device_mrid, enabled, kind, appliance) VALUES ('mrid', 'name', 'description', 1, 'end_device_mrid', true, 'kind', 2);",
        "INSERT INTO static_var_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, capacitive_rating, inductive_rating, q, svc_control_mode, voltage_set_point) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 2, true, true, 'commissioned_date', 'base_voltage_mrid', true, 'regulating_control_mrid', 3.0, 4.0, 5.0, 'svc_control_mode', 6);",
        "INSERT INTO battery_units_battery_controls (battery_unit_mrid, battery_control_mrid) VALUES ('battery_unit_mrid', 'battery_control_mrid');",
        "INSERT INTO end_devices_end_device_functions (end_device_mrid, end_device_function_mrid) VALUES ('end_device_mrid', 'end_device_function_mrid');",
    )

    override fun validateChanges(statement: Statement) {

        //Ensure new tables are added
        ensureAddedBatteryControls(statement)
        ensureAddedPanDemandResponseFunctions(statement)
        ensureAddedStaticVarCompensators(statement)
        ensureAddedBatteryUnitsBatteryControls(statement)
        ensureAddedEndDevicesEndDeviceFunctions(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM battery_controls;",
            "DELETE FROM pan_demand_response_functions;",
            "DELETE FROM static_var_compensators;",
            "DELETE FROM battery_units_battery_controls;",
            "DELETE FROM end_devices_end_device_functions;"
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

}

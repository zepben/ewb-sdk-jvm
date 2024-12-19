/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.Change
import com.zepben.evolve.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet57() = ChangeSet(
    57,
    listOf(
        // Network Change
        `Add ct_primary and min_target_deadband columns for tap_changer_controls`,
        `Create table battery_controls`,
        `Create table pan_demand_response_functions`,
        `Create table static_var_compensators`,
        `Create table battery_units_battery_controls`,
        `Create table end_devices_end_device_functions`,
        `Create table phase_impedance_data`,
        `Create table per_length_phase_impedances`,
        `Rename acls impedance column`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Add ct_primary and min_target_deadband columns for tap_changer_controls` = Change(
    listOf(
        "ALTER TABLE tap_changer_controls ADD COLUMN ct_primary NUMBER NULL DEFAULT null;",
        "ALTER TABLE tap_changer_controls ADD COLUMN min_target_deadband NUMBER NULL DEFAULT null;"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table battery_controls` = Change(
    listOf(
        """CREATE TABLE battery_controls (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            discrete BOOLEAN NULL,
            mode TEXT NOT NULL,
            monitored_phase TEXT NOT NULL,
            target_deadband NUMBER NULL,
            target_value NUMBER NULL,
            enabled BOOLEAN NULL,
            max_allowed_target_value NUMBER NULL,
            min_allowed_target_value NUMBER NULL,
            rated_current NUMBER NULL,
            terminal_mrid TEXT NULL,
            ct_primary NUMBER NULL,
            min_target_deadband NUMBER NULL,
            charging_rate NUMBER NULL,
            discharging_rate NUMBER NULL,
            reserve_percent NUMBER NULL,
            control_mode TEXT NOT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX battery_controls_mrid ON battery_controls (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table pan_demand_response_functions` = Change(
    listOf(
        """CREATE TABLE pan_demand_response_functions (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            enabled BOOLEAN NULL,
            kind TEXT NULL,
            appliance INTEGER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX pan_demand_response_functions_mrid ON pan_demand_response_functions (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table static_var_compensators` = Change(
    listOf(
        """CREATE TABLE static_var_compensators (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            commissioned_date TEST NULL,
            base_voltage_mrid TEXT NULL,
            control_enabled BOOLEAN NOT NULL,
            regulating_control_mrid TEXT NULL,
            capacitive_rating NUMBER NULL,
            inductive_rating NUMBER NULL,
            q NUMBER NULL,
            svc_control_mode TEXT NOT NULL,
            voltage_set_point INTEGER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX static_var_compensators_mrid ON static_var_compensators (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// #######################
// # Association Changes #
// #######################

@Suppress("ObjectPropertyName")
private val `Create table battery_units_battery_controls` = Change(
    listOf(
        """CREATE TABLE battery_units_battery_controls (
            battery_unit_mrid TEXT NOT_NULL,
            battery_control_mrid TEXT NOT_NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX battery_units_mrid_battery_controls_mrid ON battery_units_battery_controls (battery_unit_mrid, battery_control_mrid);",
        "CREATE INDEX battery_units_battery_controls_battery_unit_mrid ON battery_units_battery_controls (battery_unit_mrid);",
        "CREATE INDEX battery_units_battery_controls_battery_control_mrid ON battery_units_battery_controls (battery_control_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table end_devices_end_device_functions` = Change(
    listOf(
        """CREATE TABLE end_devices_end_device_functions (
            end_device_mrid TEXT NOT_NULL,
            end_device_function_mrid TEXT NOT_NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX end_device_mrid_end_device_function_mrid ON end_devices_end_device_functions (end_device_mrid, end_device_function_mrid);",
        "CREATE INDEX end_devices_end_device_functions_end_device_mrid ON end_devices_end_device_functions (end_device_mrid);",
        "CREATE INDEX end_devices_end_device_functions_end_device_function_mrid ON end_devices_end_device_functions (end_device_function_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table phase_impedance_data` = Change(
    listOf(
        """CREATE TABLE phase_impedance_data (
            per_length_phase_impedance_mrid TEXT NOT NULL,
            from_phase TEXT NOT NULL,
            to_phase TEXT NOT NULL,
            b NUMBER NULL,
            g NUMBER NULL,
            r NUMBER NULL,
            x NUMBER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX phase_impedance_data_from_phase_to_phase_per_length_phase_impedance_mrid ON phase_impedance_data (per_length_phase_impedance_mrid, from_phase, to_phase);",
        "CREATE INDEX phase_impedance_data_per_length_phase_impedance_mrid ON phase_impedance_data (per_length_phase_impedance_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table per_length_phase_impedances` = Change(
    listOf(
        """CREATE TABLE per_length_phase_impedances (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX per_length_phase_impedances_mrid ON per_length_phase_impedances (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Rename acls impedance column` = Change(
    listOf(
        "ALTER TABLE ac_line_segments RENAME COLUMN per_length_sequence_impedance_mrid TO per_length_impedance_mrid;",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

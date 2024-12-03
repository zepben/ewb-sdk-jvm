/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets

import com.zepben.evolve.cim.extensions.ZBEX
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
        `Create table static_var_compensators`
    )
)

// ###################
// # Network Changes #
// ###################

@ZBEX
@Suppress("ObjectPropertyName")
private val `Add ct_primary and min_target_deadband columns for tap_changer_controls` = Change(
    listOf(
        "ALTER TABLE tap_changer_controls ADD COLUMN ct_primary NUMBER NOT NULL DEFAULT 'NONE';",
        "ALTER TABLE tap_changer_controls ADD COLUMN min_target_deadband NUMBER NOT NULL DEFAULT 'NONE';"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@ZBEX
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
            battery_unit_mrid TEXT NULL,
            charging_Rate NUMBER NULL,
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
            end_device_mrid TEXT NULL,
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

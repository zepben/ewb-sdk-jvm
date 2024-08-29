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

internal fun changeSet55() = ChangeSet(
    55,
    listOf(
        // Network Change
        `Add phase code columns for usage_points`,
        `Create table curve_data`,
        `Create table grounding_impedances`,
        `Create table petersen_coils`,
        `Create table reactive_capability_curves`,
        `Create table synchronous_machines`,
        `Create table synchronous_machines_reactive_capability_curves`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Add phase code columns for usage_points` = Change(
    listOf(
        "ALTER TABLE usage_points ADD COLUMN phase_code TEXT NOT NULL DEFAULT 'NONE';"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table curve_data` = Change(
    listOf(
        """CREATE TABLE curve_data (
            curve_mrid TEXT NOT NULL,
            x_value NUMBER NOT NULL,
            y1_value NUMBER NOT NULL,
            y2_value NUMBER NULL,
            y3_value NUMBER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX curve_data_curve_mrid_x_value ON curve_data (curve_mrid, x_value);",
        "CREATE INDEX curve_data_curve_mrid ON curve_data (curve_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table grounding_impedances` = Change(
    listOf(
        """CREATE TABLE grounding_impedances (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            commissioned_date TEXT NULL,
            base_voltage_mrid TEXT NULL,
            r NUMBER NULL,
            x NUMBER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX grounding_impedances_mrid ON grounding_impedances (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table petersen_coils` = Change(
    listOf(
        """CREATE TABLE petersen_coils (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            commissioned_date TEXT NULL,
            base_voltage_mrid TEXT NULL,
            r NUMBER NULL,
            x_ground_nominal NUMBER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX petersen_coils_mrid ON petersen_coils (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table reactive_capability_curves` = Change(
    listOf(
        """CREATE TABLE reactive_capability_curves (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX reactive_capability_curves_mrid ON reactive_capability_curves (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table synchronous_machines` = Change(
    listOf(
        """CREATE TABLE synchronous_machines (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN,
            in_service BOOLEAN,
            commissioned_date TEXT NULL,
            base_voltage_mrid TEXT NULL,
            control_enabled BOOLEAN NOT NULL,
            regulating_control_mrid TEXT NULL,
            rated_power_factor NUMBER NULL,
            rated_s NUMBER NULL,
            rated_u INTEGER NULL,
            p NUMBER NULL,
            q NUMBER NULL,
            base_q NUMBER NULL,
            condenser_p INTEGER NULL,
            earthing BOOLEAN NOT NULL,
            earthing_star_point_r NUMBER NULL,
            earthing_star_point_x NUMBER NULL,
            ikk NUMBER NULL,
            max_q NUMBER NULL,
            max_u INTEGER NULL,
            min_q NUMBER NULL,
            min_u INTEGER NULL,
            mu NUMBER NULL,
            r NUMBER NULL,
            r0 NUMBER NULL,
            r2 NUMBER NULL,
            sat_direct_subtrans_x NUMBER NULL,
            sat_direct_sync_x NUMBER NULL,
            sat_direct_trans_x NUMBER NULL,
            x0 NUMBER NULL,
            x2 NUMBER NULL,
            type TEXT NOT NULL,
            operating_mode TEXT NOT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX synchronous_machines_mrid ON synchronous_machines (mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// #######################
// # Association Changes #
// #######################

@Suppress("ObjectPropertyName")
private val `Create table synchronous_machines_reactive_capability_curves` = Change(
    listOf(
        """CREATE TABLE synchronous_machines_reactive_capability_curves (
            SYNCHRONOUS_MACHINE_MRID TEXT NOT_NULL,
            REACTIVE_CAPABILITY_CURVE_MRID TEXT NOT_NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX synchronous_machines_mrid_reactive_capability_curve_mrid ON synchronous_machines_reactive_capability_curves (synchronous_machine_mrid, reactive_capability_curve_mrid);",
        "CREATE INDEX synchronous_machines_reactive_capability_curves_synchronous_machine_mrid ON synchronous_machines_reactive_capability_curves (synchronous_machine_mrid);",
        "CREATE INDEX synchronous_machines_reactive_capability_curves_reactive_capability_curve_mrid ON synchronous_machines_reactive_capability_curves (reactive_capability_curve_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

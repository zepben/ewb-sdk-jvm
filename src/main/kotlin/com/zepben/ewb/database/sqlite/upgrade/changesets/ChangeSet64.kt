/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.Change
import com.zepben.ewb.database.sqlite.upgrade.ChangeSet

internal fun changeSet64() = ChangeSet(
    64,
    listOf(
        // Network Changes
        `Create table hv customers`,
        `Create table lv substations`,
        `Create table ac line segment phases`,
        `Add grounding terminal to shunt compensators`,
        `Add fields to wire info tables`,
        `Add lv_substation to lv_feeders`,
        // Customer changes
        `Add code to pricing structure`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Create table hv customers` = Change(
    listOf(
        """CREATE TABLE hv_customers (
            mrid TEXT NOT NULL,
            name TEXT NULL,
            description TEXT NULL,
            num_diagram_objects INTEGER NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX hv_customers_mrid ON hv_customers (mrid);",
        "CREATE INDEX hv_customers_name ON hv_customers (name);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table lv substations` = Change(
    listOf(
        """CREATE TABLE lv_substations (
            mrid TEXT NOT NULL,
            name TEXT NULL,
            description TEXT NULL,
            num_diagram_objects INTEGER NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX lv_substations_mrid ON lv_substations (mrid);",
        "CREATE INDEX lv_substations_name ON lv_substations (name);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table ac line segment phases` = Change(
    listOf(
        """CREATE TABLE ac_line_segment_phases (
            mrid TEXT NOT NULL,
            name TEXT NULL,
            description TEXT NULL,
            num_diagram_objects INTEGER NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NULL,
            phase TEXT NOT NULL,
            sequence_number INTEGER NULL,
            wire_info_mrid TEXT NULL,
            ac_line_segment_mrid TEXT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX ac_line_segment_phases_mrid ON ac_line_segment_phases (mrid);",
        "CREATE INDEX ac_line_segment_phases_name ON ac_line_segment_phases (name);",
        "CREATE INDEX ac_line_segment_phases_ac_line_segment_mrid ON ac_line_segment_phases (ac_line_segment_mrid);",
        "CREATE INDEX ac_line_segment_phases_wire_info_mrid ON ac_line_segment_phases (wire_info_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Add grounding terminal to shunt compensators` = Change(
    listOf(
        "ALTER TABLE linear_shunt_compensators ADD COLUMN grounding_terminal_mrid TEXT NULL;",
        "CREATE INDEX linear_shunt_compensators_grounding_terminal_mrid ON linear_shunt_compensators (grounding_terminal_mrid);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)


@Suppress("ObjectPropertyName")
private val `Add fields to wire info tables` = Change(
    listOf(
        "ALTER TABLE overhead_wire_info ADD COLUMN size_description TEXT NULL;",
        "ALTER TABLE overhead_wire_info ADD COLUMN strand_count TEXT NULL;",
        "ALTER TABLE overhead_wire_info ADD COLUMN core_strand_count TEXT NULL;",
        "ALTER TABLE overhead_wire_info ADD COLUMN insulated BOOLEAN NULL;",
        "ALTER TABLE overhead_wire_info ADD COLUMN insulation_material TEXT NOT NULL DEFAULT 'UNKNOWN';",
        "ALTER TABLE overhead_wire_info ADD COLUMN insulation_thickness NUMBER NULL;",
        "ALTER TABLE cable_info ADD COLUMN size_description TEXT NULL;",
        "ALTER TABLE cable_info ADD COLUMN strand_count TEXT NULL;",
        "ALTER TABLE cable_info ADD COLUMN core_strand_count TEXT NULL;",
        "ALTER TABLE cable_info ADD COLUMN insulated BOOLEAN NULL;",
        "ALTER TABLE cable_info ADD COLUMN insulation_material TEXT NOT NULL DEFAULT 'UNKNOWN';",
        "ALTER TABLE cable_info ADD COLUMN insulation_thickness NUMBER NULL;",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Add lv_substation to lv_feeders` = Change(
    listOf(
        "ALTER TABLE lv_feeders ADD COLUMN normal_energizing_lv_substation_mrid TEXT NULL;",
        "CREATE INDEX lv_feeders_normal_head_terminal_mrid ON lv_feeders (normal_head_terminal_mrid);",
        "CREATE INDEX lv_feeders_normal_energizing_lv_substation_mrid ON lv_feeders (normal_energizing_lv_substation_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// ####################
// # Customer Changes #
// ####################

@Suppress("ObjectPropertyName")
private val `Add code to pricing structure` = Change(
    listOf(
        "ALTER TABLE pricing_structures ADD COLUMN code TEXT NULL;",
    ),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)

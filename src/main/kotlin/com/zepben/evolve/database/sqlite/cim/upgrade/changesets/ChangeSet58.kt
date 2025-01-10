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

internal fun changeSet58() = ChangeSet(
    58,
    listOf(
        // Network Change
        `Create table cuts`,
        `Create table clamps`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Create table cuts` = Change(
    listOf(
        """CREATE TABLE cuts (
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
            normal_open INTEGER NOT NULL,
            open INTEGER NOT NULL,
            rated_current NUMBER NULL,
            switch_info_mrid TEXT NULL,
            length_from_terminal_1 NUMBER NULL,
            ac_line_segment_mrid TEXT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX cuts_mrid ON cuts (mrid);",
        "CREATE INDEX cuts_ac_line_segment_mrid ON cuts (ac_line_segment_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create table clamps` = Change(
    listOf(
        """CREATE TABLE clamps (
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
            length_from_terminal_1 NUMBER NULL,
            ac_line_segment_mrid TEXT NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX clamps_mrid ON clamps (mrid);",
        "CREATE INDEX clamps_ac_line_segment_mrid ON clamps (ac_line_segment_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

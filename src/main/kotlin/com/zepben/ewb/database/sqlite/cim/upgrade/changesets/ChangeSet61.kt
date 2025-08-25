/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.Change
import com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet61() = ChangeSet(
    61,
    listOf(
        // Network Change
        `retype nonnull columns to null network`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `retype nonnull columns to null network` = Change(
    powerSystemResource("ac_line_segments"),

    // TODO: Every power system resource table needs the above.
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

fun powerSystemResource(tableName: String): List<String> =
    listOf(
        "ALTER TABLE $tableName RENAME COLUMN name to name_old",
        "ALTER TABLE $tableName ADD COLUMN name TEXT",
        "UPDATE $tableName SET name = name_old",
        "DROP INDEX ac_line_segments_name",
        "CREATE INDEX ac_line_segments_name ON $tableName (name)",
        "ALTER TABLE $tableName DROP COLUMN name_old",

        "ALTER TABLE $tableName RENAME COLUMN description to description_old",
        "ALTER TABLE $tableName ADD COLUMN description TEXT",
        "UPDATE $tableName SET description = description_old",

        "ALTER TABLE $tableName RENAME COLUMN num_diagram_objects to num_diagram_objects_old",
        "ALTER TABLE $tableName ADD COLUMN num_diagram_objects TEXT",
        "UPDATE $tableName SET num_diagram_objects = num_diagram_objects_old",

        "ALTER TABLE $tableName RENAME COLUMN num_controls to num_controls_old",
        "ALTER TABLE $tableName ADD COLUMN num_controls TEXT",
        "UPDATE $tableName SET num_controls = num_controls_old",
    )


@Suppress("ObjectPropertyName")
private val `rename RegulatingControlModeKind UNKNOWN_CONTROL_MODE to UNKNOWN` = Change(
    listOf(
        "UPDATE battery_controls SET mode = 'UNKNOWN' WHERE mode = 'UNKNOWN_CONTROL_MODE'",
        "UPDATE tap_changer_controls SET mode = 'UNKNOWN' WHERE mode = 'UNKNOWN_CONTROL_MODE'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `rename TransformerCoolingType UNKNOWN_COOLING_TYPE to UNKNOWN` = Change(
    listOf(
        "UPDATE power_transformer_end_ratings SET cooling_type = 'UNKNOWN' WHERE cooling_type = 'UNKNOWN_COOLING_TYPE'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `rename WindingConnection UNKNOWN_WINDING to UNKNOWN` = Change(
    listOf(
        "UPDATE power_transformer_ends SET connection_kind = 'UNKNOWN' WHERE connection_kind = 'UNKNOWN_WINDING'",
        "UPDATE transformer_end_info SET connection_kind = 'UNKNOWN' WHERE connection_kind = 'UNKNOWN_WINDING'",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

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
import kotlin.collections.plus

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
    powerSystemResource("ac_line_segments")
        + pole("poles")
        + usagePoint("usage_points")
        + nameType("name_types")
        + analog("analogs")
        + energyConsumer("energy_consumers")
        + energySource("energy_sources")
        + regulatingCondEq()
    ,

    // TODO: Every power system resource table needs the above.
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

fun streetDetail(tableName: String): List<String> =
    alterToNullableColumn(tableName, "building_name") +
    alterToNullableColumn(tableName, "floor_identification") +
    alterToNullableColumn(tableName, "street_name") +
    alterToNullableColumn(tableName, "number") +
    alterToNullableColumn(tableName, "suite_number") +
    alterToNullableColumn(tableName, "type") +
    alterToNullableColumn(tableName, "display_address")

fun streetAddress(tableName: String): List<String> =
    alterToNullableColumn(tableName, "postal_code") +
        alterToNullableColumn(tableName, "po_box") +
        streetDetail(tableName)

fun document(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "title") +
        alterToNullableColumn(tableName, "author") +
        alterToNullableColumn(tableName, "type") +
        alterToNullableColumn(tableName, "status")

fun transformerEnd(tableName: String): List<String> =
    identifiedObject(tableName)+
        alterToNullableColumn(tableName, "grounded")

fun tapChanger(tableName: String): List<String> =
    powerSystemResource(tableName)+
        alterToNullableColumn(tableName, "control_enabled")

fun synchronousMachine(tableName: String): List<String> =
    identifiedObject(tableName)+
        alterToNullableColumn(tableName, "earthing")

fun shuntCompensator(tableName: String): List<String> =
    regulatingCondEq(tableName)+
        alterToNullableColumn(tableName, "grounded")

fun regulatingCondEq(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "control_enabled")

fun energySource(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "is_external_grid")

fun energyConsumer(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "grounded")

fun analog(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "positive_flow_in")

fun nameType(tableName: String): List<String> =
    alterToNullableColumn(tableName, "description")

fun usagePoint(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "is_virtual")

fun pole(tableName: String): List<String> =
    identifiedObject("poles") +
        alterToNullableColumn(tableName, "classification")

fun identifiedObject(tableName: String): List<String> =
    listOf(
        "ALTER TABLE $tableName RENAME COLUMN name to name_old",
        "ALTER TABLE $tableName ADD COLUMN name TEXT",
        "UPDATE $tableName SET name = name_old",

        "DROP INDEX ac_line_segments_name",
        "CREATE INDEX ac_line_segments_name ON $tableName (name)",
        "ALTER TABLE $tableName DROP COLUMN name_old",
    ) + alterToNullableColumn(tableName, "description") +
        alterToNullableColumn(tableName, "num_diagram_objects")

fun powerSystemResource(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "num_controls")

fun alterToNullableColumn(tableName: String, columnName: String): List<String> =
    listOf(
        "ALTER TABLE $tableName RENAME COLUMN $columnName to ${columnName}_old",
        "ALTER TABLE $tableName ADD COLUMN $columnName TEXT",
        "UPDATE $tableName SET $columnName = ${columnName}_old",
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

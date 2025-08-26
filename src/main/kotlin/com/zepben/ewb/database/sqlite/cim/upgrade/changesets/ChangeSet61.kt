/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets

import com.zepben.ewb.cim.iec61968.common.StreetDetail
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires.TableTransformerEnds
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires.TableTransformerStarImpedances
import com.zepben.ewb.database.sqlite.cim.upgrade.Change
import com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSet
import com.zepben.ewb.database.sqlite.common.SqliteTable
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
    powerSystemResource("ac_line_segments") +
        identifiedObject("accumulators") +
        analog("analogs") +
        identifiedObject("asset_owners") +
        identifiedObject("base_voltages") +
        powerSystemResource("battery_controls") +
        powerSystemResource("battery_units") +
        powerSystemResource("breakers") +
        powerSystemResource("busbar_sections") +
        identifiedObject("cable_info") +
        powerSystemResource("circuits") +
        powerSystemResource("clamps") +
        identifiedObject("connectivity_nodes") +
        identifiedObject("controls") +
        powerSystemResource("current_relays") +
        identifiedObject("current_transformer_info") +
        powerSystemResource("current_transformers") +
        document("customer_agreements") +
        identifiedObject("customers") +
        powerSystemResource("cuts") +
        identifiedObject("diagram_objects") +
        identifiedObject("diagrams") +
        powerSystemResource("disconnectors") +
        identifiedObject("discretes") +
        powerSystemResource("distance_relays") +
        powerSystemResource("energy_consumer_phases") +
        energyConsumer("energy_consumers") +
        powerSystemResource("energy_source_phases") +
        energySource("energy_sources") +
        powerSystemResource("equivalent_branches") +
        powerSystemResource("ev_charging_units") +
        powerSystemResource("fault_indicators") +
        powerSystemResource("feeders") +
        powerSystemResource("fuses") +
        identifiedObject("geographical_regions") +
        powerSystemResource("ground_disconnectors") +
        powerSystemResource("grounding_impedances") +
        powerSystemResource("grounds") +
        powerSystemResource("jumpers") +
        powerSystemResource("junctions") +
        shuntCompensator("linear_shunt_compensators") +
        powerSystemResource("load_break_switches") +
        streetAddress("location_street_addresses") +
        identifiedObject("locations") +
        identifiedObject("loops") +
        powerSystemResource("lv_feeders") +
        identifiedObject("meters") +
        nameType("name_types") +
        identifiedObject("no_load_tests") +
        identifiedObject("open_circuit_tests") +
        document("operational_restrictions") +
        identifiedObject("organisations") +
        identifiedObject("overhead_wire_info") +
        identifiedObject("pan_demand_response_functions") +
        identifiedObject("per_length_phase_impedances") +
        identifiedObject("per_length_sequence_impedances") +
        powerSystemResource("petersen_coils") +
        powerSystemResource("photo_voltaic_units") +
        identifiedObject("poles") +
        identifiedObject("potential_transformer_info") +
        powerSystemResource("potential_transformers") +
        powerSystemResource("power_electronics_connection_phases") +
        regulatingCondEq("power_electronics_connections") +
        powerSystemResource("power_electronics_wind_units") +
        transformerEnd("power_transformer_ends") +
        identifiedObject("power_transformer_info") +
        powerSystemResource("power_transformers") +
        document("pricing_structures") +
        identifiedObject("protection_relay_schemes") +
        powerSystemResource("protection_relay_systems") +
        tapChanger("ratio_tap_changers") +
        identifiedObject("reactive_capability_curves") +
        powerSystemResource("reclosers") +
        identifiedObject("relay_info") +
        identifiedObject("remote_controls") +
        identifiedObject("remote_sources") +
        regulatingCondEq("rotating_machines") +
        powerSystemResource("series_compensators") +
        identifiedObject("short_circuit_tests") +
        identifiedObject("shunt_compensator_info") +
        powerSystemResource("sites") +
        regulatingCondEq("static_var_compensators") +
        identifiedObject("streetlights") +
        identifiedObject("sub_geographical_regions") +
        powerSystemResource("substations") +
        identifiedObject("switch_info") +
        synchronousMachine("synchronous_machines") +
        powerSystemResource("tap_changer_controls") +
        document("tariffs") +
        identifiedObject("terminals") +
        identifiedObject("transformer_end_info") +
        identifiedObject("transformer_star_impedances") +
        identifiedObject("transformer_tank_info") +
        usagePoint("usage_points") +
        powerSystemResource("voltage_relays")
    ,

    // TODO: Every power system resource table needs the above.
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

fun streetAddress(tableName: String): List<String> =
    alterToNullableColumn(tableName, "postal_code") +
        alterToNullableColumn(tableName, "po_box") +
        alterToNullableColumn(tableName, "building_name") +
        alterToNullableColumn(tableName, "floor_identification") +
        alterToNullableColumn(tableName, "street_name") +
        alterToNullableColumn(tableName, "number") +
        alterToNullableColumn(tableName, "suite_number") +
        alterToNullableColumn(tableName, "type") +
        alterToNullableColumn(tableName, "display_address")

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
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "earthing")

fun shuntCompensator(tableName: String): List<String> =
    regulatingCondEq(tableName) +
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
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "classification")

fun identifiedObject(tableName: String): List<String> =
    listOf(
        "ALTER TABLE $tableName RENAME COLUMN name to name_old",
        "ALTER TABLE $tableName ADD COLUMN name TEXT",
        "UPDATE $tableName SET name = name_old",

        "DROP INDEX ${tableName}_name",
        "CREATE INDEX ${tableName}_name ON $tableName (name)",
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

fun tablesToUpdate(): List<SqliteTable> = mutableListOf(
)

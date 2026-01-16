/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.Change
import com.zepben.ewb.database.sqlite.upgrade.ChangeSet

internal fun changeSet61() = ChangeSet(
    61,
    listOf(
        // Network Change
        `retype nonnull columns to null network`,
        `retype nonnull columns to null diagram`,
        `retype nonnull columns to null customer`,
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
        powerSystemResourceNoIndex("battery_controls") +
        powerSystemResource("battery_units") +
        powerSystemResource("breakers") +
        powerSystemResource("busbar_sections") +
        identifiedObject("cable_info") +
        powerSystemResource("circuits") +
        powerSystemResourceNoIndex("clamps") +
        identifiedObject("connectivity_nodes") +
        identifiedObject("controls") +
        powerSystemResource("current_relays") +
        identifiedObject("current_transformer_info") +
        powerSystemResource("current_transformers") +
        powerSystemResourceNoIndex("cuts") +
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
        powerSystemResourceNoIndex("grounding_impedances") +
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
        identifiedObjectNoIndex("pan_demand_response_functions") +
        identifiedObjectNoIndex("per_length_phase_impedances") +
        identifiedObject("per_length_sequence_impedances") +
        powerSystemResourceNoIndex("petersen_coils") +
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
        identifiedObject("protection_relay_schemes") +
        powerSystemResource("protection_relay_systems") +
        tapChanger("ratio_tap_changers") +
        identifiedObjectNoIndex("reactive_capability_curves") +
        powerSystemResource("reclosers") +
        identifiedObject("relay_info") +
        identifiedObject("remote_controls") +
        identifiedObject("remote_sources") +
        powerSystemResource("series_compensators") +
        identifiedObject("short_circuit_tests") +
        identifiedObject("shunt_compensator_info") +
        powerSystemResource("sites") +
        regulatingCondEqNoIndex("static_var_compensators") +
        identifiedObject("streetlights") +
        identifiedObject("sub_geographical_regions") +
        powerSystemResource("substations") +
        identifiedObject("switch_info") +
        synchronousMachine("synchronous_machines") +
        powerSystemResource("tap_changer_controls") +
        identifiedObject("terminals") +
        identifiedObject("transformer_end_info") +
        identifiedObject("transformer_star_impedances") +
        identifiedObject("transformer_tank_info") +
        usagePoint("usage_points") +
        powerSystemResource("voltage_relays"),

    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `retype nonnull columns to null diagram` = Change(
    identifiedObject("diagram_objects") +
        identifiedObject("diagrams") +
        alterToNullableColumn("diagram_object_points", "x_position") +
        alterToNullableColumn("diagram_object_points", "y_position"),
    targetDatabases = setOf(DatabaseType.DIAGRAM)
)

@Suppress("ObjectPropertyName")
private val `retype nonnull columns to null customer` = Change(
    document("customer_agreements") +
        identifiedObject("customers") +
        document("pricing_structures") +
        document("tariffs"),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)

@Suppress("SameParameterValue")
private fun streetAddress(tableName: String): List<String> =
    alterToNullableColumn(tableName, "postal_code") +
        alterToNullableColumn(tableName, "po_box") +
        alterToNullableColumn(tableName, "building_name") +
        alterToNullableColumn(tableName, "floor_identification") +
        alterToNullableColumn(tableName, "name") +
        alterToNullableColumn(tableName, "number") +
        alterToNullableColumn(tableName, "suite_number") +
        alterToNullableColumn(tableName, "type") +
        alterToNullableColumn(tableName, "display_address")

private fun document(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "title") +
        alterToNullableColumn(tableName, "author_name") +
        alterToNullableColumn(tableName, "type") +
        alterToNullableColumn(tableName, "status")

@Suppress("SameParameterValue")
private fun transformerEnd(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "grounded")

@Suppress("SameParameterValue")
private fun tapChanger(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "control_enabled")

@Suppress("SameParameterValue")
private fun synchronousMachine(tableName: String): List<String> =
    identifiedObjectNoIndex(tableName) +
        alterToNullableColumn(tableName, "earthing")

@Suppress("SameParameterValue")
private fun shuntCompensator(tableName: String): List<String> =
    regulatingCondEq(tableName) +
        alterToNullableColumn(tableName, "grounded")

@Suppress("SameParameterValue")
private fun regulatingCondEqNoIndex(tableName: String): List<String> =
    powerSystemResourceNoIndex(tableName) +
        alterToNullableColumn(tableName, "control_enabled")

private fun regulatingCondEq(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "control_enabled")

@Suppress("SameParameterValue")
private fun energySource(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "is_external_grid")

@Suppress("SameParameterValue")
private fun energyConsumer(tableName: String): List<String> =
    powerSystemResource(tableName) +
        alterToNullableColumn(tableName, "grounded")

@Suppress("SameParameterValue")
private fun analog(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "positive_flow_in")

@Suppress("SameParameterValue")
private fun nameType(tableName: String): List<String> =
    alterToNullableColumn(tableName, "description")

@Suppress("SameParameterValue")
private fun usagePoint(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "is_virtual")

private fun identifiedObjectNoIndex(tableName: String): List<String> =
    alterToNullableColumn(tableName, "description") +
        alterToNullableColumn(tableName, "num_diagram_objects")

private fun identifiedObject(tableName: String): List<String> =
// It has been observed that this index has been dropped on some tables in the wild. e.g. NorthPower where it is suspected to be a side effect of
    // scripts that are run on their data post the extract from PoF.
    listOf(
        "DROP INDEX IF EXISTS ${tableName}_name",
    ) +
        alterToNullableColumn(tableName, "name") +
        alterToNullableColumn(tableName, "description") +
        alterToNullableColumn(tableName, "num_diagram_objects") +
        listOf(
            "CREATE INDEX ${tableName}_name ON $tableName (name)",
        )

private fun powerSystemResourceNoIndex(tableName: String): List<String> =
    identifiedObjectNoIndex(tableName) +
        alterToNullableColumn(tableName, "num_controls")

private fun powerSystemResource(tableName: String): List<String> =
    identifiedObject(tableName) +
        alterToNullableColumn(tableName, "num_controls")

private fun alterToNullableColumn(tableName: String, columnName: String): List<String> =
    listOf(
        "ALTER TABLE $tableName RENAME COLUMN $columnName to ${columnName}_old",
        "ALTER TABLE $tableName ADD COLUMN $columnName TEXT",
        "UPDATE $tableName SET $columnName = ${columnName}_old",
        "ALTER TABLE $tableName DROP COLUMN ${columnName}_old"
    )

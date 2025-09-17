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

internal fun changeSet62() = ChangeSet(
    62,
    listOf(
        // Network changes
        `retype missed non-null network columns`,
        `retype incorrectly converted network columns`,
        `add missing network indexes`,

        // Diagram changes
        `retype incorrectly converted columns diagram`,
        `add missing diagram indexes`,

        // Customer changes
        `retype missed non-null columns customer`,
        `retype incorrectly converted columns customer`,
        `add missing customer indexes`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `retype missed non-null network columns` = Change(
    listOf(
        *alterToNullableWithIndex("battery_controls", "name", "TEXT"),
        *alterToNullableWithIndex("clamps", "name", "TEXT"),
        *alterToNullableWithIndex("cuts", "name", "TEXT"),
        *alterToNullableWithIndex("grounding_impedances", "name", "TEXT"),
        *alterToNullable("operational_restrictions", "comment", "TEXT"),
        *alterToNullableWithIndex("pan_demand_response_functions", "name", "TEXT"),
        *alterToNullableWithIndex("per_length_phase_impedances", "name", "TEXT"),
        *alterToNullableWithIndex("petersen_coils", "name", "TEXT"),
        *alterToNullable("poles", "classification", "TEXT"),
        *alterToNullableWithIndex("reactive_capability_curves", "name", "TEXT"),
        *alterToNullableWithIndex("static_var_compensators", "name", "TEXT"),
        *alterToNullableWithIndex("synchronous_machines", "name", "TEXT"),
        *alterToNullable("synchronous_machines", "num_controls", "INTEGER"),
        *alterToNullable("synchronous_machines", "control_enabled", "BOOLEAN"),
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `retype incorrectly converted network columns` = Change(
    listOf(
        *alterToNullable("ac_line_segments", "num_diagram_objects", "INTEGER"),
        *alterToNullable("ac_line_segments", "num_controls", "INTEGER"),
        *alterToNullable("accumulators", "num_diagram_objects", "INTEGER"),
        *alterToNullable("analogs", "num_diagram_objects", "INTEGER"),
        *alterToNullable("analogs", "positive_flow_in", "BOOLEAN"),
        *alterToNullable("asset_owners", "num_diagram_objects", "INTEGER"),
        *alterToNullable("base_voltages", "num_diagram_objects", "INTEGER"),
        *alterToNullable("battery_controls", "num_diagram_objects", "INTEGER"),
        *alterToNullable("battery_controls", "num_controls", "INTEGER"),
        *alterToNullable("battery_units", "num_diagram_objects", "INTEGER"),
        *alterToNullable("battery_units", "num_controls", "INTEGER"),
        *alterToNullable("breakers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("breakers", "num_controls", "INTEGER"),
        *alterToNullable("busbar_sections", "num_diagram_objects", "INTEGER"),
        *alterToNullable("busbar_sections", "num_controls", "INTEGER"),
        *alterToNullable("cable_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("circuits", "num_diagram_objects", "INTEGER"),
        *alterToNullable("circuits", "num_controls", "INTEGER"),
        *alterToNullable("clamps", "num_diagram_objects", "INTEGER"),
        *alterToNullable("clamps", "num_controls", "INTEGER"),
        *alterToNullable("connectivity_nodes", "num_diagram_objects", "INTEGER"),
        *alterToNullable("controls", "num_diagram_objects", "INTEGER"),
        *alterToNullable("current_relays", "num_diagram_objects", "INTEGER"),
        *alterToNullable("current_relays", "num_controls", "INTEGER"),
        *alterToNullable("current_transformer_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("current_transformers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("current_transformers", "num_controls", "INTEGER"),
        *alterToNullable("cuts", "num_diagram_objects", "INTEGER"),
        *alterToNullable("cuts", "num_controls", "INTEGER"),
        *alterToNullable("disconnectors", "num_diagram_objects", "INTEGER"),
        *alterToNullable("disconnectors", "num_controls", "INTEGER"),
        *alterToNullable("discretes", "num_diagram_objects", "INTEGER"),
        *alterToNullable("distance_relays", "num_diagram_objects", "INTEGER"),
        *alterToNullable("distance_relays", "num_controls", "INTEGER"),
        *alterToNullable("energy_consumer_phases", "num_diagram_objects", "INTEGER"),
        *alterToNullable("energy_consumer_phases", "num_controls", "INTEGER"),
        *alterToNullable("energy_consumers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("energy_consumers", "num_controls", "INTEGER"),
        *alterToNullable("energy_consumers", "grounded", "BOOLEAN"),
        *alterToNullable("energy_source_phases", "num_diagram_objects", "INTEGER"),
        *alterToNullable("energy_source_phases", "num_controls", "INTEGER"),
        *alterToNullable("energy_sources", "num_diagram_objects", "INTEGER"),
        *alterToNullable("energy_sources", "num_controls", "INTEGER"),
        *alterToNullable("energy_sources", "is_external_grid", "BOOLEAN"),
        *alterToNullable("equivalent_branches", "num_diagram_objects", "INTEGER"),
        *alterToNullable("equivalent_branches", "num_controls", "INTEGER"),
        *alterToNullable("ev_charging_units", "num_diagram_objects", "INTEGER"),
        *alterToNullable("ev_charging_units", "num_controls", "INTEGER"),
        *alterToNullable("fault_indicators", "num_diagram_objects", "INTEGER"),
        *alterToNullable("fault_indicators", "num_controls", "INTEGER"),
        *alterToNullable("feeders", "num_diagram_objects", "INTEGER"),
        *alterToNullable("feeders", "num_controls", "INTEGER"),
        *alterToNullable("fuses", "num_diagram_objects", "INTEGER"),
        *alterToNullable("fuses", "num_controls", "INTEGER"),
        *alterToNullable("geographical_regions", "num_diagram_objects", "INTEGER"),
        *alterToNullable("grounds", "num_diagram_objects", "INTEGER"),
        *alterToNullable("grounds", "num_controls", "INTEGER"),
        *alterToNullable("ground_disconnectors", "num_diagram_objects", "INTEGER"),
        *alterToNullable("ground_disconnectors", "num_controls", "INTEGER"),
        *alterToNullable("grounding_impedances", "num_diagram_objects", "INTEGER"),
        *alterToNullable("grounding_impedances", "num_controls", "INTEGER"),
        *alterToNullable("jumpers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("jumpers", "num_controls", "INTEGER"),
        *alterToNullable("junctions", "num_diagram_objects", "INTEGER"),
        *alterToNullable("junctions", "num_controls", "INTEGER"),
        *alterToNullable("linear_shunt_compensators", "num_diagram_objects", "INTEGER"),
        *alterToNullable("linear_shunt_compensators", "num_controls", "INTEGER"),
        *alterToNullable("linear_shunt_compensators", "control_enabled", "BOOLEAN"),
        *alterToNullable("linear_shunt_compensators", "grounded", "BOOLEAN"),
        *alterToNullable("load_break_switches", "num_diagram_objects", "INTEGER"),
        *alterToNullable("load_break_switches", "num_controls", "INTEGER"),
        *alterToNullable("locations", "num_diagram_objects", "INTEGER"),
        *alterToNullable("loops", "num_diagram_objects", "INTEGER"),
        *alterToNullable("lv_feeders", "num_diagram_objects", "INTEGER"),
        *alterToNullable("lv_feeders", "num_controls", "INTEGER"),
        *alterToNullable("meters", "num_diagram_objects", "INTEGER"),
        *alterToNullable("no_load_tests", "num_diagram_objects", "INTEGER"),
        *alterToNullable("open_circuit_tests", "num_diagram_objects", "INTEGER"),
        *alterToNullable("operational_restrictions", "num_diagram_objects", "INTEGER"),
        *alterToNullable("organisations", "num_diagram_objects", "INTEGER"),
        *alterToNullable("overhead_wire_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("pan_demand_response_functions", "num_diagram_objects", "INTEGER"),
        *alterToNullable("per_length_phase_impedances", "num_diagram_objects", "INTEGER"),
        *alterToNullable("per_length_sequence_impedances", "num_diagram_objects", "INTEGER"),
        *alterToNullable("petersen_coils", "num_diagram_objects", "INTEGER"),
        *alterToNullable("petersen_coils", "num_controls", "INTEGER"),
        *alterToNullable("photo_voltaic_units", "num_diagram_objects", "INTEGER"),
        *alterToNullable("photo_voltaic_units", "num_controls", "INTEGER"),
        *alterToNullable("poles", "num_diagram_objects", "INTEGER"),
        *alterToNullable("potential_transformer_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("potential_transformers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("potential_transformers", "num_controls", "INTEGER"),
        *alterToNullable("power_electronics_connections", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_electronics_connections", "num_controls", "INTEGER"),
        *alterToNullable("power_electronics_connections", "control_enabled", "BOOLEAN"),
        *alterToNullable("power_electronics_connection_phases", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_electronics_connection_phases", "num_controls", "INTEGER"),
        *alterToNullable("power_electronics_wind_units", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_electronics_wind_units", "num_controls", "INTEGER"),
        *alterToNullable("power_transformer_ends", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_transformer_ends", "grounded", "BOOLEAN"),
        *alterToNullable("power_transformer_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_transformers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("power_transformers", "num_controls", "INTEGER"),
        *alterToNullable("protection_relay_schemes", "num_diagram_objects", "INTEGER"),
        *alterToNullable("protection_relay_systems", "num_diagram_objects", "INTEGER"),
        *alterToNullable("protection_relay_systems", "num_controls", "INTEGER"),
        *alterToNullable("ratio_tap_changers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("ratio_tap_changers", "num_controls", "INTEGER"),
        *alterToNullable("ratio_tap_changers", "control_enabled", "BOOLEAN"),
        *alterToNullable("reactive_capability_curves", "num_diagram_objects", "INTEGER"),
        *alterToNullable("reclosers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("reclosers", "num_controls", "INTEGER"),
        *alterToNullable("relay_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("remote_controls", "num_diagram_objects", "INTEGER"),
        *alterToNullable("remote_sources", "num_diagram_objects", "INTEGER"),
        *alterToNullable("series_compensators", "num_diagram_objects", "INTEGER"),
        *alterToNullable("series_compensators", "num_controls", "INTEGER"),
        *alterToNullable("short_circuit_tests", "num_diagram_objects", "INTEGER"),
        *alterToNullable("shunt_compensator_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("sites", "num_diagram_objects", "INTEGER"),
        *alterToNullable("sites", "num_controls", "INTEGER"),
        *alterToNullable("streetlights", "num_diagram_objects", "INTEGER"),
        *alterToNullable("sub_geographical_regions", "num_diagram_objects", "INTEGER"),
        *alterToNullable("substations", "num_diagram_objects", "INTEGER"),
        *alterToNullable("substations", "num_controls", "INTEGER"),
        *alterToNullable("static_var_compensators", "num_diagram_objects", "INTEGER"),
        *alterToNullable("static_var_compensators", "num_controls", "INTEGER"),
        *alterToNullable("static_var_compensators", "control_enabled", "BOOLEAN"),
        *alterToNullable("switch_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("synchronous_machines", "num_diagram_objects", "INTEGER"),
        *alterToNullable("synchronous_machines", "num_controls", "INTEGER"),
        *alterToNullable("synchronous_machines", "control_enabled", "BOOLEAN"),
        *alterToNullable("synchronous_machines", "earthing", "BOOLEAN"),
        *alterToNullable("tap_changer_controls", "num_diagram_objects", "INTEGER"),
        *alterToNullable("tap_changer_controls", "num_controls", "INTEGER"),
        *alterToNullable("terminals", "num_diagram_objects", "INTEGER"),
        *alterToNullable("transformer_end_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("transformer_star_impedances", "num_diagram_objects", "INTEGER"),
        *alterToNullable("transformer_tank_info", "num_diagram_objects", "INTEGER"),
        *alterToNullable("usage_points", "num_diagram_objects", "INTEGER"),
        *alterToNullable("usage_points", "is_virtual", "BOOLEAN"),
        *alterToNullable("voltage_relays", "num_diagram_objects", "INTEGER"),
        *alterToNullable("voltage_relays", "num_controls", "INTEGER"),
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `add missing network indexes` = Change(
    listOf(
        //todo
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// ###################
// # Diagram Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `retype incorrectly converted columns diagram` = Change(
    listOf(
        *alterToNotNullable("diagram_object_points", "x_position", "NUMBER"),
        *alterToNotNullable("diagram_object_points", "y_position", "NUMBER"),
        *alterToNullable("diagram_objects", "num_diagram_objects", "INTEGER"),
        *alterToNullable("diagrams", "num_diagram_objects", "INTEGER"),
    ),
    targetDatabases = setOf(DatabaseType.DIAGRAM)
)

@Suppress("ObjectPropertyName")
private val `add missing diagram indexes` = Change(
    listOf(
        //todo
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// ####################
// # Customer Changes #
// ####################

@Suppress("ObjectPropertyName")
private val `retype missed non-null columns customer` = Change(
    listOf(
        *alterToNullable("customer_agreements", "comment", "TEXT"),
        *alterToNullableWithIndex("organisations", "name", "TEXT"),
        *alterToNullable("organisations", "description", "TEXT"),
        *alterToNullable("organisations", "num_diagram_objects", "INTEGER"),
        *alterToNullable("pricing_structures", "comment", "TEXT"),
        *alterToNullable("tariffs", "comment", "TEXT"),
    ),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)

@Suppress("ObjectPropertyName")
private val `retype incorrectly converted columns customer` = Change(
    listOf(
        *alterToNullable("customer_agreements", "num_diagram_objects", "INTEGER"),
        *alterToNullable("customers", "num_diagram_objects", "INTEGER"),
        *alterToNullable("organisations", "num_diagram_objects", "INTEGER"),
        *alterToNullable("pricing_structures", "num_diagram_objects", "INTEGER"),
        *alterToNullable("tariffs", "num_diagram_objects", "INTEGER"),
    ),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)

@Suppress("ObjectPropertyName")
private val `add missing customer indexes` = Change(
    listOf(
        //todo
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("SameParameterValue")
private fun alterToNullableWithIndex(tableName: String, columnName: String, type: String): Array<String> =
    arrayOf(
        "DROP INDEX IF EXISTS ${tableName}_$columnName",
        *alterToNullable(tableName, columnName, type),
        "CREATE INDEX ${tableName}_$columnName $tableName ($columnName)",
    )

private fun alterToNullable(tableName: String, columnName: String, type: String): Array<String> =
    arrayOf(
        "ALTER TABLE $tableName RENAME COLUMN $columnName to ${columnName}_old",
        "ALTER TABLE $tableName ADD COLUMN $columnName $type",
        "UPDATE $tableName SET $columnName = ${columnName}_old",
        "ALTER TABLE $tableName DROP COLUMN ${columnName}_old",
    )

@Suppress("SameParameterValue")
private fun alterToNotNullable(tableName: String, columnName: String, type: String): Array<String> =
    arrayOf(
        "ALTER TABLE $tableName RENAME COLUMN $columnName to ${columnName}_old",
        "ALTER TABLE $tableName ADD COLUMN $columnName $type NOT NULL",
        "UPDATE $tableName SET $columnName = ${columnName}_old",
        "ALTER TABLE $tableName DROP COLUMN ${columnName}_old",
    )

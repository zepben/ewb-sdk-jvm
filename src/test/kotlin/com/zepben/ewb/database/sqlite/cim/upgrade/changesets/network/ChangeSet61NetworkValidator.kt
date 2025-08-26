/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.network

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

// TODO: This needs all the data model changes verified
object ChangeSet61NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        // TODO: every table that is a power system resource needs to be added here
        *`populate powerSystemResource` ("ac_line_segments"),
        *`populate identifiedObject` ("accumulators"),
        *`populate analog` ("analogs"),
        *`populate identifiedObject` ("asset_owners"),
        *`populate identifiedObject` ("base_voltages"),
        *`populate powerSystemResource` ("battery_controls"),
        *`populate powerSystemResource` ("battery_units"),
        *`populate powerSystemResource` ("breakers"),
        *`populate powerSystemResource` ("busbar_sections"),
        *`populate identifiedObject` ("cable_info"),
        *`populate powerSystemResource` ("circuits"),
        *`populate powerSystemResource` ("clamps"),
        *`populate identifiedObject` ("connectivity_nodes"),
        *`populate identifiedObject` ("controls"),
        *`populate powerSystemResource` ("current_relays"),
        *`populate identifiedObject` ("current_transformer_info"),
        *`populate powerSystemResource` ("current_transformers"),
        *`populate document` ("customer_agreements"),
        *`populate identifiedObject` ("customers"),
        *`populate powerSystemResource` ("cuts"),
        *`populate identifiedObject` ("diagram_objects"),
        *`populate identifiedObject` ("diagrams"),
        *`populate powerSystemResource` ("disconnectors"),
        *`populate identifiedObject` ("discretes"),
        *`populate powerSystemResource` ("distance_relays"),
        *`populate powerSystemResource` ("energy_consumer_phases"),
        *`populate energyConsumer` ("energy_consumers"),
        *`populate powerSystemResource` ("energy_source_phases"),
        *`populate energySource` ("energy_sources"),
        *`populate powerSystemResource` ("equivalent_branches"),
        *`populate powerSystemResource` ("ev_charging_units"),
        *`populate powerSystemResource` ("fault_indicators"),
        *`populate powerSystemResource` ("feeders"),
        *`populate powerSystemResource` ("fuses"),
        *`populate identifiedObject` ("geographical_regions"),
        *`populate powerSystemResource` ("ground_disconnectors"),
        *`populate powerSystemResource` ("grounding_impedances"),
        *`populate powerSystemResource` ("grounds"),
        *`populate powerSystemResource` ("jumpers"),
        *`populate powerSystemResource` ("junctions"),
        *`populate shuntCompensator` ("linear_shunt_compensators"),
        *`populate powerSystemResource` ("load_break_switches"),
        *`populate streetAddress` ("location_street_addresses"),
        *`populate identifiedObject` ("locations"),
        *`populate identifiedObject` ("loops"),
        *`populate powerSystemResource` ("lv_feeders"),
        *`populate identifiedObject` ("meters"),
        *`populate nameType` ("name_types"),
        *`populate identifiedObject` ("no_load_tests"),
        *`populate identifiedObject` ("open_circuit_tests"),
        *`populate document` ("operational_restrictions"),
        *`populate identifiedObject` ("organisations"),
        *`populate identifiedObject` ("overhead_wire_info"),
        *`populate identifiedObject` ("pan_demand_response_functions"),
        *`populate identifiedObject` ("per_length_phase_impedances"),
        *`populate identifiedObject` ("per_length_sequence_impedances"),
        *`populate powerSystemResource` ("petersen_coils"),
        *`populate powerSystemResource` ("photo_voltaic_units"),
        *`populate identifiedObject` ("poles"),
        *`populate identifiedObject` ("potential_transformer_info"),
        *`populate powerSystemResource` ("potential_transformers"),
        *`populate powerSystemResource` ("power_electronics_connection_phases"),
        *`populate regulatingCondEq` ("power_electronics_connections"),
        *`populate powerSystemResource` ("power_electronics_wind_units"),
        *`populate transformerEnd` ("power_transformer_ends"),
        *`populate identifiedObject` ("power_transformer_info"),
        *`populate powerSystemResource` ("power_transformers"),
        *`populate document` ("pricing_structures"),
        *`populate identifiedObject` ("protection_relay_schemes"),
        *`populate powerSystemResource` ("protection_relay_systems"),
        *`populate tapChanger` ("ratio_tap_changers"),
        *`populate identifiedObject` ("reactive_capability_curves"),
        *`populate powerSystemResource` ("reclosers"),
        *`populate identifiedObject` ("relay_info"),
        *`populate identifiedObject` ("remote_controls"),
        *`populate identifiedObject` ("remote_sources"),
        *`populate regulatingCondEq` ("rotating_machines"),
        *`populate powerSystemResource` ("series_compensators"),
        *`populate identifiedObject` ("short_circuit_tests"),
        *`populate identifiedObject` ("shunt_compensator_info"),
        *`populate powerSystemResource` ("sites"),
        *`populate regulatingCondEq` ("static_var_compensators"),
        *`populate identifiedObject` ("streetlights"),
        *`populate identifiedObject` ("sub_geographical_regions"),
        *`populate powerSystemResource` ("substations"),
        *`populate identifiedObject` ("switch_info"),
        *`populate synchronousMachine` ("synchronous_machines"),
        *`populate powerSystemResource` ("tap_changer_controls"),
        *`populate document` ("tariffs"),
        *`populate identifiedObject` ("terminals"),
        *`populate identifiedObject` ("transformer_end_info"),
        *`populate identifiedObject` ("transformer_star_impedances"),
        *`populate identifiedObject` ("transformer_tank_info"),
        *`populate usagePoint` ("usage_points"),
        *`populate powerSystemResource` ("voltage_relays"),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        // TODO: every table that is a power system resource needs a validate power system resources call
        `validate powerSystemResource` (statement, "ac_line_segments")
        `validate identifiedObject` (statement, "accumulators")
        `validate analog` (statement, "analogs")
        `validate identifiedObject` (statement, "asset_owners")
        `validate identifiedObject` (statement, "base_voltages")
        `validate powerSystemResource` (statement, "battery_controls")
        `validate powerSystemResource` (statement, "battery_units")
        `validate powerSystemResource` (statement, "breakers")
        `validate powerSystemResource` (statement, "busbar_sections")
        `validate identifiedObject` (statement, "cable_info")
        `validate powerSystemResource` (statement, "circuits")
        `validate powerSystemResource` (statement, "clamps")
        `validate identifiedObject` (statement, "connectivity_nodes")
        `validate identifiedObject` (statement, "controls")
        `validate powerSystemResource` (statement, "current_relays")
        `validate identifiedObject` (statement, "current_transformer_info")
        `validate powerSystemResource` (statement, "current_transformers")
        `validate document` (statement, "customer_agreements")
        `validate identifiedObject` (statement, "customers")
        `validate powerSystemResource` (statement, "cuts")
        `validate identifiedObject` (statement, "diagram_objects")
        `validate identifiedObject` (statement, "diagrams")
        `validate powerSystemResource` (statement, "disconnectors")
        `validate identifiedObject` (statement, "discretes")
        `validate powerSystemResource` (statement, "distance_relays")
        `validate powerSystemResource` (statement, "energy_consumer_phases")
        `validate energyConsumer` (statement, "energy_consumers")
        `validate powerSystemResource` (statement, "energy_source_phases")
        `validate energySource` (statement, "energy_sources")
        `validate powerSystemResource` (statement, "equivalent_branches")
        `validate powerSystemResource` (statement, "ev_charging_units")
        `validate powerSystemResource` (statement, "fault_indicators")
        `validate powerSystemResource` (statement, "feeders")
        `validate powerSystemResource` (statement, "fuses")
        `validate identifiedObject` (statement, "geographical_regions")
        `validate powerSystemResource` (statement, "ground_disconnectors")
        `validate powerSystemResource` (statement, "grounding_impedances")
        `validate powerSystemResource` (statement, "grounds")
        `validate powerSystemResource` (statement, "jumpers")
        `validate powerSystemResource` (statement, "junctions")
        `validate shuntCompensator` (statement, "linear_shunt_compensators")
        `validate powerSystemResource` (statement, "load_break_switches")
        `validate streetAddress` (statement, "location_street_addresses")
        `validate identifiedObject` (statement, "locations")
        `validate identifiedObject` (statement, "loops")
        `validate powerSystemResource` (statement, "lv_feeders")
        `validate identifiedObject` (statement, "meters")
        `validate nameType` (statement, "name_types")
        `validate identifiedObject` (statement, "no_load_tests")
        `validate identifiedObject` (statement, "open_circuit_tests")
        `validate document` (statement, "operational_restrictions")
        `validate identifiedObject` (statement, "organisations")
        `validate identifiedObject` (statement, "overhead_wire_info")
        `validate identifiedObject` (statement, "pan_demand_response_functions")
        `validate identifiedObject` (statement, "per_length_phase_impedances")
        `validate identifiedObject` (statement, "per_length_sequence_impedances")
        `validate powerSystemResource` (statement, "petersen_coils")
        `validate powerSystemResource` (statement, "photo_voltaic_units")
        `validate identifiedObject` (statement, "poles")
        `validate identifiedObject` (statement, "potential_transformer_info")
        `validate powerSystemResource` (statement, "potential_transformers")
        `validate powerSystemResource` (statement, "power_electronics_connection_phases")
        `validate regulatingCondEq` (statement, "power_electronics_connections")
        `validate powerSystemResource` (statement, "power_electronics_wind_units")
        `validate transformerEnd` (statement, "power_transformer_ends")
        `validate identifiedObject` (statement, "power_transformer_info")
        `validate powerSystemResource` (statement, "power_transformers")
        `validate document` (statement, "pricing_structures")
        `validate identifiedObject` (statement, "protection_relay_schemes")
        `validate powerSystemResource` (statement, "protection_relay_systems")
        `validate tapChanger` (statement, "ratio_tap_changers")
        `validate identifiedObject` (statement, "reactive_capability_curves")
        `validate powerSystemResource` (statement, "reclosers")
        `validate identifiedObject` (statement, "relay_info")
        `validate identifiedObject` (statement, "remote_controls")
        `validate identifiedObject` (statement, "remote_sources")
        `validate regulatingCondEq` (statement, "rotating_machines")
        `validate powerSystemResource` (statement, "series_compensators")
        `validate identifiedObject` (statement, "short_circuit_tests")
        `validate identifiedObject` (statement, "shunt_compensator_info")
        `validate powerSystemResource` (statement, "sites")
        `validate regulatingCondEq` (statement, "static_var_compensators")
        `validate identifiedObject` (statement, "streetlights")
        `validate identifiedObject` (statement, "sub_geographical_regions")
        `validate powerSystemResource` (statement, "substations")
        `validate identifiedObject` (statement, "switch_info")
        `validate synchronousMachine` (statement, "synchronous_machines")
        `validate powerSystemResource` (statement, "tap_changer_controls")
        `validate document` (statement, "tariffs")
        `validate identifiedObject` (statement, "terminals")
        `validate identifiedObject` (statement, "transformer_end_info")
        `validate identifiedObject` (statement, "transformer_star_impedances")
        `validate identifiedObject` (statement, "transformer_tank_info")
        `validate usagePoint` (statement, "usage_points")
        `validate powerSystemResource` (statement, "voltage_relays")
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM ac_line_segments;",
        )

    private fun `populate usagePoint`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, is_virtual) 
           VALUES ('id1', 'name', 'desc', 1, true)
        """.trimIndent()
    )

    private fun `populate nameType` (table: String) = arrayOf (
        """
            INSERT INTO $table (name, description)
            VALUES ('probs a name', 'something about names')
        """.trimIndent()
    )

    private fun `populate analog`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, positive_flow_in) 
           VALUES ('id1', 'name', 'desc', 1, true)
        """.trimIndent()
    )

    private fun `populate energyConsumer`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, grounded) 
           VALUES ('id1', 'name', 'desc', 1, 0, false)
        """.trimIndent(),
    )

    private fun `populate synchronousMachine`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, control_enabled, earthing)
           VALUES ('id1', 'name', 'desc', 1, 0, true, false)
        """.trimIndent(),
    )


    private fun `populate transformerEnd`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, grounded) 
           VALUES ('id1', 'name', 'desc', 1, false)
        """.trimIndent()
    )

    private fun `populate document`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, title, author_name, type, status, comment) 
           VALUES ('id1', 'name', 'desc', 1, 'title', 'probably_someone', 'definitely a type', 'some status', 'no comment')
        """.trimIndent()
    )

    private fun `populate shuntCompensator`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, control_enabled, grounded) 
           VALUES ('id1', 'name', 'desc', 1, 0, true, false)
        """.trimIndent(),
    )

    private fun `populate regulatingCondEq`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, control_enabled) 
           VALUES ('id1', 'name', 'desc', 1, 0, true)
        """.trimIndent(),
    )

    private fun `populate tapChanger`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, control_enabled) 
           VALUES ('id1', 'name', 'desc', 1, 0, true)
        """.trimIndent(),
    )

    private fun `populate energySource`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls, is_external_grid) 
           VALUES ('id1', 'name', 'desc', 1, 0, false)
        """.trimIndent(),
    )

    private fun `populate powerSystemResource`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, num_controls) 
           VALUES ('id1', 'name', 'desc', 1, 0)
        """.trimIndent(),
    )

    private fun `populate identifiedObject`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects) 
           VALUES ('id1', 'name', 'desc', 1)
        """.trimIndent()
    )

    private fun `populate streetAddress`(table: String) = arrayOf(
        // TODO
        """
           INSERT INTO $table (postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address)
           VALUES ('2505', 'po_box', 'not_a_building', 'floor', 'name', 'number', 'suite', 'type_', 'disp_addy')
        """.trimIndent(),
    )

    private fun `validate usagePoint`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, is_virtual FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getString("num_diagram_objects"), equalTo(1))
                assertThat(rs.getBoolean("is_virtual"), equalTo(true))
            }
        )
    }

    private fun `validate nameType`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT name, description FROM $table",
            { rs ->
                assertThat(rs.getString("name"), equalTo("probs a name"))
                assertThat(rs.getString("description"), equalTo("something about names"))
            }
        )
    }

    private fun `validate analog`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, positive_flow_in FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getString("num_diagram_objects"), equalTo(1))
                assertThat(rs.getBoolean("positive_flow_in"), equalTo(true))
            }
        )
    }

    private fun `validate energyConsumer`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, grounded  FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("grounded"), equalTo(false))
            }
        )
    }

    private fun `validate synchronousMachine`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, control_enabled, earthing  FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("control_enabled"), equalTo(true))
                assertThat(rs.getBoolean("earthing"), equalTo(false))
            }
        )
    }

    private fun `validate transformerEnd`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects grounded FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getString("num_diagram_objects"), equalTo(1))
                assertThat(rs.getBoolean("grounded"), equalTo(false))
            }
        )
    }

    private fun `validate document`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, title, author_name, type, status, comment FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getString("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("title"), equalTo("title"))
                assertThat(rs.getString("author_name"), equalTo("probably_someone"))
                assertThat(rs.getString("type"), equalTo("definitely a type"))
                assertThat(rs.getString("status"), equalTo("some status"))
                assertThat(rs.getString("comment"), equalTo("no comment"))
            }
        )
    }

    private fun `validate shuntCompensator`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, control_enabled, grounded FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("control_enabled"), equalTo(true))
                assertThat(rs.getBoolean("grounded"), equalTo(false))
            }
        )
    }

    private fun `validate regulatingCondEq`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, control_enabled FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("control_enabled"), equalTo(true))
            }
        )
    }

    private fun `validate tapChanger`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, control_enabled FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("control_enabled"), equalTo(true))
            }
        )
    }

    private fun `validate energySource`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls, is_external_grid FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("is_external_grid"), equalTo(false))
            }
        )
    }

    private fun `validate powerSystemResource`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, num_controls FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getInt("num_controls"), equalTo(0))
            }
        )
    }

    private fun `validate identifiedObject`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getString("num_diagram_objects"), equalTo(1))
            }
        )
    }

    private fun `validate streetAddress`(statement: Statement, table: String) {
        validateRows(
            statement,
            "SELECT postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address FROM location_street_addresses",
            { rs ->
                // TODO
                assertThat(rs.getString("mrid"), equalTo("id1"))
            }
        )
    }
    
}

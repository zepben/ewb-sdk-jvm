/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.getNullableBoolean
import com.zepben.ewb.database.getNullableDouble
import com.zepben.ewb.database.getNullableInt
import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet64NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 64) {

    // There are no modified tables, so nothing to set up.
    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid) VALUES ('1', '', '', 1, 'loc1', 10, 'terminal_mrid_1');",
        "INSERT INTO linear_shunt_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, phase_connection) VALUES ('1', '', '', 1, 'loc1', 10, 'A');",
        "INSERT INTO overhead_wire_info(mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('2', '', '', 1, 1.0, 'copperCadmium');",
        "INSERT INTO cable_info(mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('2', '', '', 1, 1.0, 'copperCadmium');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO hv_customers (mrid, name, description, num_diagram_objects, location_mrid, num_controls) VALUES ('1', '', '', 1, 'loc1', 10);",
        "INSERT INTO lv_substations (mrid, name, description, num_diagram_objects, location_mrid, num_controls) VALUES ('1', '', '', 1, 'loc1', 10);",
        "INSERT INTO ac_line_segment_phases(mrid, name, description, num_diagram_objects, location_mrid, num_controls, phase, sequence_number, wire_info_mrid, ac_line_segment_mrid) VALUES ('1', '', '', 1, 'loc1', 10, 'A', 1, 'wi1', 'acls1');",
        "INSERT INTO overhead_wire_info(mrid, name, description, num_diagram_objects, rated_current, material, size_description, strand_count, core_strand_count, insulated, insulation_material, insulation_thickness) VALUES ('1', '', '', 1, 1.0, 'copperCadmium', '2cm', '3', '4', true, 'bilc', 2.0);",
        "INSERT INTO cable_info(mrid, name, description, num_diagram_objects, rated_current, material, size_description, strand_count, core_strand_count, insulated, insulation_material, insulation_thickness) VALUES ('1', '', '', 1, 1.0, 'copperCadmium', '2cm', '3', '4', true, 'bilc', 2.0);",
        "INSERT INTO linear_shunt_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, phase_connection, grounding_terminal_mrid) VALUES ('2', '', '', 2, 'loc2', 20, 'B', 'term1');",
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid, normal_energizing_lv_substation_mrid) VALUES ('2', '', '', 2, 'loc2', 20, 'terminal_mrid_2', 'lv_substation_mrid_2');",
    )

    override fun validateChanges(statement: Statement) {
        ensureHvCustomers(statement)
        ensureLvSubstations(statement)
        ensureAcLineSegmentPhases(statement)
        ensureWireInfos(statement)
        ensureShuntCompensators(statement)
        ensureLvFeeders(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM lv_feeders;",
            "DELETE FROM hv_customers;",
            "DELETE FROM lv_substations;",
            "DELETE FROM ac_line_segment_phases;",
            "DELETE FROM overhead_wire_info;",
            "DELETE FROM cable_info;",
            "DELETE FROM linear_shunt_compensators;",
        )

    private fun ensureHvCustomers(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM hv_customers",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getNullableInt("num_controls"), equalTo(10))
            }
        )
        ensureIndexes(statement, "hv_customers_mrid")
        ensureIndexes(statement, "hv_customers_name")
    }

    private fun ensureLvSubstations(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM lv_substations",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getNullableInt("num_controls"), equalTo(10))
            }
        )
        ensureIndexes(statement, "lv_substations_mrid")
        ensureIndexes(statement, "lv_substations_name")
    }

    private fun ensureAcLineSegmentPhases(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM ac_line_segment_phases",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getNullableInt("num_controls"), equalTo(10))
                assertThat(rs.getNullableString("phase"), equalTo("A"))
                assertThat(rs.getNullableInt("sequence_number"), equalTo(1))
                assertThat(rs.getNullableString("wire_info_mrid"), equalTo("wi1"))
                assertThat(rs.getNullableString("ac_line_segment_mrid"), equalTo("acls1"))
            }
        )
        ensureIndexes(statement, "ac_line_segment_phases_mrid")
        ensureIndexes(statement, "ac_line_segment_phases_name")
        ensureIndexes(statement, "ac_line_segment_phases_wire_info_mrid")
        ensureIndexes(statement, "ac_line_segment_phases_ac_line_segment_mrid")
    }

    private fun ensureWireInfos(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM overhead_wire_info",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(1.0))
                assertThat(rs.getNullableString("material"), equalTo("copperCadmium"))
                assertThat(rs.getNullableString("size_description"), nullValue())
                assertThat(rs.getNullableString("strand_count"), nullValue())
                assertThat(rs.getNullableString("core_strand_count"), nullValue())
                assertThat(rs.getNullableBoolean("insulated"), nullValue())
                assertThat(rs.getString("insulation_material"), equalTo("UNKNOWN"))
                assertThat(rs.getNullableDouble("insulation_thickness"), nullValue())
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(1.0))
                assertThat(rs.getString("material"), equalTo("copperCadmium"))
                assertThat(rs.getNullableString("size_description"), equalTo("2cm"))
                assertThat(rs.getNullableString("strand_count"), equalTo("3"))
                assertThat(rs.getNullableString("core_strand_count"), equalTo("4"))
                assertThat(rs.getNullableBoolean("insulated"), equalTo(true))
                assertThat(rs.getString("insulation_material"), equalTo("bilc"))
                assertThat(rs.getNullableDouble("insulation_thickness"), equalTo(2.0))
            }
        )
        validateRows(
            statement, "SELECT * FROM cable_info",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(1.0))
                assertThat(rs.getString("material"), equalTo("copperCadmium"))
                assertThat(rs.getNullableString("size_description"), nullValue())
                assertThat(rs.getNullableString("strand_count"), nullValue())
                assertThat(rs.getNullableString("core_strand_count"), nullValue())
                assertThat(rs.getNullableBoolean("insulated"), nullValue())
                assertThat(rs.getString("insulation_material"), equalTo("UNKNOWN"))
                assertThat(rs.getNullableDouble("insulation_thickness"), nullValue())
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getNullableString("name"), equalTo(""))
                assertThat(rs.getNullableString("description"), equalTo(""))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(1.0))
                assertThat(rs.getString("material"), equalTo("copperCadmium"))
                assertThat(rs.getNullableString("size_description"), equalTo("2cm"))
                assertThat(rs.getNullableString("strand_count"), equalTo("3"))
                assertThat(rs.getNullableString("core_strand_count"), equalTo("4"))
                assertThat(rs.getNullableBoolean("insulated"), equalTo(true))
                assertThat(rs.getString("insulation_material"), equalTo("bilc"))
                assertThat(rs.getNullableDouble("insulation_thickness"), equalTo(2.0))
            }
        )
    }

    private fun ensureShuntCompensators(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM linear_shunt_compensators",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat(rs.getString("phase_connection"), equalTo("A"))
                assertThat(rs.getNullableString("grounding_terminal_mrid"), nullValue())
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("location_mrid"), equalTo("loc2"))
                assertThat(rs.getInt("num_controls"), equalTo(20))
                assertThat(rs.getString("phase_connection"), equalTo("B"))
                assertThat(rs.getString("grounding_terminal_mrid"), equalTo("term1"))
            }
        )
        ensureIndexes(statement, "linear_shunt_compensators_grounding_terminal_mrid")
    }

    private fun ensureLvFeeders(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM lv_feeders",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat(rs.getString("normal_head_terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("normal_energizing_lv_substation_mrid"), nullValue())
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("location_mrid"), equalTo("loc2"))
                assertThat(rs.getInt("num_controls"), equalTo(20))
                assertThat(rs.getString("normal_head_terminal_mrid"), equalTo("terminal_mrid_2"))
                assertThat(rs.getString("normal_energizing_lv_substation_mrid"), equalTo("lv_substation_mrid_2"))
            }
        )
        ensureIndexes(statement, "lv_feeders_normal_energizing_lv_substation_mrid")
        ensureIndexes(statement, "lv_feeders_normal_head_terminal_mrid")
    }
}

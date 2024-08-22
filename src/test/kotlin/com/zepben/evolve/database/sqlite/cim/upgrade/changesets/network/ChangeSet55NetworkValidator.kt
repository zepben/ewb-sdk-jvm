/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets.network

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet55NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 55) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO usage_points (mrid, name, description, num_diagram_objects, location_mrid, is_virtual, connection_category, rated_power, approved_inverter_capacity, phase_code) VALUES ('mrid', 'name', 'desc', 0, 'l_id', true, 'connection_category', 2, 2, 'AN');",
        "INSERT INTO curve_data (curve_mrid, x_value, y1_value, y2_value, y3_value) VALUES ('mrid', 1.0, 1.0, 1.0, 1.0);",
        "INSERT INTO grounding_impedances (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 1, true, true, 'commissioned_date', 'base_voltage_mrid', 2.0, 2.0);",
        "INSERT INTO petersen_coils (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x_ground_nominal) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 1, true, true, 'commissioned_date', 'base_voltage_mrid', 3.0, 3.0);",
        "INSERT INTO reactive_capability_curves (mrid, name, description, num_diagram_objects) VALUES ('mrid', 'name', 'description', 1);",
        "INSERT INTO synchronous_machines (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, rated_power_factor, rated_s, rated_u, p, q, base_q, condenser_p, earthing, earthing_star_point_r, earthing_star_point_x, ikk, max_q, max_u, min_q, min_u, mu, r, r0, r2, sat_direct_subtrans_x, sat_direct_sync_x, sat_direct_trans_x, x0, x2, type, operating_mode) VALUES ('mrid', 'name', 'description', 1, 'location_mrid', 1, true, true, 'commissioned_date', 'base_voltage_mrid', true, 'regulating_control_mrid', 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1, true, 1.0, 1.0, 1.0, 1.0, 1, 1.0, 1, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 'generator', 'generator');",
        "INSERT INTO synchronous_machine_reactive_capability_curves (synchronous_machine_mrid, reactive_capability_curve_mrid) VALUES ('synchronous_machine_mrid', 'reactive_capability_curve_mrid');"
    )

    override fun validateChanges(statement: Statement) {
        ensureNewAdded(statement)
        validateRows(statement, "SELECT * FROM usage_points",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
                assertThat(rs.getString("location_mrid"), equalTo("l_id"))
                assertThat(rs.getBoolean("is_virtual"), equalTo(true))
                assertThat(rs.getString("connection_category"), equalTo("connection_category"))
                assertThat(rs.getInt("rated_power"), equalTo(2))
                assertThat(rs.getInt("approved_inverter_capacity"), equalTo(2))
                assertThat(rs.getString("phase_code"), equalTo("AN"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM curve_data;",
            "DELETE FROM grounding_impedances;",
            "DELETE FROM petersen_coils;",
            "DELETE FROM reactive_capability_curves;",
            "DELETE FROM synchronous_machines;",
            "DELETE FROM usage_points",
            "DELETE FROM synchronous_machine_reactive_capability_curves"
        )

    private fun ensureNewAdded(statement: Statement) {
        ensureTables(
            statement,
            "curve_data",
            "grounding_impedances",
            "petersen_coils",
            "reactive_capability_curves",
            "synchronous_machines",
            "synchronous_machine_reactive_capability_curves"
        )

        ensureIndexes(
            statement,
            "curve_data_curve_mrid_x_value",
            "curve_data_curve_mrid",
            "grounding_impedances_mrid",
            "petersen_coils_mrid",
            "reactive_capability_curves_mrid",
            "synchronous_machines_mrid",
            "synchronous_machine_reactive_capability_curves_synchronous_machine_mrid",
            "synchronous_machine_reactive_capability_curves_reactive_capability_curve_mrid"
        )
    }
}

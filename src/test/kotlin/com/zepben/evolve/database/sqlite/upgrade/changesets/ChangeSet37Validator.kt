/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet37Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        """
        INSERT INTO linear_shunt_compensators (
            mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, base_voltage_mrid, control_enabled, grounded, nom_u, phase_connection, sections, b0_per_section, b_per_section, g0_per_section, g_per_section
        ) VALUES ( 
            'id', 'name', 'desc', 1, 'loc', 2, true, true, 'bv', true, false, 3, 'phase_con', 4.4, 5.5, 6.6, 7.7, 8.8
        ) 
        """
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        validateRows(statement, "SELECT * FROM linear_shunt_compensators", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("location_mrid"), equalTo("loc"))
            assertThat(rs.getInt("num_controls"), equalTo(2))
            assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
            assertThat(rs.getBoolean("in_service"), equalTo(true))
            assertThat(rs.getString("base_voltage_mrid"), equalTo("bv"))
            assertThat(rs.getBoolean("control_enabled"), equalTo(true))
            assertThat(rs.getString("shunt_compensator_info_mrid"), nullValue())
            assertThat(rs.getBoolean("grounded"), equalTo(false))
            assertThat(rs.getInt("nom_u"), equalTo(3))
            assertThat(rs.getString("phase_connection"), equalTo("phase_con"))
            assertThat(rs.getDouble("sections"), equalTo(4.4))
            assertThat(rs.getDouble("b0_per_section"), equalTo(5.5))
            assertThat(rs.getDouble("b_per_section"), equalTo(6.6))
            assertThat(rs.getDouble("g0_per_section"), equalTo(7.7))
            assertThat(rs.getDouble("g_per_section"), equalTo(8.8))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM linear_shunt_compensators"
    )

}

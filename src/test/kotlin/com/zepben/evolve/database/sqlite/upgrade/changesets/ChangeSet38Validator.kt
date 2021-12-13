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
import java.sql.Statement

object ChangeSet38Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        /************** insert into base_voltages ************/
        """
        INSERT INTO base_voltages (
            mrid, name, description, num_diagram_objects, base_voltage
        ) VALUES ( 
            'id', 'name', 'desc', 1, 2
        ) 
        """,

        /************** insert into remote_controls ************/
        """
        INSERT INTO remote_controls (
            mrid, name, description, num_diagram_objects, power_system_resource_mrid
        ) VALUES ( 
            'id', 'name', 'desc', 1, 'control'
        ) 
        """,

        /************** insert into remote_sources ************/
        """
        INSERT INTO remote_sources (
            mrid, name, description, num_diagram_objects, power_system_resource_mrid
        ) VALUES ( 
            'id', 'name', 'desc', 1, 'measurement'
        ) 
        """,
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        // Ensure index was recreated, as changeset drops it to update columns
        statement.executeQuery("pragma index_info('base_voltages_mrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }
        statement.executeQuery("pragma index_info('base_voltages_name')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('remote_controls_mrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }
        statement.executeQuery("pragma index_info('remote_controls_name')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('remote_sources_mrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }
        statement.executeQuery("pragma index_info('remote_sources_name')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        // Validate entries
        validateRows(statement, "SELECT * FROM base_voltages", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getInt("nominal_voltage"), equalTo(2))
        })

        validateRows(statement, "SELECT * FROM remote_controls", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("control_mrid"), equalTo("control"))
        })

        validateRows(statement, "SELECT * FROM remote_sources", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("measurement_mrid"), equalTo("measurement"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM base_voltages",
        "DELETE FROM remote_controls",
        "DELETE FROM remote_sources",
    )

}

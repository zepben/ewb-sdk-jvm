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

object ChangeSet40Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO battery_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, battery_state) VALUES ('id1', 'test_name', 'test_description', 0, 1, 'pec1', 'Unknown')",
        "INSERT INTO photo_voltaic_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid) VALUES ('id1', 'test_name', 'test_description', 0, 1, 'pec1')",
        "INSERT INTO power_electronics_wind_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid) VALUES ('id1', 'test_name', 'test_description', 0, 1, 'pec1')",
        "INSERT INTO power_electronics_connection_phase (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, phase) VALUES ('id1', 'test_name', 'test_description', 0, 1, 'pec1', 'X')",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO battery_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, battery_state) VALUES ('id2', 'test_name', 'test_description', 0, 1, 'pec1', 'Unknown')",
        "INSERT INTO photo_voltaic_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid) VALUES ('id2', 'test_name', 'test_description', 0, 1, 'pec1')",
        "INSERT INTO power_electronics_wind_unit (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid) VALUES ('id2', 'test_name', 'test_description', 0, 1, 'pec1')",
        "INSERT INTO power_electronics_connection_phase (mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, phase) VALUES ('id2', 'test_name', 'test_description', 0, 1, 'pec1', 'X')",
    )

    override fun validate(statement: Statement) {
        ensureIndexes(statement, "battery_unit_power_electronics_connection_mrid", "photo_voltaic_unit_power_electronics_connection_mrid",
            "power_electronics_wind_unit_power_electronics_connection_mrid", "power_electronics_connection_phase_power_electronics_connection_mrid")

        // Make sure the old and new records both exist:
        validateRows(statement, "SELECT * FROM battery_unit", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id1"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
            assertThat(rs.getString("battery_state"), equalTo("Unknown"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id2"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
            assertThat(rs.getString("battery_state"), equalTo("Unknown"))
        })

        validateRows(statement, "SELECT * FROM photo_voltaic_unit", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id1"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id2"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
        })

        validateRows(statement, "SELECT * FROM power_electronics_wind_unit", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id1"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id2"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
        })

        validateRows(statement, "SELECT * FROM power_electronics_connection_phase", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id1"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
            assertThat(rs.getString("phase"), equalTo("X"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id2"))
            assertThat(rs.getString("name"), equalTo("test_name"))
            assertThat(rs.getString("description"), equalTo("test_description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
            assertThat(rs.getInt("num_controls"), equalTo(1))
            assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pec1"))
            assertThat(rs.getString("phase"), equalTo("X"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM battery_unit",
        "DELETE FROM photo_voltaic_unit",
        "DELETE FROM power_electronics_wind_unit",
        "DELETE FROM power_electronics_connection_phase"
    )
}
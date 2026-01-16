/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet60NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 60) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        // We put the same enum value we are going to replace into each enum column to ensure we only replace the one we are after. We
        // also put an entry with a value that shouldn't be replaced.
        *`populate current_relays`(),
        *`populate distance_relays`(),
        *`populate voltage_relays`(),
        *`populate battery_controls`(),
        *`populate tap_changer_controls`(),
        *`populate power_transformer_end_ratings`(),
        *`populate power_transformer_ends`(),
        *`populate transformer_end_info`(),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        `validate current_relays`(statement)
        `validate distance_relays`(statement)
        `validate voltage_relays`(statement)
        `validate battery_controls`(statement)
        `validate tap_changer_controls`(statement)
        `validate power_transformer_end_ratings`(statement)
        `validate power_transformer_ends`(statement)
        `validate transformer_end_info`(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM current_relays;",
            "DELETE FROM distance_relays;",
            "DELETE FROM voltage_relays;",
            "DELETE FROM battery_controls;",
            "DELETE FROM tap_changer_controls;",
            "DELETE FROM power_transformer_end_ratings;",
            "DELETE FROM power_transformer_ends;",
            "DELETE FROM transformer_end_info;",
        )

    private fun `populate current_relays`() = arrayOf(
        """
           INSERT INTO current_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id1', 'name', 'desc', 0, 0, 'protection_kind', 'power_direction')
        """.trimIndent(),
        """
           INSERT INTO current_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id2', 'name', 'desc', 0, 0, 'UNKNOWN_DIRECTION', 'UNKNOWN_DIRECTION')
        """.trimIndent(),
    )

    private fun `populate distance_relays`() = arrayOf(
        """
           INSERT INTO distance_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id1', 'name', 'desc', 0, 0, 'protection_kind', 'power_direction')
        """.trimIndent(),
        """
           INSERT INTO distance_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id2', 'name', 'desc', 0, 0, 'UNKNOWN_DIRECTION', 'UNKNOWN_DIRECTION')
        """.trimIndent(),
    )

    private fun `populate voltage_relays`() = arrayOf(
        """
           INSERT INTO voltage_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id1', 'name', 'desc', 0, 0, 'protection_kind', 'power_direction')
        """.trimIndent(),
        """
           INSERT INTO voltage_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) 
           VALUES ('id2', 'name', 'desc', 0, 0, 'UNKNOWN_DIRECTION', 'UNKNOWN_DIRECTION')
        """.trimIndent(),
    )

    private fun `populate battery_controls`() = arrayOf(
        """
            INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, num_controls, mode, monitored_phase, control_mode)
            VALUES ('id1', 'name', 'desc', 0, 0, 'mode', 'monitored_phase', 'control_mode');
        """.trimIndent(),
        """
            INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, num_controls, mode, monitored_phase, control_mode)
            VALUES ('id2', 'name', 'desc', 0, 0, 'UNKNOWN_CONTROL_MODE', 'UNKNOWN_CONTROL_MODE', 'UNKNOWN_CONTROL_MODE');
        """.trimIndent(),
    )

    private fun `populate tap_changer_controls`() = arrayOf(
        """
            INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, num_controls, mode, monitored_phase)
            VALUES ('id1', 'name', 'desc', 0, 0, 'mode', 'monitored_phase');
        """.trimIndent(),
        """
            INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, num_controls, mode, monitored_phase)
            VALUES ('id2', 'name', 'desc', 0, 0, 'UNKNOWN_CONTROL_MODE', 'UNKNOWN_CONTROL_MODE');
        """.trimIndent(),
    )

    private fun `populate power_transformer_end_ratings`() = arrayOf(
        """
            INSERT INTO power_transformer_end_ratings (power_transformer_end_mrid, cooling_type, rated_s)
            VALUES ('id1', 'cooling_type', 0);
        """.trimIndent(),
        """
            INSERT INTO power_transformer_end_ratings (power_transformer_end_mrid, cooling_type, rated_s)
            VALUES ('id2', 'UNKNOWN_COOLING_TYPE', 0);
        """.trimIndent(),
    )

    private fun `populate power_transformer_ends`() = arrayOf(
        """
            INSERT INTO power_transformer_ends (mrid, name, description, num_diagram_objects, end_number, grounded, connection_kind)
            VALUES ('id1', 'name', 'desc', 0, 0, true, 'connection_kind');
        """.trimIndent(),
        """
            INSERT INTO power_transformer_ends (mrid, name, description, num_diagram_objects, end_number, grounded, connection_kind)
            VALUES ('id2', 'name', 'desc', 0, 0, true, 'UNKNOWN_WINDING');
        """.trimIndent(),
    )

    private fun `populate transformer_end_info`() = arrayOf(
        """
            INSERT INTO transformer_end_info (mrid, name, description, num_diagram_objects, connection_kind, end_number)
            VALUES ('id1', 'name', 'desc', 0, 'connection_kind', 0);
        """.trimIndent(),
        """
            INSERT INTO transformer_end_info (mrid, name, description, num_diagram_objects, connection_kind, end_number)
            VALUES ('id2', 'name', 'desc', 0, 'UNKNOWN_WINDING', 0);
        """.trimIndent(),
    )

    private fun `validate current_relays`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM current_relays",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("protection_kind"), equalTo("UNKNOWN_DIRECTION"))
                assertThat(rs.getString("power_direction"), equalTo("UNKNOWN"))
            }
        )
    }

    private fun `validate distance_relays`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM distance_relays",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("protection_kind"), equalTo("UNKNOWN_DIRECTION"))
                assertThat(rs.getString("power_direction"), equalTo("UNKNOWN"))
            }
        )
    }

    private fun `validate voltage_relays`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM voltage_relays",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("protection_kind"), equalTo("UNKNOWN_DIRECTION"))
                assertThat(rs.getString("power_direction"), equalTo("UNKNOWN"))
            }
        )
    }

    private fun `validate battery_controls`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM battery_controls",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("mode"), equalTo("mode"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase"))
                assertThat(rs.getString("control_mode"), equalTo("control_mode"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("mode"), equalTo("UNKNOWN"))
                assertThat(rs.getString("monitored_phase"), equalTo("UNKNOWN_CONTROL_MODE"))
                assertThat(rs.getString("control_mode"), equalTo("UNKNOWN_CONTROL_MODE"))
            }
        )
    }

    private fun `validate tap_changer_controls`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM tap_changer_controls",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("mode"), equalTo("mode"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("mode"), equalTo("UNKNOWN"))
                assertThat(rs.getString("monitored_phase"), equalTo("UNKNOWN_CONTROL_MODE"))
            }
        )
    }

    private fun `validate power_transformer_end_ratings`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM power_transformer_end_ratings",
            { rs ->
                assertThat(rs.getString("power_transformer_end_mrid"), equalTo("id1"))
                assertThat(rs.getString("cooling_type"), equalTo("cooling_type"))
            },
            { rs ->
                assertThat(rs.getString("power_transformer_end_mrid"), equalTo("id2"))
                assertThat(rs.getString("cooling_type"), equalTo("UNKNOWN"))
            }
        )
    }

    private fun `validate power_transformer_ends`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM power_transformer_ends",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("connection_kind"), equalTo("UNKNOWN"))
            }
        )
    }

    private fun `validate transformer_end_info`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM transformer_end_info",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("connection_kind"), equalTo("UNKNOWN"))
            }
        )
    }

}

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

object ChangeSet58NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 58) {

    // There are no modified tables, so nothing to set up.
    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO clamps (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('1', '', '', 1, 'loc1', 10, true, true, 'comm1', 'bv1', 1.1, 'acls1');",
        "INSERT INTO cuts (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('2', '', '', 2, 'loc2', 20, true, true, 'comm2', 'bv2', 200, 2000, 22, 'si2', 2.2, 'acls2');",
    )

    override fun validateChanges(statement: Statement) {
        ensureClamps(statement)
        ensureCuts(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM clamps;",
            "DELETE FROM cuts;",
        )

    private fun ensureClamps(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM clamps",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat("Should be normally in service", rs.getBoolean("normally_in_service"))
                assertThat("Should be in service", rs.getBoolean("in_service"))
                assertThat(rs.getString("commissioned_date"), equalTo("comm1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getDouble("length_from_terminal_1"), equalTo(1.1))
                assertThat(rs.getString("ac_line_segment_mrid"), equalTo("acls1"))
            }
        )
        ensureIndexes(statement, "clamps_mrid")
        ensureIndexes(statement, "clamps_ac_line_segment_mrid")
    }

    private fun ensureCuts(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM cuts",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("location_mrid"), equalTo("loc2"))
                assertThat(rs.getInt("num_controls"), equalTo(20))
                assertThat("Should be normally in service", rs.getBoolean("normally_in_service"))
                assertThat("Should be in service", rs.getBoolean("in_service"))
                assertThat(rs.getString("commissioned_date"), equalTo("comm2"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv2"))
                assertThat("Should be normal open", rs.getBoolean("normal_open"))
                assertThat("Should be open", rs.getBoolean("open"))
                assertThat(rs.getDouble("rated_current"), equalTo(22.0))
                assertThat(rs.getString("switch_info_mrid"), equalTo("si2"))
                assertThat(rs.getDouble("length_from_terminal_1"), equalTo(2.2))
                assertThat(rs.getString("ac_line_segment_mrid"), equalTo("acls2"))
            }
        )
        ensureIndexes(statement, "cuts_mrid")
        ensureIndexes(statement, "cuts_ac_line_segment_mrid")
    }

}

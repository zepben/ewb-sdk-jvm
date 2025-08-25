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
        *`populate ac_line_segments`(),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        // TODO: every table that is a power system resource needs a validate power system resources call
        `validate power system resources`(statement, "ac_line_segments")
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM ac_line_segments;",
        )

    private fun `populate ac_line_segments`() = arrayOf(
        """
           INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, num_controls) 
           VALUES ('id1', 'name', 'desc', 1, 0)
        """.trimIndent(),
    )

    private fun `populate location_street_addresses`() = arrayOf(
        // TODO
        """
           INSERT INTO location_street_addresses () 
           VALUES ()
        """.trimIndent(),
    )
    private fun `validate power system resources`(statement: Statement, table: String) {
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

    private fun `validate street addresses`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address FROM location_street_addresses",
            { rs ->
                // TODO
                assertThat(rs.getString("mrid"), equalTo("id1"))
            }
        )
    }

}

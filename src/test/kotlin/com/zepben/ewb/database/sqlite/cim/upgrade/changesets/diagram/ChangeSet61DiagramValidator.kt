/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.diagram

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

// TODO: This needs all the data model changes verified
object ChangeSet61DiagramValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        *`populate identifiedObjectRotation` ("diagram_objects"),
        *`populate identifiedObject` ("diagrams"),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        `validate identifiedObject` (statement, "diagram_objects")
        `validate identifiedObject` (statement, "diagrams")
    }

    override fun tearDownStatements(): List<String> =
        listOf()

    private fun `populate identifiedObjectRotation`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, rotation) 
           VALUES ('id1', 'name', 'desc', 1, 'fully')
        """.trimIndent()
    )

    private fun `populate identifiedObject`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, diagram_style, orientation_kind) 
           VALUES ('id1', 'name', 'desc', 1, 'stylish', 'first year')
        """.trimIndent()
    )

    private fun `validate identifiedObject`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            }
        )
    }

}

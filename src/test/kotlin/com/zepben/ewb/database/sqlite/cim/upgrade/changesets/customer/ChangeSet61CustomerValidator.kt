/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.customer

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

// TODO: This needs all the data model changes verified
object ChangeSet61CustomerValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        *`populate document` ("customer_agreements"),
        *`populate identifiedObject` ("customers"),
        *`populate document` ("pricing_structures"),
        *`populate document` ("tariffs"),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        `validate document` (statement, "customer_agreements")
        `validate identifiedObject` (statement, "customers")
        `validate document` (statement, "pricing_structures")
        `validate document` (statement, "tariffs")
    }

    override fun tearDownStatements(): List<String> =
        listOf()

    private fun `populate document`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, title, author_name, type, status, comment) 
           VALUES ('id1', 'name', 'desc', 1, 'title', 'probably_someone', 'definitely a type', 'some status', 'no comment')
        """.trimIndent()
    )

    private fun `populate identifiedObject`(table: String) = arrayOf(
        """
           INSERT INTO $table (mrid, name, description, num_diagram_objects, kind) 
           VALUES ('id1', 'name', 'desc', 1, 'nah, pretty mean')
        """.trimIndent()
    )

    private fun `validate document`(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT mrid, name, description, num_diagram_objects, title, author_name, type, status, comment FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("title"), equalTo("title"))
                assertThat(rs.getString("author_name"), equalTo("probably_someone"))
                assertThat(rs.getString("type"), equalTo("definitely a type"))
                assertThat(rs.getString("status"), equalTo("some status"))
                assertThat(rs.getString("comment"), equalTo("no comment"))
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
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            }
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.customer

import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet64CustomerValidator : ChangeSetValidator(DatabaseType.CUSTOMER, 63) {

    override fun setUpStatements(): List<String> = listOf(
        *`set up customer_agreements`(),
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = listOf(
        *`populate customer_agreements`(),
    )

    override fun validateChanges(statement: Statement) {
        `validate customer_agreements`(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM customer_agreements;",
        )

    private fun `set up customer_agreements`() = arrayOf(
        """
           INSERT INTO customer_agreements (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, customer_mrid)
           VALUES ('mrid_1', 'name_1', 'description_1', 1, 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1', 'customer_mrid_1');
        """.trimIndent()
    )

    private fun `populate customer_agreements`() = arrayOf(
        """
           INSERT INTO customer_agreements (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, validity_interval_start, validity_interval_end, customer_mrid)
           VALUES ('mrid_2', 'name_2', 'description_2', 2, 'title_2', 'created_date_time_2', 'author_name_2', 'type_2', 'status_2', 'comment_2', 'validity_interval_start_2', 'validity_interval_end_2', 'customer_mrid_2');
        """.trimIndent(),
    )

    private fun `validate customer_agreements`(statement: Statement) {
        validateRows(
            statement,
            "SELECT mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, validity_interval_start, validity_interval_end, customer_mrid FROM customer_agreements;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("title"), equalTo("title_1"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("status"), equalTo("status_1"))
                assertThat(rs.getString("comment"), equalTo("comment_1"))
                assertThat(rs.getNullableString("validity_interval_start"), nullValue())
                assertThat(rs.getNullableString("validity_interval_end"), nullValue())
                assertThat(rs.getString("customer_mrid"), equalTo("customer_mrid_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("name_2"))
                assertThat(rs.getString("description"), equalTo("description_2"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("title"), equalTo("title_2"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_2"))
                assertThat(rs.getString("author_name"), equalTo("author_name_2"))
                assertThat(rs.getString("type"), equalTo("type_2"))
                assertThat(rs.getString("status"), equalTo("status_2"))
                assertThat(rs.getString("comment"), equalTo("comment_2"))
                assertThat(rs.getString("validity_interval_start"), equalTo("validity_interval_start_2"))
                assertThat(rs.getString("validity_interval_end"), equalTo("validity_interval_end_2"))
                assertThat(rs.getString("customer_mrid"), equalTo("customer_mrid_2"))
            }
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.customer

import com.zepben.ewb.database.getNullableInt
import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet64CustomerValidator : ChangeSetValidator(DatabaseType.CUSTOMER, 64) {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO pricing_structures (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_1', 'name_1', 'description_1', 1, 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1');",
    )

    // There are no table changes, so no need to populate anything.
    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO pricing_structures (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, code) VALUES ('mrid_2', 'name_2', 'description_2', 2, 'title_2', 'created_date_time_2', 'author_name_2', 'type_2', 'status_2', 'comment_2', 'code');",
    )

    override fun validateChanges(statement: Statement) {
        ensurePricingStructures(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM pricing_structures;",
        )


    private fun ensurePricingStructures(statement: Statement) {
        validateRows(
            statement,
            "SELECT * from pricing_structures;",
            { rs ->
                assertThat(rs.getNullableString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getNullableString("name"), equalTo("name_1"))
                assertThat(rs.getNullableString("description"), equalTo("description_1"))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getNullableString("title"), equalTo("title_1"))
                assertThat(rs.getNullableString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getNullableString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getNullableString("type"), equalTo("type_1"))
                assertThat(rs.getNullableString("status"), equalTo("status_1"))
                assertThat(rs.getNullableString("comment"), equalTo("comment_1"))
                assertThat(rs.getNullableString("code"), nullValue())
            },
            { rs ->
                assertThat(rs.getNullableString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), equalTo("name_2"))
                assertThat(rs.getNullableString("description"), equalTo("description_2"))
                assertThat(rs.getNullableInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getNullableString("title"), equalTo("title_2"))
                assertThat(rs.getNullableString("created_date_time"), equalTo("created_date_time_2"))
                assertThat(rs.getNullableString("author_name"), equalTo("author_name_2"))
                assertThat(rs.getNullableString("type"), equalTo("type_2"))
                assertThat(rs.getNullableString("status"), equalTo("status_2"))
                assertThat(rs.getNullableString("comment"), equalTo("comment_2"))
                assertThat(rs.getNullableString("code"), equalTo("code"))
            },
        )
    }

}

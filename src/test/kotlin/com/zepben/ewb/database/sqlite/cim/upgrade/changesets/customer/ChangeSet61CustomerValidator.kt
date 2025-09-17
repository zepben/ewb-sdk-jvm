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

object ChangeSet61CustomerValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: We are utilising the SQLite feature of being able to put any type of data into a column by putting string into all fields. This stops us
    //       having to deal with the complexity of column types in the validations, but still does the nullability checking.
    //

    //
    // NOTE: Some columns were incorrectly left as `NOT NULL` in the v61 changeset, so the tests here will validate against thet. The fixes for these
    //       columns have been added in change set 62 as we already had databases in the wild on v61 before this was detected.
    //

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO customer_agreements (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, customer_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1', 'customer_mrid_1');",
        "INSERT INTO customers (mrid, name, description, num_diagram_objects, organisation_mrid, kind, num_end_devices, special_need) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'organisation_mrid_1', 'kind_1', 'num_end_devices_1', 'special_need_1');",
        "INSERT INTO organisations (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO pricing_structures (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1');",
        "INSERT INTO tariffs (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO customer_agreements (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment, customer_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'should_be_nullable', null);",
        "INSERT INTO customers (mrid, name, description, num_diagram_objects, organisation_mrid, kind, num_end_devices, special_need) VALUES ('mrid_2', null, null, null, null, 'kind_2', null, null);",
        "INSERT INTO organisations (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', 'should_be_nullable', 'should_be_nullable', 'should_be_nullable');",
        "INSERT INTO pricing_structures (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'should_be_nullable');",
        "INSERT INTO tariffs (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'should_be_nullable');",
    )

    override fun validateChanges(statement: Statement) {
        `validate customer_agreements`(statement)
        `validate customers`(statement)
        `validate organisations`(statement)
        `validate pricing_structures`(statement)
        `validate tariffs`(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE from customer_agreements;",
            "DELETE from customers;",
            "DELETE from organisations;",
            "DELETE from pricing_structures;",
            "DELETE from tariffs;",
        )

    private fun `validate customer_agreements`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM customer_agreements",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("title"), equalTo("title_1"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("status"), equalTo("status_1"))
                assertThat(rs.getString("comment"), equalTo("comment_1"))
                assertThat(rs.getString("customer_mrid"), equalTo("customer_mrid_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("title"), nullValue())
                assertThat(rs.getNullableString("created_date_time"), nullValue())
                assertThat(rs.getNullableString("author_name"), nullValue())
                assertThat(rs.getNullableString("type"), nullValue())
                assertThat(rs.getNullableString("status"), nullValue())
                assertThat(rs.getString("comment"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("customer_mrid"), nullValue())
            }
        )
    }

    private fun `validate customers`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM customers",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("organisation_mrid"), equalTo("organisation_mrid_1"))
                assertThat(rs.getString("kind"), equalTo("kind_1"))
                assertThat(rs.getString("num_end_devices"), equalTo("num_end_devices_1"))
                assertThat(rs.getString("special_need"), equalTo("special_need_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("organisation_mrid"), nullValue())
                assertThat(rs.getNullableString("kind"), equalTo("kind_2"))
                assertThat(rs.getNullableString("num_end_devices"), nullValue())
                assertThat(rs.getNullableString("special_need"), nullValue())
            }
        )
    }

    private fun `validate organisations`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM organisations",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getString("description"), equalTo("should_be_nullable"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("should_be_nullable"))
            }
        )
    }

    private fun `validate pricing_structures`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM pricing_structures",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("title"), equalTo("title_1"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("status"), equalTo("status_1"))
                assertThat(rs.getString("comment"), equalTo("comment_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("title"), nullValue())
                assertThat(rs.getNullableString("created_date_time"), nullValue())
                assertThat(rs.getNullableString("author_name"), nullValue())
                assertThat(rs.getNullableString("type"), nullValue())
                assertThat(rs.getNullableString("status"), nullValue())
                assertThat(rs.getString("comment"), equalTo("should_be_nullable"))
            }
        )
    }

    private fun `validate tariffs`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM tariffs",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("title"), equalTo("title_1"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("status"), equalTo("status_1"))
                assertThat(rs.getString("comment"), equalTo("comment_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("title"), nullValue())
                assertThat(rs.getNullableString("created_date_time"), nullValue())
                assertThat(rs.getNullableString("author_name"), nullValue())
                assertThat(rs.getNullableString("type"), nullValue())
                assertThat(rs.getNullableString("status"), nullValue())
                assertThat(rs.getString("comment"), equalTo("should_be_nullable"))
            }
        )
    }

}

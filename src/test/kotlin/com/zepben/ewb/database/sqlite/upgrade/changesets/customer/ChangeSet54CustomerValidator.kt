/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.customer

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet54CustomerValidator : ChangeSetValidator(DatabaseType.CUSTOMER, 54) {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO customers (mrid, name, description, num_diagram_objects, organisation_mrid, kind, num_end_devices) VALUES ('c1', 'n1', 'd1', 1, 'o1', 'k1', 10);",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO customers (mrid, name, description, num_diagram_objects, organisation_mrid, kind, num_end_devices, special_need) VALUES ('c2', 'n2', 'd2', 2, 'o2', 'k2', 20, 's2');",
    )

    override fun validateChanges(statement: Statement) {
        // Make sure the data was copied.
        validateRows(
            statement, "SELECT * FROM customers",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("c1"))
                assertThat(rs.getString("name"), equalTo("n1"))
                assertThat(rs.getString("description"), equalTo("d1"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("organisation_mrid"), equalTo("o1"))
                assertThat(rs.getString("kind"), equalTo("k1"))
                assertThat(rs.getInt("num_end_devices"), equalTo(10))
                assertThat(rs.getString("special_need"), nullValue())
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("c2"))
                assertThat(rs.getString("name"), equalTo("n2"))
                assertThat(rs.getString("description"), equalTo("d2"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("organisation_mrid"), equalTo("o2"))
                assertThat(rs.getString("kind"), equalTo("k2"))
                assertThat(rs.getInt("num_end_devices"), equalTo(20))
                assertThat(rs.getString("special_need"), equalTo("s2"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM customers"
        )

}

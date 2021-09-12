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
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet35Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        """
        INSERT INTO usage_points (
            mrid, name, description, num_diagram_objects, location_mrid
        ) VALUES ( 
            'id', 'name', 'desc', 1, 'loc'
        ) 
        """
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        validateRows(statement, "SELECT * FROM usage_points", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("location_mrid"), equalTo("loc"))
            assertThat(rs.getBoolean("is_virtual"), equalTo(false))
            assertThat(rs.getString("connection_category"), nullValue())
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM usage_points"
    )

}

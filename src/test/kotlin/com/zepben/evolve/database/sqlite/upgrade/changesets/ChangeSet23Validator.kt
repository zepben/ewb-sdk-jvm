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
import java.sql.Statement

object ChangeSet23Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_diagram_objects, location_mrid, num_controls, num_measurements, normally_in_service, in_service, base_voltage_mrid, vector_group
        ) VALUES (
            'id', 'name', 'desc', 1, 'loc', 2, 3, true, true, 'bv', 'Dyn11'
        )
        """
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        validateRows(statement, "select * from power_transformers",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc"))
                assertThat(rs.getInt("num_controls"), equalTo(2))
                assertThat(rs.getInt("num_measurements"), equalTo(3))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv"))
                assertThat(rs.getString("vector_group"), equalTo("Dyn11"))
                assertThat(rs.getDouble("transformer_utilisation"), equalTo(0.0))
            }
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM power_transformers"
    )

}

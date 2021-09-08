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

object ChangeSet21Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO measurements VALUES('meas1','meas1','meas1',1,'psr1')",
        "INSERT INTO measurements VALUES('meas2','meas2','meas2',2,'psr2')",
        "INSERT INTO measurements VALUES('meas3','meas3','meas3',3,'psr3')",
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        // Ensure index was recreated, as changeset drops it to update numbers
        validateRows(statement, "select name as n, description as d, num_diagram_objects as ndo, power_system_resource_mrid as psrid from analogs",
            { rs ->
                assertThat(rs.getString("n"), equalTo("meas1"))
                assertThat(rs.getString("d"), equalTo("meas1"))
                assertThat(rs.getInt("ndo"), equalTo(1))
                assertThat(rs.getString("psrid"), equalTo("psr1"))
            }, { rs ->
                assertThat(rs.getString("n"), equalTo("meas2"))
                assertThat(rs.getString("d"), equalTo("meas2"))
                assertThat(rs.getInt("ndo"), equalTo(2))
                assertThat(rs.getString("psrid"), equalTo("psr2"))
            }, { rs ->
                assertThat(rs.getString("n"), equalTo("meas3"))
                assertThat(rs.getString("d"), equalTo("meas3"))
                assertThat(rs.getInt("ndo"), equalTo(3))
                assertThat(rs.getString("psrid"), equalTo("psr3"))
            }
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM analogs"
    )

}

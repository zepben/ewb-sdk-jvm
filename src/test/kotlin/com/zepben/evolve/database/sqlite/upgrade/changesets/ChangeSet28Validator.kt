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

object ChangeSet28Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO battery_unit (mrid, name, description, num_diagram_objects, num_controls, max_p, min_p, battery_state, rated_e, stored_e) VALUES ('abc', 'test_name', 'test_description', 0, 0, 0, 0, 'CHARGING', 1.5, 2.5)"
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        validateRows(statement, "SELECT rated_e, stored_e FROM battery_unit WHERE mrid = 'abc'", { rs ->
            assertThat(rs.getLong("rated_e"), equalTo(1500L))
            assertThat(rs.getLong("stored_e"), equalTo(2500L))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM battery_unit"
    )

}

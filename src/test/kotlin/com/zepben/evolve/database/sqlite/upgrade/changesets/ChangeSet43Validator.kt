/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet43Validator : ChangeSetValidator {
    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid) VALUES ('id', 'name', 'desc', 1, 'loc', 2, 'terminal')"
    )

    override fun validate(statement: Statement) {
        ensureIndexes(
            statement,
            "lv_feeders_name",
            "lv_feeders_description",
            "lv_feeders_num_diagram_objects",
            "lv_feeders_location_mrid",
            "lv_feeders_num_controls",
            "lv_feeders_normal_head_mrid"
        )

        validateRows(statement, "SELECT * FROM lv_feeders", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("desc"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("location_mrid"), equalTo("loc"))
            assertThat(rs.getInt("num_controls"), equalTo(2))
            assertThat(rs.getString("normal_head_terminal_mrid"), equalTo("terminal"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM lv_feeders"
    )
}

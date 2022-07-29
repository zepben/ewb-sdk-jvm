/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import java.sql.Statement

object ChangeSet43Validator : ChangeSetValidator {
    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid) VALUES ('id1', '', '', 0, null, 0, null)",
        "INSERT INTO feeder_lv_feeders (feeder_mrid, lv_feeder_mrid) VALUES ('id2', 'id1')"
    )

    override fun validate(statement: Statement) {
        validateRows(statement,"SELECT feeder_mrid FROM lv_feeders JOIN feeder_lv_feeders ON mrid = lv_feeder_mrid", { rs ->
            assertThat(rs.getString("feeder_mrid"), Matchers.equalTo("id2"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM feeder_lv_feeders",
        "DELETE FROM lv_feeders"
    )
}

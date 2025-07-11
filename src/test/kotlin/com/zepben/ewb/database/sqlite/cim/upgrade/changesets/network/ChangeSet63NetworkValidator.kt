/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.network

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet63NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 63) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
    )

    override fun populateStatements(): List<String> = listOf(

        "INSERT INTO directional_current_relays (mrid, name, description, num_diagram_objects, num_controls, protection_kind, power_direction) VALUES ('mrid', 'name', 'description', 0, 0, 'UNKNOWN', 'power_direction');",
    )

    override fun validateChanges(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM directional_current_relays",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("description"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getString("protection_kind"), equalTo("UNKNOWN"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM directional_current_relays;",
        )

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.diagram

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet52DiagramValidator : ChangeSetValidator(DatabaseType.DIAGRAM, 52) {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO diagram_object_points (diagram_object_mrid, sequence_number, x_position, y_position) VALUES ('id1', '2', '3.3', '4.4')"
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO diagram_object_points (diagram_object_mrid, sequence_number, x_position, y_position) VALUES ('id2', 3, 4.4, 5.5)"
    )

    override fun validateChanges(statement: Statement) {
        // Make sure the indexes were recreated.
        ensureIndexes(statement, "diagram_object_points_diagram_object_mrid_sequence_number", "diagram_object_points_diagram_object_mrid")

        // Make sure the data was copied.
        validateRows(
            statement, "SELECT * FROM diagram_object_points",
            { rs ->
                assertThat(rs.getString("diagram_object_mrid"), equalTo("id1"))
                assertThat(rs.getInt("sequence_number"), equalTo(2))
                assertThat(rs.getDouble("x_position"), equalTo(3.3))
                assertThat(rs.getDouble("y_position"), equalTo(4.4))
            },
            { rs ->
                assertThat(rs.getString("diagram_object_mrid"), equalTo("id2"))
                assertThat(rs.getInt("sequence_number"), equalTo(3))
                assertThat(rs.getDouble("x_position"), equalTo(4.4))
                assertThat(rs.getDouble("y_position"), equalTo(5.5))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM diagram_object_points"
        )

}

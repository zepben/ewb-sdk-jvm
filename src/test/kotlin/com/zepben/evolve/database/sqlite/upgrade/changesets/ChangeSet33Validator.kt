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

object ChangeSet33Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        """
        insert into diagram_objects (
            mrid,
            name,
            description,
            num_diagram_objects,
            identified_object_mrid,
            diagram_mrid,
            style,
            rotation
        ) values (
            'mrid1',
            'name1',
            'description1',
            1,
            'identified_object_mrid1',
            'diagram_mrid1',
            'style1',
            2.2
        )
        """
    )

    override fun populateStatements(): List<String> = listOf(
        // Added support for null styles.
        """
        insert into diagram_objects (
            mrid,
            name,
            description,
            num_diagram_objects,
            identified_object_mrid,
            diagram_mrid,
            style,
            rotation
        ) values (
            'mrid2',
            'name2',
            'description2',
            3,
            'identified_object_mrid2',
            'diagram_mrid2',
            null,
            4.4
        )
        """
    )

    override fun validate(statement: Statement) {
        // Make sure the indexes were recreated:
        ensureIndexes(
            statement,
            "diagram_objects_mrid",
            "diagram_objects_name",
            "diagram_objects_identified_object_mrid",
            "diagram_objects_diagram_mrid"
        )

        // Make sure the old and new records both exist:
        validateRows(statement, "SELECT * FROM diagram_objects", { rs ->
            assertThat(rs.getString("mrid"), equalTo("mrid1"))
            assertThat(rs.getString("name"), equalTo("name1"))
            assertThat(rs.getString("description"), equalTo("description1"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("identified_object_mrid"), equalTo("identified_object_mrid1"))
            assertThat(rs.getString("diagram_mrid"), equalTo("diagram_mrid1"))
            assertThat(rs.getString("style"), equalTo("style1"))
            assertThat(rs.getDouble("rotation"), equalTo(2.2))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("mrid2"))
            assertThat(rs.getString("name"), equalTo("name2"))
            assertThat(rs.getString("description"), equalTo("description2"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(3))
            assertThat(rs.getString("identified_object_mrid"), equalTo("identified_object_mrid2"))
            assertThat(rs.getString("diagram_mrid"), equalTo("diagram_mrid2"))
            assertThat(rs.getString("style"), nullValue())
            assertThat(rs.getDouble("rotation"), equalTo(4.4))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM diagram_objects"
    )

}

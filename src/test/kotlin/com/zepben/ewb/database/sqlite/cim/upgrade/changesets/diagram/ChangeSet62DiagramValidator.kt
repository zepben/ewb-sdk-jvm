/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.diagram

import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet62DiagramValidator : ChangeSetValidator(DatabaseType.DIAGRAM, 62) {

    //
    // NOTE: We are utilising the SQLite feature of being able to put any type of data into a column by putting string into all fields. This stops us
    //       having to deal with the complexity of column types in the validations, but still does the nullability checking.
    //

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO diagram_object_points (diagram_object_mrid, sequence_number, x_position, y_position) VALUES ('diagram_object_mrid_1', 'sequence_number_1', null, null);",
        "INSERT INTO diagram_objects (mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'identified_object_mrid_1', 'diagram_mrid_1', 'style_1', 'rotation_1');",
        "INSERT INTO diagrams (mrid, name, description, num_diagram_objects, diagram_style, orientation_kind) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'diagram_style_1', 'orientation_kind_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO diagram_object_points (diagram_object_mrid, sequence_number, x_position, y_position) VALUES ('diagram_object_mrid_2', 'sequence_number_2', 'x_position_2', 'y_position_2');",
        "INSERT INTO diagram_objects (mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation) VALUES ('mrid_2', null, null, null, 'identified_object_mrid_2', 'diagram_mrid_2', null, 'rotation_2');",
        "INSERT INTO diagrams (mrid, name, description, num_diagram_objects, diagram_style, orientation_kind) VALUES ('mrid_2', null, null, null, 'diagram_style_2', 'orientation_kind_2');",
    )

    override fun validateChanges(statement: Statement) {
        `validate diagram_object_points`(statement)
        `validate diagram_objects`(statement)
        `validate diagrams`(statement)

        `validate indexes`(statement)
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM diagram_object_points;",
        "DELETE FROM diagram_objects;",
        "DELETE FROM diagrams;",
    )

    private fun `validate diagram_object_points`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM diagram_object_points",
            { rs ->
                assertThat(rs.getString("diagram_object_mrid"), equalTo("diagram_object_mrid_1"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_1"))
                assertThat(rs.getDouble("x_position"), equalTo(0.0))
                assertThat(rs.getDouble("y_position"), equalTo(0.0))
            },
            { rs ->
                assertThat(rs.getString("diagram_object_mrid"), equalTo("diagram_object_mrid_2"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_2"))
                assertThat(rs.getString("x_position"), equalTo("x_position_2"))
                assertThat(rs.getString("y_position"), equalTo("y_position_2"))
            }
        )
    }

    private fun `validate diagram_objects`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM diagram_objects",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("identified_object_mrid"), equalTo("identified_object_mrid_1"))
                assertThat(rs.getString("diagram_mrid"), equalTo("diagram_mrid_1"))
                assertThat(rs.getString("style"), equalTo("style_1"))
                assertThat(rs.getString("rotation"), equalTo("rotation_1"))
            },
            { rs ->
                assertThat(rs.getNullableString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getString("identified_object_mrid"), equalTo("identified_object_mrid_2"))
                assertThat(rs.getString("diagram_mrid"), equalTo("diagram_mrid_2"))
                assertThat(rs.getNullableString("style"), nullValue())
                assertThat(rs.getString("rotation"), equalTo("rotation_2"))
            }
        )
    }

    private fun `validate diagrams`(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM diagrams",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("diagram_style"), equalTo("diagram_style_1"))
                assertThat(rs.getString("orientation_kind"), equalTo("orientation_kind_1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getString("diagram_style"), equalTo("diagram_style_2"))
                assertThat(rs.getString("orientation_kind"), equalTo("orientation_kind_2"))
            }
        )
    }

    private fun `validate indexes`(statement: Statement) {
        ensureIndexes(
            statement,
            "name_types_name",
            "names_identified_object_mrid_name_type_name_name",
            "names_identified_object_mrid",
            "names_name",
            "names_name_type_name",
            "diagram_object_points_diagram_object_mrid_sequence_number",
            "diagram_object_points_diagram_object_mrid",
            "diagram_objects_mrid",
            "diagram_objects_name",
            "diagram_objects_identified_object_mrid",
            "diagram_objects_diagram_mrid",
            "diagrams_mrid",
            "diagrams_name",
        )
    }

}

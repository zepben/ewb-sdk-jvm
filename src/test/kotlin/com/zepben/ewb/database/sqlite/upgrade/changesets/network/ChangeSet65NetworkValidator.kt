/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet65NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 65) {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO terminals (mrid, name, description, num_diagram_objects, conducting_equipment_mrid, sequence_number, connectivity_node_mrid, phases) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'conducting_equipment_mrid_1', 'sequence_number_1', 'connectivity_node_mrid_1', 'phases_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO terminals (mrid, name, description, num_diagram_objects, conducting_equipment_mrid, sequence_number, connectivity_node_mrid, phases, normal_feeder_direction) VALUES ('mrid_2', 'name_2', 'description_2', 'num_diagram_objects_2', 'conducting_equipment_mrid_2', 'sequence_number_2', 'connectivity_node_mrid_2', 'phases_2', 'UPSTREAM');",
    )

    override fun validateChanges(statement: Statement) {
        `validate terminals`(statement)

    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM terminals;",
    )

    private fun `validate terminals`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM terminals;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("conducting_equipment_mrid"), equalTo("conducting_equipment_mrid_1"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_1"))
                assertThat(rs.getString("connectivity_node_mrid"), equalTo("connectivity_node_mrid_1"))
                assertThat(rs.getString("phases"), equalTo("phases_1"))
                assertThat(rs.getString("normal_feeder_direction"), nullValue())
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("name_2"))
                assertThat(rs.getString("description"), equalTo("description_2"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_2"))
                assertThat(rs.getString("conducting_equipment_mrid"), equalTo("conducting_equipment_mrid_2"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_2"))
                assertThat(rs.getString("connectivity_node_mrid"), equalTo("connectivity_node_mrid_2"))
                assertThat(rs.getString("phases"), equalTo("phases_2"))
                assertThat(rs.getString("normal_feeder_direction"), equalTo("UPSTREAM"))
            }
        )
    }

}

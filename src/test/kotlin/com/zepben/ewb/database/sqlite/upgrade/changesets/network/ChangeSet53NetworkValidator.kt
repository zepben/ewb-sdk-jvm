/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.getNullableDouble
import com.zepben.ewb.database.getNullableInt
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet53NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 53) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
        """
           INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, 
           base_voltage_mrid, length, wire_info_mrid, per_length_sequence_impedance_mrid, commissioned_date) 
           VALUES ('id1', 'name', 'desc', 0, 'l_id',  0, true, true, 'bv1', 10.0, 'wi1', 'plsi1', '2020-01-01')
        """.trimIndent(),
    )

    override fun populateStatements(): List<String> = listOf(
        """
           INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, 
           base_voltage_mrid, length, design_temperature, design_rating, wire_info_mrid, per_length_sequence_impedance_mrid, commissioned_date) 
           VALUES ('id2', 'name', 'desc', 0, 'l_id',  0, true, true, 'bv1', 10.0, 1, 2.2, 'wi1', 'plsi1', '2020-01-01')
        """.trimIndent(),
    )

    override fun validateChanges(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM ac_line_segments",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
                assertThat(rs.getString("location_mrid"), equalTo("l_id"))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getDouble("length"), equalTo(10.0))
                assertThat(rs.getNullableInt("design_temperature"), nullValue())
                assertThat(rs.getNullableDouble("design_rating"), nullValue())
                assertThat(rs.getString("wire_info_mrid"), equalTo("wi1"))
                assertThat(rs.getString("per_length_sequence_impedance_mrid"), equalTo("plsi1"))
                assertThat(rs.getString("commissioned_date"), equalTo("2020-01-01"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("id2"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("desc"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
                assertThat(rs.getString("location_mrid"), equalTo("l_id"))
                assertThat(rs.getInt("num_controls"), equalTo(0))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getDouble("length"), equalTo(10.0))
                assertThat(rs.getNullableInt("design_temperature"), equalTo(1))
                assertThat(rs.getNullableDouble("design_rating"), equalTo(2.2))
                assertThat(rs.getString("wire_info_mrid"), equalTo("wi1"))
                assertThat(rs.getString("per_length_sequence_impedance_mrid"), equalTo("plsi1"))
                assertThat(rs.getString("commissioned_date"), equalTo("2020-01-01"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM ac_line_segments;",
        )

}

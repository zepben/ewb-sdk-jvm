/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.getNullableDouble
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet56NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 56) {

    //
    // NOTE: In the validators we are only checking a subset of common columns including the one that was actually changed.
    //

    private val tables = listOf(
        "breakers",
        "disconnectors",
        "fuses",
        "ground_disconnectors",
        "jumpers",
        "load_break_switches",
        "reclosers",
    )

    override fun setUpStatements(): List<String> = tables.flatMap { table ->
        listOf(
            // Rated current of 11.
            "INSERT INTO $table (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('1', '', '', 1, 'loc1', 10, true, true, 'comm1', 'bv1', 100, 1000, 11, 'si1');",

            // Rated current of null
            "INSERT INTO $table (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('2', '', '', 2, 'loc2', 20, true, true, 'comm2', 'bv2', 200, 2000, null, 'si2');"
        )
    }

    override fun populateStatements(): List<String> = tables.map { table ->
        // Rated current of 33.3.
        "INSERT INTO $table (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('3', '', '', 3, 'loc3', 30, true, true, 'comm3', 'bv3', 300, 3000, 33.3, 'si3');"
    }

    override fun validateChanges(statement: Statement) {
        tables.forEach { ensureModifiedRatedCurrent(statement, it) }
    }

    override fun tearDownStatements(): List<String> =
        tables.map { "DELETE FROM $it;" }

    private fun ensureModifiedRatedCurrent(statement: Statement, table: String) {
        validateRows(
            statement, "SELECT * FROM $table",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("1"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
                assertThat(rs.getString("location_mrid"), equalTo("loc1"))
                assertThat(rs.getInt("num_controls"), equalTo(10))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("commissioned_date"), equalTo("comm1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv1"))
                assertThat(rs.getInt("normal_open"), equalTo(100))
                assertThat(rs.getInt("open"), equalTo(1000))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(11.0))
                assertThat(rs.getString("switch_info_mrid"), equalTo("si1"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("2"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(2))
                assertThat(rs.getString("location_mrid"), equalTo("loc2"))
                assertThat(rs.getInt("num_controls"), equalTo(20))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("commissioned_date"), equalTo("comm2"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv2"))
                assertThat(rs.getInt("normal_open"), equalTo(200))
                assertThat(rs.getInt("open"), equalTo(2000))
                assertThat(rs.getNullableDouble("rated_current"), nullValue())
                assertThat(rs.getString("switch_info_mrid"), equalTo("si2"))
            },
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("3"))
                assertThat(rs.getString("name"), equalTo(""))
                assertThat(rs.getString("description"), equalTo(""))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(3))
                assertThat(rs.getString("location_mrid"), equalTo("loc3"))
                assertThat(rs.getInt("num_controls"), equalTo(30))
                assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
                assertThat(rs.getBoolean("in_service"), equalTo(true))
                assertThat(rs.getString("commissioned_date"), equalTo("comm3"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("bv3"))
                assertThat(rs.getInt("normal_open"), equalTo(300))
                assertThat(rs.getInt("open"), equalTo(3000))
                assertThat(rs.getNullableDouble("rated_current"), equalTo(33.3))
                assertThat(rs.getString("switch_info_mrid"), equalTo("si3"))
            }
        )
    }

}

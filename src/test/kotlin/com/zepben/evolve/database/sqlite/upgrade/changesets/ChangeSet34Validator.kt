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

object ChangeSet34Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        """
        insert into equivalent_branches (
            mrid,
            name,
            description,
            num_diagram_objects,
            location_mrid,
            num_controls,
            normally_in_service,
            in_service,
            base_voltage_mrid,
            negative_r12,
            negative_r21,
            negative_x12,
            negative_x21,
            positive_r12,
            positive_r21,
            positive_x12,
            positive_x21,
            r,
            r21,
            x,
            x21,
            zero_r12,
            zero_r21,
            zero_x12,
            zero_x21
        ) values (
            'mrid',
            'name',
            'description',
            1,
            'location_mrid',
            2,
            true,
            false,
            'base_voltage_mrid',
            3.3,
            4.4,
            5.5,
            6.6,
            7.7,
            8.8,
            9.9,
            10.01,
            11.11,
            12.21,
            13.31,
            14.41,
            15.51,
            16.61,
            17.71,
            18.81
        )
        """
    )

    override fun validate(statement: Statement) {
        ensureIndexes(statement, "equivalent_branches_mrid", "equivalent_branches_name")

        validateRows(statement, "SELECT * FROM equivalent_branches", { rs ->
            assertThat(rs.getString("mrid"), equalTo("mrid"))
            assertThat(rs.getString("name"), equalTo("name"))
            assertThat(rs.getString("description"), equalTo("description"))
            assertThat(rs.getInt("num_diagram_objects"), equalTo(1))
            assertThat(rs.getString("location_mrid"), equalTo("location_mrid"))
            assertThat(rs.getInt("num_controls"), equalTo(2))
            assertThat(rs.getBoolean("normally_in_service"), equalTo(true))
            assertThat(rs.getBoolean("in_service"), equalTo(false))
            assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid"))
            assertThat(rs.getDouble("negative_r12"), equalTo(3.3))
            assertThat(rs.getDouble("negative_r21"), equalTo(4.4))
            assertThat(rs.getDouble("negative_x12"), equalTo(5.5))
            assertThat(rs.getDouble("negative_x21"), equalTo(6.6))
            assertThat(rs.getDouble("positive_r12"), equalTo(7.7))
            assertThat(rs.getDouble("positive_r21"), equalTo(8.8))
            assertThat(rs.getDouble("positive_x12"), equalTo(9.9))
            assertThat(rs.getDouble("positive_x21"), equalTo(10.01))
            assertThat(rs.getDouble("r"), equalTo(11.11))
            assertThat(rs.getDouble("r21"), equalTo(12.21))
            assertThat(rs.getDouble("x"), equalTo(13.31))
            assertThat(rs.getDouble("x21"), equalTo(14.41))
            assertThat(rs.getDouble("zero_r12"), equalTo(15.51))
            assertThat(rs.getDouble("zero_r21"), equalTo(16.61))
            assertThat(rs.getDouble("zero_x12"), equalTo(17.71))
            assertThat(rs.getDouble("zero_x21"), equalTo(18.81))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM equivalent_branches"
    )

}

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

object ChangeSet61NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: In the validators we are only checking the columns that were actually changed.
    //

    override fun setUpStatements(): List<String> = listOf(
    )

    override fun populateStatements(): List<String> = listOf(
        """INSERT INTO directional_current_relays (
            mrid,
            name,
            description,
            directional_characteristic_angle,
            polarizing_quantity_type,
            relay_element_phase,
            minimum_pickup_current,
            current_limit_1,
            inverse_time_flag,
            time_delay_1
        ) VALUES (
            'mrid',
            'name',
            'description',
            '123.2', 
            'UNKNOWN',
            'ABCN',
            '1.098',
            '10.4',
            'true',
            '123'
        );""".trimIndent(),
    )

    override fun validateChanges(statement: Statement) {
        validateRows(
            statement, "SELECT * FROM directional_current_relays",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid"))
                assertThat(rs.getString("name"), equalTo("name"))
                assertThat(rs.getString("description"), equalTo("description"))
                assertThat(rs.getString("directional_characteristic_angle"), equalTo("123.2"))
                assertThat(rs.getString("polarizing_quantity_type"), equalTo("UNKNOWN"))
                assertThat(rs.getString("relay_element_phase"), equalTo("ABCN"))
                assertThat(rs.getString("minimum_pickup_current"), equalTo("1.098"))
                assertThat(rs.getString("current_limit_1"), equalTo("10.4"))
                assertThat(rs.getString("inverse_time_flag"), equalTo("true"))
                assertThat(rs.getString("time_delay_1"), equalTo("123"))
            }
        )
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            "DELETE FROM directional_current_relays;",
        )

}

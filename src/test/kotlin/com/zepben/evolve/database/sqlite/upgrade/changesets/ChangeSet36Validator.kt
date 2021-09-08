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
import java.sql.Connection


object ChangeSet36Validator : ChangeSetValidator {
    override fun setUp(connection: Connection) { }

    override fun validate(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.executeUpdate("INSERT INTO shunt_compensator_info (mrid, name, description, num_diagram_objects, max_power_loss, rated_current, rated_reactive_power, rated_voltage) VALUES ('id1', 'test_name', 'test_description', 0, 1, 2, 3, 4)")

            statement.executeQuery("SELECT * FROM shunt_compensator_info").use { rs ->
                rs.next()
                assertThat(rs.getString("mrid"), equalTo("id1"))
                assertThat(rs.getString("name"), equalTo("test_name"))
                assertThat(rs.getString("description"), equalTo("test_description"))
                assertThat(rs.getInt("num_diagram_objects"), equalTo(0))
                assertThat(rs.getInt("max_power_loss"), equalTo(1))
                assertThat(rs.getInt("rated_current"), equalTo(2))
                assertThat(rs.getInt("rated_reactive_power"), equalTo(3))
                assertThat(rs.getInt("rated_voltage"), equalTo(4))
            }

        }

    }

    override fun tearDown(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DELETE FROM shunt_compensator_info")
        }
    }

}


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


object ChangeSet30Validator : ChangeSetValidator {
    override fun setUp(connection: Connection) { }

    override fun validate(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.executeUpdate("INSERT INTO names (name, identified_object_mrid, name_type_name) VALUES ('test_name', 'id1', 'nametype')")
            statement.executeUpdate("INSERT INTO name_types (name, description) VALUES ('test_name_type', 'test_description')")

            statement.executeQuery("SELECT * FROM names").use { rs ->
                rs.next()
                assertThat(rs.getString("name"), equalTo("test_name"))
                assertThat(rs.getString("identified_object_mrid"), equalTo("id1"))
                assertThat(rs.getString("name_type_name"), equalTo("nametype"))
            }

            statement.executeQuery("SELECT * FROM name_types").use { rs ->
                rs.next()
                assertThat(rs.getString("name"), equalTo("test_name_type"))
                assertThat(rs.getString("description"), equalTo("test_description"))
            }
        }

    }

    override fun tearDown(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DELETE FROM names")
            statement.execute("DELETE FROM name_types")
        }
    }

}


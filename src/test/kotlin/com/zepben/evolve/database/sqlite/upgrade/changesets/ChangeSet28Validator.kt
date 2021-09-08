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


object ChangeSet28Validator : ChangeSetValidator {
    override fun setUp(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.executeUpdate("""INSERT INTO battery_unit (mrid, name, description, num_diagram_objects, num_controls, max_p, min_p, battery_state, rated_e, stored_e) VALUES ('abc', 'test_name', 'test_description', 0, 0, 0, 0, "CHARGING", 1.5, 2.5)""")
        }
    }

    override fun validate(connection: Connection) {
        // Ensure index was recreated, as changeset drops it to update numbers
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT rated_e, stored_e FROM battery_unit WHERE mrid = 'abc'").use { rs ->
                assertThat(rs.getLong("rated_e"), equalTo(1500L))
                assertThat(rs.getLong("stored_e"), equalTo(2500L))
            }
        }
    }

    override fun tearDown(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DELETE FROM battery_unit")
        }
    }

}


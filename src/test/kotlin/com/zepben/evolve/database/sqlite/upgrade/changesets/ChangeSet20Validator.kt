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


object ChangeSet20Validator : ChangeSetValidator {
    override fun setUp(connection: Connection) {
        connection.createStatement().use { statement ->
            listOf(
                "INSERT INTO terminals VALUES('t1','t1','',0,'',0,'','ABC')",
                "INSERT INTO terminals VALUES('t2','t2','',0,NULL,1,'','ABC')",
                "INSERT INTO terminals VALUES('t3','t3','',0,NULL,2,'','ABC')",
                "INSERT INTO terminals VALUES('t4','t4','',0,NULL,10,'','ABC')",
                "INSERT INTO power_transformer_ends VALUES('e1','e1','e1',0,0,NULL,NULL,'true',0,0,'','D',0,0,0,0,0,0,0,0,0,0,0)",
                "INSERT INTO power_transformer_ends VALUES('e2','e2','e2',0,1,NULL,NULL,'true',0,0,'','D',0,0,0,0,0,0,0,0,0,0,0)",
                "INSERT INTO power_transformer_ends VALUES('e3','e3','e3',0,10,'','','true',0,0,'','D',0,0,0,0,0,0,0,0,0,0,0)"
            ).forEach {
                statement.executeUpdate(it)
            }
        }
    }

    override fun validate(connection: Connection) {
        // Ensure index was recreated, as changeset drops it to update numbers
        connection.createStatement().use { statement ->
            statement.executeQuery("pragma index_info('power_transformer_ends_power_transformer_mrid_end_number')").use { rs ->
                assertThat(rs.next(), equalTo(true))
            }

            statement.executeQuery("select mrid, sequence_number as sn from terminals")!!.let { rs ->
                rs.next()
                assertThat(rs.getInt("sn"), equalTo(1))
                rs.next()
                assertThat(rs.getInt("sn"), equalTo(2))
                rs.next()
                assertThat(rs.getInt("sn"), equalTo(3))
                rs.next()
                assertThat(rs.getInt("sn"), equalTo(11))
            }

            statement.executeQuery("select mrid, end_number as en from power_transformer_ends")!!.let { rs ->
                rs.next()
                assertThat(rs.getInt("en"), equalTo(1))
                rs.next()
                assertThat(rs.getInt("en"), equalTo(2))
                rs.next()
                assertThat(rs.getInt("en"), equalTo(11))
            }
        }
    }

    override fun tearDown(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DELETE FROM terminals")
            statement.execute("DELETE FROM power_transformer_ends")
        }
    }

}


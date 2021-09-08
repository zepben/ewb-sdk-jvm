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
import kotlin.test.fail


object ChangeSet24Validator : ChangeSetValidator {
    override fun setUp(connection: Connection) {
        connection.createStatement().use { statement ->
            listOf(
                "INSERT INTO power_transformers VALUES('power_transformer_0','pt_0','description_0',1,NULL,0,0,0,0,NULL,'UNKNOWN',0)",
                "INSERT INTO power_transformers VALUES('power_transformer_1','pt_1','description_1',1,NULL,0,0,0,0,NULL,'UNKNOWN',1.1999999999999999555)"
            ).forEach {
                statement.executeUpdate(it)
            }
        }
    }

    override fun validate(connection: Connection) {
        // Ensure index was recreated, as changeset drops it to update numbers
        connection.createStatement().use { statement ->
            statement.executeQuery("pragma table_info('power_transformers');").use rs@{ rs ->
                while (rs.next()) {
                    if (rs.getString("name") == "transformer_utilisation") {
                        assertThat(rs.getString("notnull"), equalTo("0"))
                        return@rs
                    }
                }
                fail()
            }
            statement.executeQuery("select transformer_utilisation from power_transformers")!!.let { rs ->
                rs.next()
                assertThat(rs.getDouble("transformer_utilisation"), equalTo(0.0))
                rs.next()
                assertThat(rs.getDouble("transformer_utilisation"), equalTo(1.2))
            }
        }
    }

    override fun tearDown(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DELETE FROM power_transformers")
        }
    }

}


/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql

import com.zepben.evolve.database.sql.extensions.parameters
import com.zepben.evolve.database.sql.extensions.sql
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class PreparedStatementExtensionsKtTest {

    @Test
    internal fun `can print sql statements and bound values`() {
        val sql = "insert into some_table values (?, ?, ?, ?)"
        DriverManager.getConnection("jdbc:sqlite::memory:").use { connection ->
            connection.createStatement().use { it.executeUpdate("create table some_table (f1 integer, f2 string, f3 integer, f4 string)") }
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, 1)
                statement.setString(2, "2")

                MatcherAssert.assertThat(statement.sql(), equalTo(sql))
                MatcherAssert.assertThat(statement.parameters(), equalTo("[1, 2, null, null]"))
            }
        }
    }

}

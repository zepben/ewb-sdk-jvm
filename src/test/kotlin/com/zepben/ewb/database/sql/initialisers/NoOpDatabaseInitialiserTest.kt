/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.initialisers

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager

class NoOpDatabaseInitialiserTest {

    @Test
    internal fun `calls passed in connection factory on connect`() {
        val connection = mockk<Connection>()
        val getConnection = mockk<() -> Connection>().also { every { it() } returns connection }

        val initialiser = NoOpDatabaseInitialiser<BaseDatabaseTables>(getConnection)

        assertThat(initialiser.connect(), sameInstance(connection))

        // Ensure other actions don't make use of the connection, or any parameters, by passing in mockk's that will fail if anything is called on them.
        assertThat("should do nothing", initialiser.beforeConnect(mockk()))
        assertThat("should do nothing", initialiser.afterConnectBeforePrepare(mockk(), mockk(), mockk()))
        assertThat("should do nothing", initialiser.afterWriteBeforeCommit(mockk(), mockk(), mockk()))

    }

    @Test
    internal fun `can use a jdbc connection string`() {
        val connectionString = "jdbc:h2:mem:metrics;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
        DriverManager.getConnection(connectionString).use { connection ->
            connection.createStatement().use { statement ->
                statement.execute("CREATE TABLE my_test (col1 TEXT NULL)")

                NoOpDatabaseInitialiser<BaseDatabaseTables>(connectionString).connect().use { initConn ->
                    initConn.createStatement().use { initStatement ->
                        initStatement.execute("INSERT INTO my_test (col1) VALUES ('some value')")
                    }
                }

                statement.executeQuery("SELECT col1 FROM my_test").use { rs ->
                    assertThat("Should have a record", rs.next())
                    assertThat(rs.getString(1), equalTo("some value"))
                    assertThat("Should only have one record", !rs.next())
                }
            }
        }

    }

}

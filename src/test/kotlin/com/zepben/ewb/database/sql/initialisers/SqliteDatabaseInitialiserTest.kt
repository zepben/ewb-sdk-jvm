/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.initialisers

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.generators.SqliteGenerator
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.io.IOException
import java.nio.file.Path
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

class SqliteDatabaseInitialiserTest {

    private val databaseFile = "database file"
    private val deleteFile = mockk<(Path) -> Unit>()
    private val getConnection = mockk<(String) -> Connection>()
    private val logger = mockk<Logger>(relaxed = true)

    private val statement = mockk<Statement> {
        justRun { queryTimeout = any() }
        every { executeUpdate(any()) } returns 1
        every { execute(any()) } returns true
        justRun { close() }
    }
    private val preparedStatement = mockk<PreparedStatement> {
        justRun { setInt(any(), any()) }
        every { executeUpdate() } returns 1
        justRun { close() }
    }
    private val connection = mockk<Connection> {
        every { createStatement() } returns statement
        every { prepareStatement(any()) } returns preparedStatement
        justRun { commit() }
        justRun { close() }
    }

    private val initialiser = SqliteDatabaseInitialiser<BaseDatabaseTables>(databaseFile, deleteFile, getConnection)

    @Test
    internal fun `deletes any existing file before connecting`() {
        justRun { deleteFile(any()) }

        assertThat("should run", initialiser.beforeConnect(mockk()))

        verifySequence {
            deleteFile(Path.of(databaseFile))
        }
    }

    @Test
    internal fun `detects errors deleting existing file before connecting`() {
        every { deleteFile(any()) } throws IOException("oops, something is wrong.")

        assertThat("shouldn't run", !initialiser.beforeConnect(logger))

        verifySequence {
            deleteFile(Path.of(databaseFile))
            logger.error("Unable to write database, failed to remove previous instance: oops, something is wrong.")
        }
    }

    @Test
    internal fun `connects to the specified database name using jdbc in batch mode`() {
        every { getConnection(any()) } returns connection

        assertThat(initialiser.connect(), sameInstance(connection))

        verifySequence {
            getConnection("jdbc:sqlite:$databaseFile")
            // Called inside `connection.configureBatch()`:
            connection.createStatement()
            statement.executeUpdate("PRAGMA journal_mode = OFF")
            statement.executeUpdate("PRAGMA synchronous = OFF")
            statement.close()
        }
    }

    @Test
    internal fun `creates schema after connecting`() {
        val versionTable = TableVersion(123)
        val table1 = mockk<SqlTable> { every { name } returns "table1" }
        val table2 = mockk<SqlTable> { every { name } returns "table2" }
        val generator = mockk<SqliteGenerator> { every { createTableSql(any()) } answers { firstArg<SqlTable>().name } }
        val databaseTables = mockk<BaseDatabaseTables> {
            every { tables } returns mapOf(TableVersion::class to versionTable)
            every { forEachTable(any()) } answers {
                val action = firstArg<(SqlTable) -> Unit>()
                action(table1)
                action(table2)
            }
            every { sqlGenerator } returns generator
        }

        assertThat("should run", initialiser.afterConnectBeforePrepare(connection, databaseTables, logger))

        verifySequence {
            databaseTables.tables
            logger.info("Creating database schema v123...")
            connection.createStatement()
            statement.queryTimeout = 2
            databaseTables.forEachTable(any())
            databaseTables.sqlGenerator
            generator.createTableSql(table1)
            table1.name
            statement.executeUpdate("table1")
            databaseTables.sqlGenerator
            generator.createTableSql(table2)
            table2.name
            statement.executeUpdate("table2")
            connection.prepareStatement(versionTable.preparedInsertSql)
            preparedStatement.setInt(1, 123)
            preparedStatement.executeUpdate()
            preparedStatement.close()
            connection.commit()
            logger.info("Schema created.")
            statement.close()
        }
    }

    @Test
    internal fun `detects errors creating schema`() {
        val databaseTables = mockk<BaseDatabaseTables> { every { tables } answers { throw SQLException("dang!") } }

        assertThat("shouldn't run", !initialiser.afterConnectBeforePrepare(connection, databaseTables, logger))

        verifySequence {
            databaseTables.tables
            logger.error("Failed to create database schema: dang!")
        }
        confirmVerified(connection)
    }

    @Test
    internal fun `enables indexes after writing`() {
        val table1 = mockk<SqlTable> { every { name } returns "table1" }
        val table2 = mockk<SqlTable> { every { name } returns "table2" }
        val generator = mockk<SqliteGenerator> {
            every { createIndexesSql(any()) } answers {
                firstArg<SqlTable>().name.let { listOf("$it-index1", "$it-index2") }
            }
        }
        val databaseTables = mockk<BaseDatabaseTables> {
            every { forEachTable(any()) } answers {
                val action = firstArg<(SqlTable) -> Unit>()
                action(table1)
                action(table2)
            }
            every { sqlGenerator } returns generator
        }

        initialiser.afterWriteBeforeCommit(connection, databaseTables, logger)

        verifySequence {
            logger.info("Adding indexes...")
            connection.createStatement()
            databaseTables.forEachTable(any())
            databaseTables.sqlGenerator
            generator.createIndexesSql(table1)
            table1.name
            statement.execute("table1-index1")
            statement.execute("table1-index2")
            databaseTables.sqlGenerator
            generator.createIndexesSql(table2)
            table2.name
            statement.execute("table2-index1")
            statement.execute("table2-index2")
            statement.close()
            logger.info("Indexes added.")
        }
    }

    @Test
    internal fun `exceptions in afterWriteBeforeCommit are propagated`() {
        every { connection.createStatement() } throws IllegalArgumentException("something is wrong")

        expect {
            initialiser.afterWriteBeforeCommit(connection, mockk(), logger)
        }.toThrow<IllegalArgumentException>()
            .withMessage("something is wrong")
    }

}

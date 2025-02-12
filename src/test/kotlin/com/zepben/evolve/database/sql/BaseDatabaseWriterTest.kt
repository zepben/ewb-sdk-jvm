/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql

import com.zepben.evolve.database.sqlite.common.SqliteTable
import com.zepben.evolve.database.sqlite.common.SqliteTableVersion
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

internal class BaseDatabaseWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val resultSet = mockk<ResultSet> {
        every { next() } returns true
        every { getInt(1) } returns 1
        justRun { close() }
    }
    private val statement = mockk<Statement>().also {
        every { it.executeQuery(any()) } returns resultSet
        every { it.resultSet } returns resultSet
        justRun { it.close() }
    }
    private val connection = mockk<Connection> {
        every { createStatement() } returns statement
        justRun { commit() }
        justRun { close() }
    }
    private val getConnection = spyk({ connection })
    private val data = mockk<Any>()

    private var versionTable = spyk(SqliteTableVersion(1))
    private val tables = spyk(object : BaseDatabaseTables() {
        override val includedTables: Sequence<SqliteTable> get() = sequenceOf(versionTable)
    }) { justRun { prepareInsertStatements(connection) } }

    private val writerCalls = spyk<ProtectedWriteCalls>()
    private val writer = createWriter()

    @Test
    internal fun `calls processes in order`() {
        validateCalls()
    }

    @Test
    internal fun `short circuits on process failure`() {
        every { connection.commit() } throws SQLException()
        validateCalls(expectedResult = false)

        every { writerCalls.afterWriteBeforeCommit(any()) } returns false
        validateCalls(expectCommit = false)

        every { writerCalls.writeData(any()) } returns false
        validateCalls(expectAfterWriteBeforeCommit = false)

        every { tables.prepareInsertStatements(any()) } throws SQLException()
        validateCalls(expectWriteData = false)

        every { resultSet.getInt(1) } returns 2
        validateCalls(expectPrepareInsertStatements = false)

        every { writerCalls.afterConnectBeforePrepare(any()) } returns false
        validateCalls(expectVersionMatches = false)

        every { getConnection() } throws SQLException()
        validateCalls(expectAfterConnectBeforePrepare = false)

        every { writerCalls.beforeConnect() } returns false
        validateCalls(expectConnect = false)
    }

    @Test
    internal fun `detects older version mismatches`() {
        versionTable = spyk(SqliteTableVersion(0))
        writer.write(data)

        MatcherAssert.assertThat(systemErr.log, containsString("Unsupported version in database file (got 1, expected 0)"))
    }

    @Test
    internal fun `detects newer version mismatches`() {
        versionTable = spyk(SqliteTableVersion(2))
        writer.write(data)

        MatcherAssert.assertThat(systemErr.log, containsString("Unsupported version in database file (got 1, expected 2)"))
    }

    @Test
    internal fun `detects missing version mismatches`() {
        every { resultSet.next() } returns false
        writer.write(data)

        MatcherAssert.assertThat(systemErr.log, containsString("Missing version table in database file, cannot check compatibility"))
    }

    @Test
    internal fun `handles errors in processors`() {
        every { writerCalls.beforeConnect() } throws SQLException("SQL error message")
        writer.write(data)

        MatcherAssert.assertThat(systemErr.log, containsString("Failed to write the database: SQL error message"))
        systemErr.clearCapturedLog()

        every { writerCalls.beforeConnect() } throws MissingTableConfigException("tables error message")
        writer.write(data)

        MatcherAssert.assertThat(systemErr.log, containsString("Failed to write the database: tables error message"))
        systemErr.clearCapturedLog()

        every { writerCalls.beforeConnect() } throws Exception("unhandled error message")
        ExpectException.expect { writer.write(data) }
            .toThrow<Exception>()
            .withMessage("unhandled error message")
    }

    private fun createWriter(): BaseDatabaseWriter<BaseDatabaseTables, Any> =
        object : BaseDatabaseWriter<BaseDatabaseTables, Any>(tables, getConnection) {
            override fun beforeConnect(): Boolean = writerCalls.beforeConnect()
            override fun afterConnectBeforePrepare(connection: Connection): Boolean = writerCalls.afterConnectBeforePrepare(connection)
            override fun writeData(data: Any): Boolean = writerCalls.writeData(data)
            override fun afterWriteBeforeCommit(connection: Connection): Boolean = writerCalls.afterWriteBeforeCommit(connection)
        }

    private fun validateCalls(
        expectConnect: Boolean = true,
        expectAfterConnectBeforePrepare: Boolean = expectConnect,
        expectVersionMatches: Boolean = expectAfterConnectBeforePrepare,
        expectPrepareInsertStatements: Boolean = expectVersionMatches,
        expectWriteData: Boolean = expectPrepareInsertStatements,
        expectAfterWriteBeforeCommit: Boolean = expectWriteData,
        expectCommit: Boolean = expectAfterWriteBeforeCommit,
        expectedResult: Boolean = expectCommit
    ) {
        clearMocks(writerCalls, getConnection, connection, tables, statement, resultSet, answers = false)
        MatcherAssert.assertThat(writer.write(data), Matchers.equalTo(expectedResult))

        verifySequence {
            writerCalls.beforeConnect()

            if (expectConnect)
                getConnection()

            if (expectAfterConnectBeforePrepare)
                writerCalls.afterConnectBeforePrepare(connection)

            if (expectVersionMatches) {
                connection.createStatement()
                tables.tables
                statement.executeQuery("SELECT version FROM version")
                resultSet.next()
                resultSet.getInt(1)
                resultSet.close()
                statement.close()
            }

            if (expectPrepareInsertStatements)
                tables.prepareInsertStatements(connection)

            if (expectWriteData)
                writerCalls.writeData(data)

            if (expectAfterWriteBeforeCommit)
                writerCalls.afterWriteBeforeCommit(connection)

            if (expectCommit)
                connection.commit()

            // Connection will only be closed if it was correctly opened, which will only be when we expect to call afterConnectBeforePrepare,
            // otherwise we won't enter the `use` block.
            if (expectAfterConnectBeforePrepare)
                connection.close()
        }

        confirmVerified(writerCalls, getConnection, connection, tables, statement, resultSet)
    }

    // A class that is used to capture the calls to the private methods of the writer.
    private class ProtectedWriteCalls {
        fun beforeConnect(): Boolean = true
        fun afterConnectBeforePrepare(@Suppress("unused") connection: Connection): Boolean = true
        fun writeData(@Suppress("unused") data: Any): Boolean = true
        fun afterWriteBeforeCommit(@Suppress("unused") connection: Connection): Boolean = true
    }

}

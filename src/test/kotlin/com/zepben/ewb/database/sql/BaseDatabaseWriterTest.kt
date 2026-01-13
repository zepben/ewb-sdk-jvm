/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.common.BaseDatabaseWriter
import com.zepben.ewb.database.sql.common.MissingTableConfigException
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
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
        justRun { autoCommit = false }
        justRun { commit() }
        justRun { close() }
    }
    private val initialiser = mockk<DatabaseInitialiser<BaseDatabaseTables>> {
        every { connect() } returns connection
        every { beforeConnect(any()) } returns true
        every { afterConnectBeforePrepare(any(), any(), any()) } returns true
        every { afterWriteBeforeCommit(any(), any(), any()) } returns true
    }
    private val writeDataMock = mockk<(Any) -> Boolean>().also { every { it(any()) } returns true }
    private val data = mockk<Any>()

    private val versionTable = mockk<TableVersion> {
        every { supportedVersion } returns 1
        every { getVersion(connection) } returns 1
    }
    private val tables = spyk(object : BaseDatabaseTables() {
        override val sqlGenerator: SqlGenerator
            // Unused by CimDatabaseTables internally, so just return a mockk with no configuration that will break if it is actually used.
            get() = mockk()
        override val includedTables: Sequence<SqlTable> get() = sequenceOf(versionTable)
    }) { justRun { prepareInsertStatements(connection) } }

    private val writer = createWriter()

    @Test
    internal fun `calls processes in order`() {
        validateCalls()
    }

    @Test
    internal fun `short circuits on process failure`() {
        every { connection.commit() } throws SQLException()
        validateCalls(expectedResult = false)

        every { initialiser.afterWriteBeforeCommit(any(), any(), any()) } returns false
        validateCalls(expectCommit = false)

        every { writeDataMock(any()) } returns false
        validateCalls(expectAfterWriteBeforeCommit = false)

        every { tables.prepareInsertStatements(any()) } throws SQLException()
        validateCalls(expectWriteData = false)

        every { versionTable.getVersion(connection) } returns 2
        validateCalls(expectPrepareInsertStatements = false)

        every { initialiser.afterConnectBeforePrepare(any(), any(), any()) } returns false
        validateCalls(expectVersionMatches = false)

        every { initialiser.connect() } throws SQLException()
        validateCalls(expectAfterConnectBeforePrepare = false)

        every { initialiser.beforeConnect(any()) } returns false
        validateCalls(expectConnect = false)
    }

    @Test
    internal fun `detects older version mismatches`() {
        every { versionTable.supportedVersion } returns 0
        writer.write(data)

        assertThat(systemErr.log, containsString("Unsupported version in database file (got 1, expected 0)"))
    }

    @Test
    internal fun `detects newer version mismatches`() {
        every { versionTable.supportedVersion } returns 2
        writer.write(data)

        assertThat(systemErr.log, containsString("Unsupported version in database file (got 1, expected 2)"))
    }

    @Test
    internal fun `detects missing version mismatches`() {
        every { versionTable.getVersion(connection) } returns null
        writer.write(data)

        assertThat(systemErr.log, containsString("Missing version table in database file, cannot check compatibility"))
    }

    @Test
    internal fun `handles errors in processors`() {
        every { initialiser.beforeConnect(any()) } throws SQLException("SQL error message")
        writer.write(data)

        assertThat(systemErr.log, containsString("Failed to write the database: SQL error message"))
        systemErr.clearCapturedLog()

        every { initialiser.beforeConnect(any()) } throws MissingTableConfigException("tables error message")
        writer.write(data)

        assertThat(systemErr.log, containsString("Failed to write the database: tables error message"))
        systemErr.clearCapturedLog()

        every { initialiser.beforeConnect(any()) } throws Exception("unhandled error message")
        ExpectException.expect { writer.write(data) }
            .toThrow<Exception>()
            .withMessage("unhandled error message")
    }

    private fun createWriter(): BaseDatabaseWriter<BaseDatabaseTables, Any> {
        return object : BaseDatabaseWriter<BaseDatabaseTables, Any>() {
            override val databaseTables: BaseDatabaseTables
                get() = tables
            override val databaseInitialiser: DatabaseInitialiser<BaseDatabaseTables>
                get() = initialiser

            override fun writeData(data: Any): Boolean = writeDataMock(data)
        }
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
        clearMocks(initialiser, writeDataMock, connection, tables, statement, resultSet, versionTable, answers = false)
        assertThat(writer.write(data), equalTo(expectedResult))

        verifySequence {
            initialiser.beforeConnect(any())

            if (expectConnect)
                initialiser.connect()

            if (expectAfterConnectBeforePrepare) {
                connection.autoCommit = false
                initialiser.afterConnectBeforePrepare(connection, tables, any())
            }

            if (expectVersionMatches) {
                tables.tables
                versionTable.supportedVersion
                versionTable.getVersion(connection)
            }

            if (expectPrepareInsertStatements)
                tables.prepareInsertStatements(connection)

            if (expectWriteData)
                writeDataMock(data)

            if (expectAfterWriteBeforeCommit)
                initialiser.afterWriteBeforeCommit(connection, tables, any())

            if (expectCommit)
                connection.commit()

            // Connection will only be closed if it was correctly opened, which will only be when we expect to call afterConnectBeforePrepare,
            // otherwise we won't enter the `use` block.
            if (expectAfterConnectBeforePrepare)
                connection.close()
        }

        confirmVerified(initialiser, writeDataMock, connection, tables, statement, resultSet, versionTable)
    }

}

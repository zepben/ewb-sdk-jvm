/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim

import com.zepben.ewb.database.sql.MissingTableConfigException
import com.zepben.ewb.database.sqlite.cim.metadata.MetadataCollectionReader
import com.zepben.ewb.database.sqlite.common.SqliteTableVersion
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.Connection

internal class CimDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val metadata = MetadataCollection()
    private val service = object : BaseService("service name", metadata) {}

    private val databaseFile = "databaseFile"
    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.read(metadata) } returns true }
    private val serviceReader = mockk<BaseServiceReader<BaseService>>().also { every { it.read(service) } returns true }

    private val connection = mockk<Connection> {
        justRun { close() }
    }

    private val tableVersion = mockk<SqliteTableVersion> {
        every { getVersion(any()) } returns 1
        every { supportedVersion } returns 1
    }

    private var afterServiceReadResult = true
    private var afterServiceReadCalled = false

    private val reader = object : CimDatabaseReader<CimDatabaseTables, BaseService>(
        connection,
        databaseFile,
        mockk<CimDatabaseTables> { every { tables } returns mapOf(SqliteTableVersion::class to tableVersion) },
        { _, _ -> metadataReader },
        { _, _ -> serviceReader },
    ) {
        override fun afterServiceRead(service: BaseService): Boolean {
            afterServiceReadCalled = true
            return afterServiceReadResult
        }
    }

    @Test
    internal fun `can read from valid database`() {
        assertThat("Should have read", reader.read(service))

        verifyReadersCalled()
        assertThat("afterServiceRead should have been called", afterServiceReadCalled)
    }

    @Test
    internal fun `can only run once`() {
        assertThat("Should have read the first time", reader.read(service))
        assertThat("Shouldn't have read a second time", !reader.read(service))

        assertThat(systemErr.log, containsString("You can only use the database reader once."))
    }

    @Test
    internal fun `detect missing databases`() {
        every { tableVersion.getVersion(any()) } answers { callOriginal() }
        every { tableVersion.selectSql } returns "SELECT"
        every { connection.prepareStatement(any()) } throws Exception("Test Error")

        assertThat("Should not have read", !reader.read(service))
        assertThat(systemErr.log, containsString("Failed to connect to the database for reading: Test Error"))

        verify { connection.prepareStatement(any()) }
        confirmVerified(connection, metadataReader, serviceReader)
        assertThat("afterServiceRead shouldn't have been called", !afterServiceReadCalled)
    }

    @Test
    internal fun `detect old databases`() {
        every { tableVersion.getVersion(any()) } returns 0

        assertThat("Should not have read", !reader.read(service))
        assertThat(
            systemErr.log,
            containsString("Unable to read from database $databaseFile [found v0, expected v1]. Consider using the UpgradeRunner if you wish to support this database.")
        )

        verifyInvalidVersionCalls()
    }

    @Test
    internal fun `detect future databases`() {
        every { tableVersion.getVersion(any()) } returns 2

        assertThat("Should not have read", !reader.read(service))
        assertThat(
            systemErr.log,
            containsString("Unable to read from database $databaseFile [found v2, expected v1]. You need to use a newer version of the SDK to read this database.")
        )

        verifyInvalidVersionCalls()
    }

    @Test
    internal fun `detect invalid databases`() {
        every { tableVersion.getVersion(any()) } returns null

        assertThat("Should not have read", !reader.read(service))
        assertThat(systemErr.log, containsString("Failed to read the version number from the selected database. Are you sure it is a EWB database?"))

        verifyInvalidVersionCalls()
    }

    @Test
    internal fun `detect metadata failure`() {
        every { metadataReader.read(metadata) } returns false

        assertThat("Should not have read", !reader.read(service))

        verifyReadersCalled()
        assertThat("afterServiceRead shouldn't have been called", !afterServiceReadCalled)
    }

    @Test
    internal fun `detect reader failure`() {
        every { serviceReader.read(service) } returns false

        assertThat("Should not have read", !reader.read(service))

        verifyReadersCalled()
        assertThat("afterServiceRead shouldn't have been called", !afterServiceReadCalled)
    }

    @Test
    internal fun `detect afterServiceRead failure`() {
        afterServiceReadResult = false

        assertThat("Should not have read", !reader.read(service))

        verifyReadersCalled()
        assertThat("afterServiceRead should have been called", afterServiceReadCalled)
    }

    @Test
    internal fun `detect missing tables`() {
        every { serviceReader.read(service) } throws MissingTableConfigException("Test Error")

        assertThat("Should not have read", !reader.read(service))

        assertThat(systemErr.log, containsString("Unable to read database: Test Error"))
    }

    private fun verifyReadersCalled() {
        verifySequence {
            tableVersion.supportedVersion
            tableVersion.getVersion(connection)

            metadataReader.read(metadata)
            serviceReader.read(service)
        }
    }

    private fun verifyInvalidVersionCalls() {
        verifySequence {
            tableVersion.supportedVersion
            tableVersion.getVersion(connection)
        }

        confirmVerified(metadataReader, serviceReader)
        assertThat("afterServiceRead shouldn't have been called", !afterServiceReadCalled)
    }

}

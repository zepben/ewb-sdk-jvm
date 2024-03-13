/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.EwbDatabaseType
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.Paths
import java.sql.Connection

internal class BaseDatabaseReaderTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val databaseFile = "databaseFile"
    private val metadataReader = mockk<MetadataCollectionReader>().also { every { it.load() } returns true }
    private val databaseReader = mockk<BaseServiceReader>().also { every { it.load() } returns true }

    private val connection = mockk<Connection>(relaxed = true)
    private val connectionResult = mockk<UpgradeRunner.ConnectionResult>().also {
        every { it.connection } returns connection
        every { it.version } returns TableVersion().SUPPORTED_VERSION
    }
    private val upgradeRunner = mockk<UpgradeRunner>().also { every { it.connectAndUpgrade(any(), any(), any()) } returns connectionResult }

    private val databaseTables = mockk<BaseDatabaseTables>()
    private val ewbDatabaseType = EwbDatabaseType.NETWORK

    private var postLoadResult = true
    private var postLoadCalled = false

    private val reader = object : BaseDatabaseReader(
        databaseFile,
        { metadataReader },
        { databaseReader },
        mockk(), // Services won't be used as we have replaced the postLoad implementation. The real function is tested by each descendant class.
        upgradeRunner,
        ewbDatabaseType
    ) {
        override fun postLoad(): Boolean {
            postLoadCalled = true
            return postLoadResult
        }
    }

    @Test
    internal fun load() {
        assertThat("Should have loaded", reader.load())

        verify(exactly = 1) { upgradeRunner.connectAndUpgrade("jdbc:sqlite:$databaseFile", Paths.get(databaseFile), ewbDatabaseType) }
        verify(exactly = 1) { connectionResult.version }
        verify(exactly = 1) { connectionResult.connection }
        verifyLoad()
        verify(exactly = 1) { connection.close() }
        confirmVerified(connection, databaseTables)
    }

    @Test
    fun `can only run once`() {
        assertThat("Should have loaded", reader.load())
        assertThat("Should not have run a second time", !reader.load())
        assertThat(systemErr.log, containsString("You can only use the database reader once."))
    }

    @Test
    internal fun `detect invalid databases`() {
        every { upgradeRunner.connectAndUpgrade(any(), any(), ewbDatabaseType) } throws UpgradeRunner.UpgradeException("Test Error")

        assertThat("Should not have loaded", !reader.load())
        assertThat(systemErr.log, containsString("Failed to connect to the database for reading: Test Error"))

        verify(exactly = 1) { upgradeRunner.connectAndUpgrade("jdbc:sqlite:$databaseFile", Paths.get(databaseFile), ewbDatabaseType) }
        verify(exactly = 0) { connectionResult.version }
        verify(exactly = 0) { connectionResult.connection }
        verify(exactly = 0) { metadataReader.load() }
        verify(exactly = 0) { databaseReader.load() }
        assertThat("postLoad should not have been called", !postLoadCalled)
        verify(exactly = 0) { connection.close() }
        confirmVerified(connection, databaseTables)
    }

    @Test
    internal fun `detect metadata failure`() {
        every { metadataReader.load() } returns false

        assertThat("Should not have loaded", !reader.load())

        verifyLoad()
    }

    @Test
    internal fun `detect reader failure`() {
        every { databaseReader.load() } returns false

        assertThat("Should not have loaded", !reader.load())

        verifyLoad()
    }

    @Test
    internal fun `detect postLoad failure`() {
        postLoadResult = false

        assertThat("Should not have loaded", !reader.load())

        verifyLoad()
    }

    @Test
    internal fun `detect missing tables`() {
        every { databaseReader.load() } throws MissingTableConfigException("Test Error")

        assertThat("Should not have loaded", !reader.load())

        assertThat(systemErr.log, containsString("Unable to load database: Test Error"))
    }

    private fun verifyLoad() {
        verify(exactly = 1) { metadataReader.load() }
        verify(exactly = 1) { databaseReader.load() }
        assertThat("postLoad should have been called", postLoadCalled)
    }

}

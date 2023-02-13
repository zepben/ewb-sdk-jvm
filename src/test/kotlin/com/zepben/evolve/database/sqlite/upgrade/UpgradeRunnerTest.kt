/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade

import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.extensions.executeConfiguredQuery
import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.sql.*

internal class UpgradeRunnerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val resultSet = mockk<ResultSet>(relaxed = true)
    private val statement = mockk<Statement>(relaxed = true).also { every { it.executeQuery(any()) } returns resultSet }
    private val preparedStatement = mockk<PreparedStatement>(relaxed = true).also { every { it.executeQuery(any()) } returns resultSet }
    private val connection = mockk<Connection>(relaxed = true)
    private val connectionProvider = spyk<(String) -> Connection>({ connection })
    private val statementProvider = spyk<(Connection) -> Statement>({ statement })
    private val preparedStatementProvider = spyk<(Connection, String) -> PreparedStatement>({ _, _ -> preparedStatement })
    private val createBackup = spyk<(Path, Path, CopyOption) -> Unit>({ _, _, _ -> })

    private val changeSet1 = mockk<ChangeSet>(relaxed = true).also { every { it.number } returns 1 }
    private val changeSet2 = mockk<ChangeSet>(relaxed = true).also { every { it.number } returns 2 }
    private val changeSets = listOf(changeSet1, changeSet2)

    private val column = mockk<Column>().also { every { it.queryIndex } returns 1 }
    private val tableVersion = mockk<TableVersion>().also {
        every { it.SUPPORTED_VERSION } returns 2
        every { it.VERSION } returns column
        every { it.selectSql() } returns "select version"
        every { it.preparedUpdateSql() } returns "update statement"
    }

    private val upgradeRunner = UpgradeRunner(connectionProvider, statementProvider, preparedStatementProvider, createBackup, changeSets, tableVersion)

    @Test
    internal fun hasChangeSetsForEachSupportedVersionInOrder() {
        val actualChangeSets = UpgradeRunner().changeSets

        // If upgrades are supported, make sure they are all registered in the correct order.
        if (actualChangeSets.isNotEmpty())
            assertThat(actualChangeSets.map { it.number }, equalTo((actualChangeSets.minOf { it.number }..TableVersion().SUPPORTED_VERSION).toList()))
    }

    @Test
    internal fun configuresConnectionForBatchProcessing() {
        mockkStatic("com.zepben.evolve.database.sqlite.extensions.ConnectionExtensionsKt")
        configureDatabaseVersion(2)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        verify(exactly = 1) { connectionProvider("driver:database") }
        verify(exactly = 1) { connection.configureBatch(statementProvider) }
    }


    @Test
    internal fun runsEachChangeSetInTransactions() {
        configureDatabaseVersion(0)
        every { changeSet1.commands } returns listOf("1-1", "1-2")
        every { changeSet2.commands } returns listOf("2-1", "2-2")

        val connectionResult = upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        assertThat(connectionResult.connection, equalTo(connection))
        assertThat(connectionResult.version, equalTo(2))

        assertThat(systemErr.log, containsString("Upgrading database 'database' from v0 to v2"))

        verifyOrder {

            createBackup(any(), any(), any())
            tableVersion.preparedUpdateSql()
            preparedStatementProvider(connection, "update statement")

            validateChangeSetExecuted(changeSet1, listOf("1-1", "1-2"), 1)
            validateChangeSetExecuted(changeSet2, listOf("2-1", "2-2"), 2)
        }
    }

    @Test
    internal fun doesntRunUpgradeIfCurrentVersion() {
        configureDatabaseVersion(2)
        changeSets.forEach { clearMocks(it, answers = false) }

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        confirmVerified(createBackup)
        changeSets.forEach { confirmVerified(it) }
    }

    @Test
    internal fun onlyRunsChangeSetsIfRequired() {
        configureDatabaseVersion(1)
        changeSets.forEach { clearMocks(it, answers = false) }

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        verify(exactly = 1) { createBackup(any(), any(), any()) }

        verify(atLeast = 1) { changeSet1.number }
        confirmVerified(changeSet1)

        verify(atLeast = 1) { changeSet2.number }
        verify(exactly = 1) { changeSet2.preCommandsHook(any()) }
        verify(exactly = 1) { changeSet2.commands }
        verify(exactly = 1) { changeSet2.postCommandsHook(any()) }
        confirmVerified(changeSet2)
    }

    @Test
    internal fun reportsObsoleteDatabaseVersion() {
        mockkStatic("com.zepben.evolve.database.sqlite.extensions.StatementExtensionsKt")
        configureDatabaseVersion(-2)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage(
                "Failed to execute upgrade scripts. Unable to upgrade obsolete database version [v-2], upgrading a database before v0 is " +
                    "unsupported. Please generate a new database from the source system using an updated migrator."
            )

        verify(exactly = 1) { statement.executeConfiguredQuery("select version") }
        verify(exactly = 0) { statement.executeQuery("SELECT major FROM version") }
    }

    @Test
    internal fun reportsFuturisticDatabaseVersion() {
        mockkStatic("com.zepben.evolve.database.sqlite.extensions.StatementExtensionsKt")
        configureDatabaseVersion(3)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage(
                "Failed to execute upgrade scripts. Selected database is a newer version [v3] than the supported version [v2]. Either update the " +
                    "version of the server you are using, or downgrade the migrator."
            )

        verify(exactly = 1) { statement.executeConfiguredQuery("select version") }
    }

    @Test
    internal fun reportsMissingVersionNumbers() {
        mockkStatic("com.zepben.evolve.database.sqlite.extensions.StatementExtensionsKt")
        every { resultSet.getInt(1) } throws Exception()

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage(
                "Failed to execute upgrade scripts. Invalid EWB database detected, unable to read the version number from the database. Please " +
                    "ensure you only have databases in the EWB data directory that have been generated by a working migrator."
            )

        verify(exactly = 1) { statement.executeConfiguredQuery("select version") }
    }

    @Test
    internal fun reportsDatabaseConnectionIssues() {
        val exceptionMessage = "some error"
        val exception = SQLException(exceptionMessage)
        every { connectionProvider(any()) } throws exception

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage(exceptionMessage)
            .exception.apply {
                assertThat(cause, equalTo(exception))
            }
    }

    @Test
    internal fun reportsSqlExceptions() {
        val exception = SQLException("sql message")
        every { preparedStatementProvider(any(), any()) } throws exception

        configureDatabaseVersion(0)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage("Failed to execute upgrade scripts. sql message")
    }

    @Test
    internal fun createAppropriatelyNamedBackup() {
        configureDatabaseVersion(1)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database.db"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("with/path/database.sqlite"))

        verify(exactly = 1) { createBackup(Paths.get("database"), Paths.get("database-v1"), StandardCopyOption.REPLACE_EXISTING) }
        verify(exactly = 1) { createBackup(Paths.get("database.db"), Paths.get("database-v1.db"), StandardCopyOption.REPLACE_EXISTING) }
        verify(exactly = 1) {
            createBackup(
                Paths.get("with/path/database.sqlite"),
                Paths.get("with/path/database-v1.sqlite"),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        confirmVerified(createBackup)
    }

    @Test
    internal fun reportsDatabaseBackupIOErrors() {
        configureDatabaseVersion(0)

        val exception = IOException("io error")
        every { createBackup(any(), any(), any()) } throws exception

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage("Failed to create database backup. io error")
    }

    @Test
    internal fun reportsDatabaseBackupSecurityExceptions() {
        configureDatabaseVersion(0)

        val exception = SecurityException("security error")
        every { createBackup(any(), any(), any()) } throws exception

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow<UpgradeRunner.UpgradeException>()
            .withMessage("Failed to create database backup. security error")
    }

    @Test
    internal fun propagatesUnknownExceptions() {
        val exception = Exception("unknown exception")
        every { preparedStatementProvider(any(), any()) } throws exception
        configureDatabaseVersion(0)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrowAny().exception.also {
            assertThat(it, equalTo(exception))
        }
    }

    @Disabled
    @Test
    @Throws(Exception::class)
    fun upgradeRealFile() {
        systemErr.unmute()
        val databaseFile = "remove @Disabled and change this to your file name"
        val upgradeRunner = UpgradeRunner()
        upgradeRunner.connectAndUpgrade("jdbc:sqlite:$databaseFile", Paths.get(databaseFile))
    }

    private fun configureDatabaseVersion(version: Int) {
        every { resultSet.next() } returns true
        every { resultSet.getInt(1) } returns version
    }

    private fun MockKVerificationScope.validateChangeSetExecuted(changeSet: ChangeSet, expectedCommands: List<String>, changeSetNumber: Int) {
        statement.executeUpdate("PRAGMA foreign_keys=OFF")

        changeSet.commands
        expectedCommands.forEach { statement.executeUpdate(it) }

        preparedStatement.setInt(any(), changeSetNumber)
        preparedStatement.executeUpdate()

        statement.executeUpdate("PRAGMA foreign_key_check")
        statement.executeUpdate("COMMIT")
        statement.executeUpdate("BEGIN TRANSACTION")
        statement.executeUpdate("PRAGMA foreign_keys=ON")
    }

}

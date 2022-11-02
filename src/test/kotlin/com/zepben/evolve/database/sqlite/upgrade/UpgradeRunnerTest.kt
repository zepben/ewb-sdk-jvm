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
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.InOrder
import org.mockito.kotlin.*
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.sql.*

class UpgradeRunnerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val resultSet = mock<ResultSet>()
    private val statement = mock<Statement>().also { doReturn(resultSet).`when`(it).executeQuery(any()) }
    private val preparedStatement = mock<PreparedStatement>().also { doReturn(resultSet).`when`(it).executeQuery(any()) }
    private val connection = mock<Connection>()
    private val connectionProvider = spy<(String) -> Connection>(value = { connection })
    private val statementProvider = spy<(Connection) -> Statement>(value = { statement })
    private val preparedStatementProvider = spy<(Connection, String) -> PreparedStatement>(value = { _, _ -> preparedStatement })
    private val createBackup = spy<(Path, Path, CopyOption) -> Unit>(value = { _, _, _ -> })

    private val changeSet1 = mock<ChangeSet>().also { doReturn(1).`when`(it).number }
    private val changeSet2 = mock<ChangeSet>().also { doReturn(2).`when`(it).number }
    private val changeSets = listOf(changeSet1, changeSet2)

    private val column = mock<Column>().also { doReturn(1).`when`(it).queryIndex }
    private val tableVersion = mock<TableVersion>().also {
        doReturn(2).`when`(it).SUPPORTED_VERSION
        doReturn(column).`when`(it).VERSION
        doReturn("select version").`when`(it).selectSql()
        doReturn("update statement").`when`(it).preparedUpdateSql()
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
        configureDatabaseVersion(2)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        verify(connectionProvider).invoke("driver:database")
        verify(connection).configureBatch(statementProvider)
    }


    @Test
    fun runsEachChangeSetInTransactions() {
        configureDatabaseVersion(0)
        doReturn(listOf("1-1", "1-2")).`when`(changeSet1).commands()
        doReturn(listOf("2-1", "2-2")).`when`(changeSet2).commands()

        val connectionResult = upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        assertThat(connectionResult.connection, equalTo(connection))
        assertThat(connectionResult.version, equalTo(2))

        assertThat(systemErr.log, containsString("Upgrading database 'database' from v0 to v2"))

        val inOrder = inOrder(createBackup, statement, preparedStatement, preparedStatementProvider)

        inOrder.verify(createBackup).invoke(any(), any(), any())
        inOrder.verify(preparedStatementProvider).invoke(connection, tableVersion.preparedUpdateSql())

        validateChangeSetExecuted(inOrder, changeSet1)
        validateChangeSetExecuted(inOrder, changeSet2)
    }

    @Test
    internal fun doesntRunUpgradeIfCurrentVersion() {
        configureDatabaseVersion(2)
        changeSets.forEach { clearInvocations(it) }

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        verifyNoMoreInteractions(createBackup)
        changeSets.forEach { verifyNoMoreInteractions(it) }
    }

    @Test
    internal fun onlyRunsChangeSetsIfRequired() {
        configureDatabaseVersion(1)
        changeSets.forEach { clearInvocations(it) }

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        verify(createBackup).invoke(any(), any(), any())

        verify(changeSet1, atLeastOnce()).number
        verifyNoMoreInteractions(changeSet1)

        verify(changeSet2, atLeastOnce()).number
        verify(changeSet2).preCommandsHook(any())
        verify(changeSet2).commands()
        verify(changeSet2).postCommandsHook(any())
        verifyNoMoreInteractions(changeSet2)
    }

    @Test
    internal fun reportsObsoleteDatabaseVersion() {
        configureDatabaseVersion(-2)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage(
                "Failed to execute upgrade scripts. Unable to upgrade obsolete database version [v-2], upgrading a database before v0 is " +
                    "unsupported. Please generate a new database from the source system using an updated migrator."
            )

        verify(statement).executeConfiguredQuery("select version")
        verify(statement, never()).executeQuery("SELECT major FROM version")
    }

    @Test
    internal fun reportsFuturisticDatabaseVersion() {
        configureDatabaseVersion(3)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage(
                "Failed to execute upgrade scripts. Selected database is a newer version [v3] than the supported version [v2]. Either update the " +
                    "version of the server you are using, or downgrade the migrator."
            )

        verify(statement).executeConfiguredQuery("select version")
    }

    @Test
    internal fun reportsMissingVersionNumbers() {
        doAnswer { throw Exception() }.`when`(resultSet).getInt(1)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage(
                "Failed to execute upgrade scripts. Invalid EWB database detected, unable to read the version number from the database. Please " +
                    "ensure you only have databases in the EWB data directory that have been generated by a working migrator."
            )

        verify(statement).executeConfiguredQuery("select version")
    }

    @Test
    internal fun reportsDatabaseConnectionIssues() {
        val exception = SQLException("some error")
        doAnswer { throw exception }.`when`(connectionProvider).invoke(any())

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage(exception.message)
            .exception().apply {
                assertThat(cause, equalTo(exception))
            }
    }

    @Test
    internal fun reportsSqlExceptions() {
        val exception = SQLException("sql message")
        doAnswer { throw exception }.`when`(preparedStatementProvider).invoke(any(), any())

        configureDatabaseVersion(0)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage("Failed to execute upgrade scripts. sql message")
    }

    @Test
    fun createAppropriatelyNamedBackup() {
        configureDatabaseVersion(1)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database.db"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("with/path/database.sqlite"))

        verify(createBackup).invoke(Paths.get("database"), Paths.get("database-v1"), StandardCopyOption.REPLACE_EXISTING)
        verify(createBackup).invoke(Paths.get("database.db"), Paths.get("database-v1.db"), StandardCopyOption.REPLACE_EXISTING)
        verify(createBackup).invoke(Paths.get("with/path/database.sqlite"), Paths.get("with/path/database-v1.sqlite"), StandardCopyOption.REPLACE_EXISTING)
        verifyNoMoreInteractions(createBackup)
    }

    @Test
    internal fun reportsDatabaseBackupIOErrors() {
        configureDatabaseVersion(0)

        val exception = IOException("io error")
        doAnswer { throw exception }.`when`(createBackup).invoke(any(), any(), any())

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage("Failed to create database backup. io error")
    }

    @Test
    internal fun reportsDatabaseBackupSecurityExceptions() {
        configureDatabaseVersion(0)

        val exception = SecurityException("security error")
        doAnswer { throw exception }.`when`(createBackup).invoke(any(), any(), any())

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage("Failed to create database backup. security error")
    }

    @Test
    internal fun propagatesUnknownExceptions() {
        val exception = Exception("unknown exception")
        doAnswer { throw exception }.`when`(preparedStatementProvider).invoke(any(), any())
        configureDatabaseVersion(0)

        expect {
            upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        }.toThrow().exception().also {
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
        doReturn(true).`when`(resultSet).next()
        doReturn(version).`when`(resultSet).getInt(1)
    }

    private fun validateChangeSetExecuted(inOrder: InOrder, changeSet: ChangeSet) {
        inOrder.verify(statement).executeUpdate("PRAGMA foreign_keys=OFF")

        changeSet.commands().forEach { inOrder.verify(statement).executeUpdate(it) }

        inOrder.verify(preparedStatement).setInt(tableVersion.VERSION.queryIndex, changeSet.number)
        inOrder.verify(preparedStatement).executeUpdate()

        inOrder.verify(statement).executeUpdate("PRAGMA foreign_key_check")
        inOrder.verify(statement).executeUpdate("COMMIT")
        inOrder.verify(statement).executeUpdate("BEGIN TRANSACTION")
        inOrder.verify(statement).executeUpdate("PRAGMA foreign_keys=ON")
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.upgrade
import com.zepben.cimbend.database.sqlite.tables.TableVersion
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import com.zepben.testutils.mockito.DefaultAnswer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.AdditionalAnswers
import org.mockito.Mockito.*
import java.nio.file.CopyOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.sql.*

class UpgradeRunnerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val resultSet = mock(ResultSet::class.java)
    private val statement = mock(Statement::class.java, DefaultAnswer.of(ResultSet::class.java, resultSet))
    private val preparedStatement = mock(PreparedStatement::class.java, DefaultAnswer.of(ResultSet::class.java, resultSet))
    private val connection = mock(Connection::class.java)
    private val connectionProvider = spyLambda<Function1<String, Connection>> { connection }
    private val statementProvider = spyLambda<Function1<Connection, Statement>> { statement }
    private val preparedStatementProvider = spyLambda<Function2<Connection, String, PreparedStatement>> { _, _ -> preparedStatement }
    private val createBackup = spyLambda<Function3<Path, Path, CopyOption, Unit>> { _, _, _ -> }

    private val upgradeRunner = UpgradeRunner(connectionProvider, statementProvider, preparedStatementProvider, createBackup)
    private val expectedChangeSets: List<ChangeSet> = upgradeRunner.changeSets
    private val tableVersion = TableVersion()

    @Test
    @Throws(Exception::class)
    fun runsUpgrades() {
        configureDatabaseVersion(14)
        val connectionResult = upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        assertThat(connectionResult.connection, equalTo(connection))
        assertThat(connectionResult.version, equalTo(tableVersion.SUPPORTED_VERSION))

        verify(connectionProvider, times(1)).invoke("driver:database")

        verify(statementProvider, times(2)).invoke(connection)
        verify(preparedStatementProvider, times(1)).invoke(connection, tableVersion.preparedUpdateSql())

        verify(statement, times(1)).executeUpdate("PRAGMA journal_mode = OFF")
        verify(statement, times(1)).executeUpdate("PRAGMA synchronous = OFF")
        verify(statement, times(expectedChangeSets.size)).executeUpdate("PRAGMA foreign_keys=OFF")
        verify(statement, times(expectedChangeSets.size)).executeUpdate("PRAGMA foreign_key_check")
        verify(statement, times(expectedChangeSets.size)).executeUpdate("COMMIT")
        verify(statement, times(expectedChangeSets.size)).executeUpdate("BEGIN TRANSACTION")
        verify(statement, times(expectedChangeSets.size)).executeUpdate("PRAGMA foreign_keys=ON")

        verify(preparedStatement, times(expectedChangeSets.size)).setInt(eq(tableVersion.VERSION.queryIndex()), anyInt())
        verify(preparedStatement, times(expectedChangeSets.size)).executeUpdate()

        assertThat(
            systemErr.log,
            containsString("Upgrading database 'database' from v14 to v${tableVersion.SUPPORTED_VERSION}")
        )
        verify(createBackup, times(1)).invoke(Paths.get("database"), Paths.get("database-v14"), StandardCopyOption.REPLACE_EXISTING)

        for (changeSet in expectedChangeSets) {
            assertThat(systemErr.log, containsString("Applying database change set ${changeSet.number}"))
            val inOrder = inOrder(statement)
            changeSet.commands().forEach { inOrder.verify(statement).executeUpdate(it) }
        }
    }

    @Test
    @Throws(Exception::class)
    fun createAppropriatelyNamedBackup() {
        configureDatabaseVersion(14)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database.db"))
        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("with/path/database.sqlite"))

        verify(createBackup, times(1)).invoke(Paths.get("database"), Paths.get("database-v14"), StandardCopyOption.REPLACE_EXISTING)
        verify(createBackup, times(1)).invoke(Paths.get("database.db"), Paths.get("database-v14.db"), StandardCopyOption.REPLACE_EXISTING)
        verify(createBackup, times(1))
            .invoke(Paths.get("with/path/database.sqlite"), Paths.get("with/path/database-v14.sqlite"), StandardCopyOption.REPLACE_EXISTING)
    }

    @Test
    @Throws(Exception::class)
    fun onlyRunsVersionsIfRequired() {
        configureDatabaseVersion(tableVersion.SUPPORTED_VERSION)
        upgradeRunner.connectAndUpgrade("database", Paths.get("filename"))

        verify(statement, times(2)).executeUpdate(any())
        verify(statement, times(1)).executeUpdate("PRAGMA journal_mode = OFF")
        verify(statement, times(1)).executeUpdate("PRAGMA synchronous = OFF")
    }

    @Test
    @Throws(Exception::class)
    fun warnsAboutUnsupportedVersions() {
        doAnswer { throw SQLException("Unable to connect to database [missing-database].") }.`when`(connectionProvider).invoke("missing-database")
        validateException(false, "Unable to connect to database [missing-database].")

        doReturn(false).`when`(resultSet).next()
        validateException(true, "Failed to execute upgrade scripts. No version number found in database.")

        configureDatabaseVersion(1)
        validateException(
            true,
            "Failed to execute upgrade scripts. Upgrading a database before v14 is unsupported. Please generate a new database from the source system."
        )

        configureDatabaseVersion(1234567)
        validateException(
            true,
            "Failed to execute upgrade scripts. Selected database is a newer version [v1234567] than the supported version [${tableVersion.SUPPORTED_VERSION}]."
        )
    }

    @Test
    fun `migrates version table`() {
        doThrow(SQLException()).`when`(statement).executeQuery(tableVersion.selectSql())
        doReturn(true).`when`(resultSet).next()
        doReturn(14).`when`(resultSet).getInt(1)

        upgradeRunner.connectAndUpgrade("driver:database", Paths.get("database"))

        val inOrder = inOrder(statement)
        inOrder.verify(statement).executeQuery("SELECT major FROM version")
        inOrder.verify(statement).execute("DROP TABLE version")
        inOrder.verify(statement).execute(tableVersion.createTableSql())
        inOrder.verify(statement).executeUpdate("INSERT into ${tableVersion.name()} VALUES (14)")
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

    @Throws(SQLException::class)
    private fun configureDatabaseVersion(version: Int) {
        doReturn(true).`when`(resultSet).next()
        doReturn(version).`when`(resultSet).getInt(tableVersion.VERSION.queryIndex())
    }

    @Throws(SQLException::class)
    private fun validateException(validDatabaseConnection: Boolean, expectedMessage: String) {
        clearInvocations(connection)
        ExpectException.expect {
            upgradeRunner.connectAndUpgrade(
                if (validDatabaseConnection) "database" else "missing-database",
                Paths.get("filename")
            )
        }
            .toThrow(UpgradeRunner.UpgradeException::class.java)
            .withMessage(expectedMessage)
        verify(connection, times(if (validDatabaseConnection) 1 else 0)).close()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> spyLambda(lambda: T): T {
            val interfaces = lambda.javaClass.interfaces
            assertThat(interfaces, arrayWithSize(1))
            return mock(interfaces[0] as Class<T>, AdditionalAnswers.delegatesTo<Any>(lambda))
        }
    }
}

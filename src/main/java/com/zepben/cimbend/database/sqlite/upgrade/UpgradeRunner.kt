/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.upgrade

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.database.executeConfiguredQuery
import com.zepben.cimbend.database.sqlite.extensions.configureBatch
import com.zepben.cimbend.database.sqlite.tables.TableVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.*
import java.sql.*

class UpgradeRunner constructor(
    private val getConnection: (String) -> Connection = DriverManager::getConnection,
    private val getStatement: (Connection) -> Statement = Connection::createStatement,
    private val getPreparedStatement: (Connection, String) -> PreparedStatement = Connection::prepareStatement,
    private val createBackup: (databaseFilename: Path, backupFilename: Path, copyOption: CopyOption) -> Unit = { f, b, o -> Files.copy(f, b, o) }
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    internal val changeSets: List<ChangeSet> = listOf(
        changeSet15(),
        changeSet16(),
        changeSet17(),
        changeSet18(),
        changeSet19(),
        changeSet20(),
        changeSet21()
    ).asUnmodifiable()

    private val tableVersion = TableVersion()

    init {
        changeSets.forEachIndexed { index, changeSet ->
            val previous: Int = changeSets.getOrNull(index - 1)?.number ?: (changeSet.number - 1)
            require(previous == changeSet.number - 1) {
                "Change set has been registered in non-sequential order. Did you bump the change set number correctly?"
            }
        }

        require(changeSets.last().number == tableVersion.SUPPORTED_VERSION) {
            "The last registered change set does not match the supported version. Did you forget to bump the supported version number?"
        }
    }

    @Throws(SQLException::class)
    fun connectAndUpgrade(databaseDescriptor: String, databaseFile: Path): ConnectionResult {
        val connection = runCatching { getConnection(databaseDescriptor).configureBatch(getStatement) }
            .getOrElse { throw UpgradeException(it.message, it) }

        return try {
            val statement = getStatement(connection)

            val databaseVersion = getVersion(statement) ?: throw UpgradeException("No version number found in database.")
            when {
                databaseVersion < 14 -> throw UpgradeException("Upgrading a database before v14 is unsupported. Please generate a new database from the source system.")
                databaseVersion > tableVersion.SUPPORTED_VERSION -> throw SQLException("Selected database is a newer version [v$databaseVersion] than the supported version [${tableVersion.SUPPORTED_VERSION}].")
                databaseVersion < tableVersion.SUPPORTED_VERSION -> {
                    logger.info("Upgrading database '{}' from v{} to v{}", databaseFile, databaseVersion, changeSets.last().number)
                    createBackup(databaseFile, createBackupName(databaseFile, databaseVersion), StandardCopyOption.REPLACE_EXISTING)

                    val versionUpdateStatement = getPreparedStatement(connection, tableVersion.preparedUpdateSql())
                    changeSets.asSequence()
                        .filter { databaseVersion < it.number }
                        .forEach { runUpgrade(it, statement, versionUpdateStatement) }
                }
            }

            ConnectionResult(connection, tableVersion.SUPPORTED_VERSION)
        } catch (e: Exception) {
            //
            // NOTE: Closing the connection will rollback any active transactions (see https://www.sqlite.org/lang_transaction.html).
            //
            connection.close()
            when (e) {
                is UpgradeException, is SQLException -> throw UpgradeException("Failed to execute upgrade scripts. ${e.message}", e)
                is IOException, is SecurityException -> throw UpgradeException("Failed to create database backup. ${e.message}", e)
                else -> throw e
            }
        }
    }

    @Throws(SQLException::class, IOException::class, SecurityException::class)
    private fun runUpgrade(
        changeSet: ChangeSet,
        statement: Statement,
        versionUpdateStatement: PreparedStatement
    ) {
        logger.info("Applying database change set ${changeSet.number}")
        statement.executeUpdate("PRAGMA foreign_keys=OFF")

        changeSet.preCommandsHook(statement)

        for (sql in changeSet.commands())
            statement.executeUpdate(sql)

        changeSet.postCommandsHook(statement)

        updateVersion(versionUpdateStatement, changeSet.number)

        statement.executeUpdate("PRAGMA foreign_key_check")
        statement.executeUpdate("COMMIT")
        statement.executeUpdate("BEGIN TRANSACTION")
        statement.executeUpdate("PRAGMA foreign_keys=ON")
    }

    private fun createBackupName(databaseFile: Path, changeSetNumber: Int): Path {
        val filename = databaseFile.toString()

        val pos = filename.lastIndexOf(".")
        return if (pos > 0)
            Paths.get(filename.substring(0, pos) + "-v" + changeSetNumber + filename.substring(pos))
        else
            Paths.get("$filename-v$changeSetNumber")
    }

    @Throws(SQLException::class)
    private fun updateVersion(versionUpdateStatement: PreparedStatement, version: Int) {
        versionUpdateStatement.setInt(tableVersion.VERSION.queryIndex(), version)
        versionUpdateStatement.executeUpdate()
    }

    @Throws(SQLException::class)
    private fun getVersion(statement: Statement): Int? {
        try {
            val results: ResultSet = statement.executeConfiguredQuery(tableVersion.selectSql())
            return if (results.next())
                results.getInt(tableVersion.VERSION.queryIndex())
            else
                null
        } catch (e: SQLException) {
            val results = statement.executeQuery("SELECT major FROM version")
            if (!results.next())
                return null

            val version = results.getInt(1)
            statement.execute("DROP TABLE version")
            statement.execute(tableVersion.createTableSql())
            statement.executeUpdate("INSERT into ${tableVersion.VERSION.name()} VALUES ($version)")

            return version
        }
    }

    class ConnectionResult(val connection: Connection, val version: Int)
    class UpgradeException(message: String?, cause: Throwable? = null) : Exception(message, cause)
}

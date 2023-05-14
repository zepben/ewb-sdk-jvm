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
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.changesets.*
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet44
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet45
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet46
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet47
import com.zepben.evolve.database.sqlite.upgrade.changesets.changeSet48
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.*
import java.sql.*

@Suppress("SqlResolve")
class UpgradeRunner constructor(
    private val getConnection: (String) -> Connection = DriverManager::getConnection,
    private val getStatement: (Connection) -> Statement = Connection::createStatement,
    private val getPreparedStatement: (Connection, String) -> PreparedStatement = Connection::prepareStatement,
    private val createBackup: (databaseFilename: Path, backupFilename: Path, copyOption: CopyOption) -> Unit = { f, b, o -> Files.copy(f, b, o) },
    internal val changeSets: List<ChangeSet> = listOf(
        changeSet44(),
        changeSet45(),
        changeSet46(),
        changeSet47(),
        changeSet48()
    ),
    private val tableVersion: TableVersion = TableVersion()
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val minimumSupportedVersion = changeSets.firstOrNull()?.let { it.number - 1 } ?: tableVersion.SUPPORTED_VERSION

    @Throws(UpgradeException::class)
    fun connectAndUpgrade(databaseDescriptor: String, databaseFile: Path): ConnectionResult {
        val connection = runCatching { getConnection(databaseDescriptor).configureBatch(getStatement) }
            .getOrElse { throw UpgradeException(it.message, it) }

        return try {
            getStatement(connection).use { statement ->
                upgrade(databaseFile, statement, connection)
            }

            ConnectionResult(connection, tableVersion.SUPPORTED_VERSION)
        } catch (e: Exception) {
            //
            // NOTE: Closing the connection will roll back any active transactions (see https://www.sqlite.org/lang_transaction.html).
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
    private fun upgrade(databaseFile: Path, statement: Statement, connection: Connection) {
        tryRunUpgrade(getVersion(statement)) { databaseVersion ->
            logger.info("Upgrading database '$databaseFile' from v$databaseVersion to v${changeSets.last().number}")
            createBackup(databaseFile, createBackupName(databaseFile, databaseVersion), StandardCopyOption.REPLACE_EXISTING)

            getPreparedStatement(connection, tableVersion.preparedUpdateSql()).use { versionUpdateStatement ->
                changeSets.asSequence()
                    .filter { databaseVersion < it.number }
                    .forEach { runUpgrade(it, statement, versionUpdateStatement) }
            }
        }
    }

    private fun tryRunUpgrade(databaseVersion: Int?, upgradeBlock: (databaseVersion: Int) -> Unit) {
        when {
            databaseVersion == tableVersion.SUPPORTED_VERSION -> return
            databaseVersion == null -> throwMissingVersionException()
            databaseVersion > tableVersion.SUPPORTED_VERSION -> throwFuturisticDatabaseException(databaseVersion)
            databaseVersion < minimumSupportedVersion -> throwUnsupportedUpgradeException(databaseVersion)
            else -> upgradeBlock(databaseVersion)
        }
    }

    @Throws(SQLException::class, SecurityException::class)
    internal fun runUpgrade(
        changeSet: ChangeSet,
        statement: Statement,
        versionUpdateStatement: PreparedStatement
    ) {
        logger.info("Applying database change set ${changeSet.number}")
        statement.executeUpdate("PRAGMA foreign_keys=OFF")

        changeSet.preCommandsHook(statement)

        changeSet.commands.forEach { statement.executeUpdate(it) }

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
        versionUpdateStatement.setInt(tableVersion.VERSION.queryIndex, version)
        versionUpdateStatement.executeUpdate()
    }

    @Throws(SQLException::class)
    private fun getVersion(statement: Statement): Int? =
        runCatching {
            statement.executeConfiguredQuery(tableVersion.selectSql()).use { results ->
                results.getInt(tableVersion.VERSION.queryIndex)
            }
        }.getOrNull()

    private fun throwMissingVersionException() {
        throw UpgradeException(
            "Invalid EWB database detected, unable to read the version number from the database. Please ensure you only have databases " +
                "in the EWB data directory that have been generated by a working migrator."
        )
    }

    private fun throwFuturisticDatabaseException(databaseVersion: Int) {
        throw UpgradeException(
            "Selected database is a newer version [v$databaseVersion] than the supported version [v${tableVersion.SUPPORTED_VERSION}]. Either update the " +
                "version of the server you are using, or downgrade the migrator."
        )
    }

    private fun throwUnsupportedUpgradeException(databaseVersion: Int) {
        throw UpgradeException(
            "Unable to upgrade obsolete database version [v$databaseVersion], upgrading a database before v$minimumSupportedVersion is unsupported. Please " +
                "generate a new database from the source system using an updated migrator."
        )
    }

    class ConnectionResult(val connection: Connection, val version: Int)
    class UpgradeException(message: String?, cause: Throwable? = null) : Exception(message, cause)

}

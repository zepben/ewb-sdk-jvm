/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sql.BaseDatabaseTables
import com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseTables
import com.zepben.ewb.database.sqlite.cim.diagram.DiagramDatabaseTables
import com.zepben.ewb.database.sqlite.cim.network.NetworkDatabaseTables
import com.zepben.ewb.database.sqlite.cim.tables.tableCimVersion
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.NoChanges
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.combined.*
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.customer.ChangeSet50CustomerValidator
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.customer.ChangeSet54CustomerValidator
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.diagram.ChangeSet50DiagramValidator
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.diagram.ChangeSet52DiagramValidator
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.network.*
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory
import org.sqlite.SQLiteException
import java.io.File
import java.sql.Connection
import java.sql.DriverManager.getConnection

@Suppress("SqlResolve", "SameParameterValue")
internal class ChangeSetTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    private val preSplitChangeSetValidators = listOf(
        ChangeSet44Validator,
        ChangeSet45Validator,
        ChangeSet46Validator,
        ChangeSet47Validator,
        ChangeSet48Validator,
        ChangeSet49Validator
    ).associateBy { it.version }

    private val customerChangeSetValidators = listOf(
        ChangeSet50CustomerValidator,
        NoChanges(DatabaseType.CUSTOMER, 51),
        NoChanges(DatabaseType.CUSTOMER, 52),
        NoChanges(DatabaseType.CUSTOMER, 53),
        ChangeSet54CustomerValidator,
        NoChanges(DatabaseType.CUSTOMER, 55),
        NoChanges(DatabaseType.CUSTOMER, 56),
        NoChanges(DatabaseType.CUSTOMER, 57),
        NoChanges(DatabaseType.CUSTOMER, 58),
        NoChanges(DatabaseType.CUSTOMER, 59)
    ).associateBy { it.version }

    private val diagramChangeSetValidators = listOf(
        ChangeSet50DiagramValidator,
        NoChanges(DatabaseType.DIAGRAM, 51),
        ChangeSet52DiagramValidator,
        NoChanges(DatabaseType.DIAGRAM, 53),
        NoChanges(DatabaseType.DIAGRAM, 54),
        NoChanges(DatabaseType.DIAGRAM, 55),
        NoChanges(DatabaseType.DIAGRAM, 56),
        NoChanges(DatabaseType.DIAGRAM, 57),
        NoChanges(DatabaseType.DIAGRAM, 58),
        NoChanges(DatabaseType.DIAGRAM, 59),
    ).associateBy { it.version }

    private val networkChangeSetValidators = listOf(
        ChangeSet50NetworkValidator,
        ChangeSet51NetworkValidator,
        ChangeSet52NetworkValidator,
        ChangeSet53NetworkValidator,
        NoChanges(DatabaseType.NETWORK_MODEL, 54),
        ChangeSet55NetworkValidator,
        ChangeSet56NetworkValidator,
        ChangeSet57NetworkValidator,
        ChangeSet58NetworkValidator,
        ChangeSet59NetworkValidator
    ).associateBy { it.version }

    @Test
    internal fun `test change sets`() {
        // All pre-split change sets are for the network database, as that is the only database that existed.
        validateChangeSets(createBaseCombinedDB(), preSplitChangeSetValidators, UpgradeRunner::preSplitChangeSets, DatabaseType.NETWORK_MODEL)

        validateChangeSets(
            createBaseDB(DatabaseType.CUSTOMER),
            customerChangeSetValidators,
            UpgradeRunner::postSplitChangeSets,
            DatabaseType.CUSTOMER,
            CustomerDatabaseTables()
        )
        validateChangeSets(
            createBaseDB(DatabaseType.DIAGRAM),
            diagramChangeSetValidators,
            UpgradeRunner::postSplitChangeSets,
            DatabaseType.DIAGRAM,
            DiagramDatabaseTables()
        )
        validateChangeSets(
            createBaseDB(DatabaseType.NETWORK_MODEL),
            networkChangeSetValidators,
            UpgradeRunner::postSplitChangeSets,
            DatabaseType.NETWORK_MODEL,
            NetworkDatabaseTables()
        )
    }

    /**
     * Creates an in memory sqlite database using the base combined schema (from version 43).
     */
    private fun createBaseCombinedDB(): Connection =
        createDatabaseFromScript("src/test/data/base-schema.sql")

    /**
     * Creates an in memory sqlite database using the base network schema (from version 49).
     */
    private fun createBaseDB(type: DatabaseType): Connection =
        createDatabaseFromScript("src/test/data/base-${type.fileDescriptor}-schema.sql")

    private fun createDatabaseFromScript(script: String): Connection =
        getConnection("jdbc:sqlite::memory:").apply {
            createStatement().use { statement ->
                File(script).readLines().forEach {
                    statement.executeUpdate(it)
                }
            }
        }

    private fun validateChangeSets(
        conn: Connection,
        changeSetValidators: Map<Int, ChangeSetValidator>,
        changesSets: UpgradeRunner.() -> List<ChangeSet>,
        type: DatabaseType,
        expectedTables: BaseDatabaseTables? = null
    ) {
        val runner = UpgradeRunner()
        val tableVersion = tableCimVersion

        conn.createStatement().use { stmt ->
            conn.prepareStatement(tableVersion.preparedUpdateSql).use { versionUpdateStatement ->
                runner.changesSets().forEach { cs ->

                    val validator = requireNotNull(changeSetValidators[cs.number]) {
                        "Validator for change set ${cs.number} is missing for $type. Have you added a ChangeSetValidator for your latest model update and " +
                            "made sure its version is set correctly?"
                    }

                    logger.info("Preparing for update ${cs.number}.")
                    validator.setUpStatements().forEach {
                        stmt.executeUpdate(it)
                    }

                    stmt.executeUpdate("BEGIN TRANSACTION")
                    stmt.executeUpdate("PRAGMA foreign_keys=ON")
                    runner.runUpgrade(cs, stmt, versionUpdateStatement, type)

                    logger.info("Populating after update ${cs.number}.")
                    validator.populateStatements().forEach {
                        try {
                            stmt.executeUpdate(it)
                        } catch (e: SQLiteException) {
                            throw SQLiteException("Failed executing update error was: ${e.message}\n Query was: $it: ", e.resultCode)
                        }
                    }
                    stmt.executeUpdate("PRAGMA foreign_key_check")
                    stmt.executeUpdate("COMMIT")

                    logger.info("Validating update ${cs.number}.")
                    validator.let { csv ->
                        csv.validate(stmt)
                        csv.tearDownStatements().forEach {
                            stmt.executeUpdate(it)
                        }
                    }
                }
            }
        }

        // We are using the tables rather than the database reader because setting up the reader is more complex than it needs to be
        logger.info("Validating finalized database against expected tables")
        expectedTables?.tables?.forEach {
            conn.prepareStatement(it.value.preparedInsertSql)
        }

        conn.close()
    }

}

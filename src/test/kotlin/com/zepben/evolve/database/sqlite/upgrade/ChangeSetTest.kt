/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.changesets.*
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory
import java.io.File
import java.sql.Connection
import java.sql.DriverManager.getConnection

@Suppress("SqlResolve", "SameParameterValue")
class ChangeSetTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    // Add a ChangeSetValidator here for the corresponding number when testing a new ChangeSet.
    // Please do not use TodoValidator for any new ChangeSets.
    private val changeSetValidators = mapOf(
        20 to ChangeSet20Validator,
        21 to ChangeSet21Validator,
        22 to ChangeSet22Validator,
        23 to ChangeSet23Validator,
        24 to ChangeSet24Validator,
        25 to TodoValidator,
        26 to TodoValidator,
        27 to TodoValidator,
        28 to ChangeSet28Validator,
        29 to TodoValidator,
        30 to ChangeSet30Validator,
        31 to TodoValidator,
        32 to TodoValidator,
        33 to ChangeSet33Validator,
        34 to ChangeSet34Validator,
        35 to ChangeSet35Validator,
        36 to ChangeSet36Validator,
        37 to ChangeSet37Validator,
        38 to ChangeSet38Validator,
        39 to ChangeSet39Validator,
        40 to ChangeSet40Validator,
        41 to ChangeSet41Validator,
        42 to ChangeSet42Validator,
        43 to ChangeSet43Validator
    )

    @Test
    internal fun `test change sets`() {
        val conn = createBaseDB()
        val runner = UpgradeRunner()
        val tableVersion = TableVersion()

        conn.createStatement().use { stmt ->
            conn.prepareStatement(tableVersion.preparedUpdateSql()).use { versionUpdateStatement ->
                runner.changeSets.filter { it.number > 19 }.forEach { cs ->

                    val validator = changeSetValidators[cs.number]
                        ?: throw IllegalStateException("Validator for ${cs.number} missing. Have you added a ChangeSetValidator for your latest model update?")

                    logger.info("Preparing for update ${cs.number}.")
                    validator.setUpStatements().forEach {
                        stmt.executeUpdate(it)
                    }

                    stmt.executeUpdate("BEGIN TRANSACTION")
                    stmt.executeUpdate("PRAGMA foreign_keys=ON")
                    runner.runUpgrade(cs, stmt, versionUpdateStatement)

                    logger.info("Populating after update ${cs.number}.")
                    validator.populateStatements().forEach {
                        stmt.executeUpdate(it)
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
    }

    /**
     * Creates an in memory sqlite database using the base schema (from version 19).
     */
    private fun createBaseDB(): Connection {
        val f = File("src/test/data/base-schema.sql")
        val lines = f.readLines()
        val conn = getConnection("jdbc:sqlite::memory:")
        conn.createStatement().use { statement ->
            lines.forEach {
                statement.executeUpdate(it)
            }
        }
        return conn
    }

}

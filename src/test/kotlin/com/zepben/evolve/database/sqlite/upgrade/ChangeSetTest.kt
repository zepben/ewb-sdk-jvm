/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.database.sqlite.upgrade.changesets.ChangeSet44Validator
import com.zepben.evolve.database.sqlite.upgrade.changesets.ChangeSet45Validator
import com.zepben.evolve.database.sqlite.upgrade.changesets.ChangeSet46Validator
import com.zepben.evolve.database.sqlite.upgrade.changesets.ChangeSet47Validator
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

    private val changeSetValidators = mapOf(
        44 to ChangeSet44Validator,
        45 to ChangeSet45Validator,
        46 to ChangeSet46Validator,
        47 to ChangeSet47Validator
    )

    @Test
    internal fun `test change sets`() {
        val conn = createBaseDB()
        val runner = UpgradeRunner()
        val tableVersion = TableVersion()

        conn.createStatement().use { stmt ->
            conn.prepareStatement(tableVersion.preparedUpdateSql()).use { versionUpdateStatement ->
                runner.changeSets.forEach { cs ->

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
     * Creates an in memory sqlite database using the base schema (from version 43).
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

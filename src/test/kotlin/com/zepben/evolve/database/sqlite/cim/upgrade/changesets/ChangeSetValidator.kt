/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.UpgradeRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.sql.ResultSet
import java.sql.Statement

/**
 * A class for validating a change set.
 *
 * @param databaseType The type of database the changeset should be validated against.
 * @property version The version number of the changeset to validate.
 * @param expectChanges Indicates if the changeset should have any changes.
 */
abstract class ChangeSetValidator(
    private val databaseType: DatabaseType,
    val version: Int,
    private val expectChanges: Boolean = true
) {

    private val description: String by lazy { "$databaseType [$version]" }

    /**
     * Set up prior to applying the ChangeSet.
     * @return A list of statements to run on the database to which the ChangeSet will be applied.
     */
    abstract fun setUpStatements(): List<String>

    /**
     * Populate any tables or fields with data after applying the ChangeSet.
     * @return A list of statements to run on the database to which the ChangeSet has been applied.
     */
    abstract fun populateStatements(): List<String>

    /**
     * Validation after applying the ChangeSet
     * @param statement A statement that can be used to run queries against the database to which the ChangeSet has been applied.
     */
    fun validate(statement: Statement) {
        val changeSet = UpgradeRunner().run { preSplitChangeSets + postSplitChangeSets }.first { it.number == version }

        val totalChanges = changeSet.preCommandHooks.count { databaseType in it.targetDatabases } +
            changeSet.commands.count { databaseType in it.targetDatabases } +
            changeSet.postCommandHooks.count { databaseType in it.targetDatabases }

        if (expectChanges) {
            assertThat(
                "$description: Validator has expected changes but no preCommandHooks, commands or postCommandHooks were found in the change set. " +
                    "Should you be using a NoChanges validator instead?",
                totalChanges,
                greaterThan(0)
            )
        } else {
            assertThat(
                "$description: Validator had no expected changes but preCommandHooks, commands or postCommandHooks were found [$totalChanges]. " +
                    "Have you used a NoChanges validator instead of creating an actual validator?",
                totalChanges,
                equalTo(0)
            )
        }

        validateChanges(statement)
    }

    /**
     * Tear down after validating the ChangeSet.
     * @return A list of statements to remove any data from the database to which the ChangeSet was applied.
     */
    abstract fun tearDownStatements(): List<String>

    /**
     * Validate the changes have actually been applied when applying the ChangeSet
     * @param statement A statement that can be used to run queries against the database to which the ChangeSet has been applied.
     */
    protected abstract fun validateChanges(statement: Statement)

    /**
     * Validate each row returned from a query.
     * @param statement The statement used to run the query.
     * @param sql The query string.
     * @param validators A list of validators, one per expected row in the query.
     */
    protected fun validateRows(statement: Statement, sql: String, vararg validators: (ResultSet) -> Unit) {
        statement.executeQuery(sql).use { rs ->
            validators.forEach {
                assertThat(rs.next(), equalTo(true))
                it(rs)
            }
            assertThat(rs.next(), equalTo(false))
        }
    }

    /**
     * Ensure the presence of indexes in the database.
     *
     * @param statement The statement used to run the check.
     * @param expectedIndexes A list of indexes to check.
     * @param present Indicates if the indexes are expected to be found or not.
     */
    protected fun ensureIndexes(statement: Statement, vararg expectedIndexes: String, present: Boolean = true) {
        expectedIndexes.forEach {
            statement.executeQuery("pragma index_info('$it')").use { rs ->
                assertThat("Does index '$it' exist", rs.next(), equalTo(present))
            }
        }
    }

    /**
     * Ensure the presence of tables in the database.
     *
     * @param statement The statement used to run the check.
     * @param expectedTables A list of tables to check.
     * @param present Indicates if the tables are expected to be found or not.
     */
    protected fun ensureTables(statement: Statement, vararg expectedTables: String, present: Boolean = true) {
        expectedTables.forEach {
            statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$it';").use { rs ->
                assertThat("Does table '$it' exist", rs.next(), equalTo(present))
            }
            if (present) {
                statement.executeQuery("SELECT count(*) FROM $it;").use { rs ->
                    rs.next()
                    assertThat("Table '$it' should still be populated", rs.getInt(1), not(equalTo(0)))
                }
            }
        }
    }

    /**
     * Ensure the presence of columns in a table of the database.
     *
     * @param statement The statement used to run the check.
     * @param table The table containing the columns to check.
     * @param expectedColumns A list of columns to check.
     * @param present Indicates if the columns are expected to be found or not.
     */
    @Suppress("SameParameterValue")
    protected fun ensureColumns(statement: Statement, table: String, vararg expectedColumns: String, present: Boolean = true) {
        statement.executeQuery("pragma table_info('$table')").use { rs ->
            val columns = mutableListOf<String>()
            while (rs.next()) {
                columns.add(rs.getString("name"))
            }
            expectedColumns.forEach {
                assertThat("Does column '$table.$it' exist", it in columns, equalTo(present))
            }
        }
    }

}

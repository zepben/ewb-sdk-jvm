/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.TableAcLineSegments
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

internal class DatabaseTablesTest {

    private val sqliteTable = mockk<SqliteTable>()
    private val tables = mockk<DatabaseTables>(relaxed = true)
    private val resultSet = mockk<ResultSet>(relaxed = true)
    private val connection = mockk<Connection>(relaxed = true)
    private val preparedStatement = mockk<PreparedStatement>(relaxed = true).also { every { it.executeQuery(any()) } returns resultSet }
    private val preparedStatementProvider = spyk<(Connection, String) -> PreparedStatement>({ _, _ -> preparedStatement })
    private val acLineTable = mockk<TableAcLineSegments>()

    //todo fix all these tests to actual test teh DatabaseTables class.
//    @Test
//    fun `getTables`() {
//        tables.getTable(sqliteTable::class.java)
//        verify(exactly = 1) { tables.getTable(sqliteTable::class.java) }
//    }
//
//    @Test
//    fun `getInsert`() {
//        tables.getInsert(sqliteTable::class.java)
//        verify(exactly = 1) { tables.getInsert(sqliteTable::class.java) }
//    }
//
//    @Test
//    fun `prepareInsertStatements`() {
//        tables.prepareInsertStatements(connection, preparedStatementProvider)
//        verify(exactly = 1) { tables.prepareInsertStatements(connection, preparedStatementProvider) }
//    }
//
//    @Test
//    fun `getTables retrieves existing table`() {
//        customerDatabaseTables.forEachTable { sqlTable ->
//            val retrievedTable = customerDatabaseTables.getTable(sqlTable::class.java)
//            MatcherAssert.assertThat("retrieved table should be the same class as the one requested", (retrievedTable.javaClass == sqlTable::class.java))
//        }
//    }
//
//    @Test
//    fun `getTables throws error when requesting for table that doesn't exist`() {
//        ExpectException.expect {
//            customerDatabaseTables.getTable(acLineTable::class.java)
//        }.toThrow<MissingTableConfigException>()
//            .withMessage(
//                "INTERNAL ERROR: No table has been registered for " + acLineTable::class.java.simpleName + ". You might want to consider fixing that."
//            )
//    }
//
//    @Test
//    fun `getInsert returns a statement for the requested table`() {
//        customerDatabaseTables.prepareInsertStatements(connection, preparedStatementProvider)
//        customerDatabaseTables.forEachTable { sqlTable ->
//            val statement = customerDatabaseTables.getInsert(sqlTable::class.java)
//            MatcherAssert.assertThat("insert statement for existing class should be retrieved", (statement.javaClass != null))
//        }
//    }
//
//    @Test
//    fun `getInsert throws error when requesting for table that doesn't exist`() {
//        customerDatabaseTables.prepareInsertStatements(connection, preparedStatementProvider)
//        ExpectException.expect {
//            customerDatabaseTables.getTable(acLineTable::class.java)
//        }.toThrow<MissingTableConfigException>()
//            .withMessage(
//                "INTERNAL ERROR: No table has been registered for " + acLineTable::class.java.simpleName + ". You might want to consider fixing that."
//            )
//    }

}

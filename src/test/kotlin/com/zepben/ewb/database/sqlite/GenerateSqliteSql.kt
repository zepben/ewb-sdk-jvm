/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite

import com.zepben.ewb.database.sql.SqlTable
import com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseTables
import com.zepben.ewb.database.sqlite.cim.diagram.DiagramDatabaseTables
import com.zepben.ewb.database.sqlite.cim.network.NetworkDatabaseTables
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GenerateSqliteSql {

    //
    // NOTE: These tests are deliberately disabled as they simply print the database SQL statements which
    //       just clogs up the test log. You should selectively run them if you need the statements.
    //

    @Test
    @Disabled
    fun `print database create statements`() {
        printStatements { table ->
            println(table.createTableSql + ";")
            table.createIndexesSql.forEach { println("$it;") }
            println("")
        }
    }

    @Test
    @Disabled
    fun `print database select statements`() {
        printStatements { table ->
            println(table.selectSql + ";")
        }
    }

    @Test
    @Disabled
    fun `print database insert statements`() {
        printStatements { table ->
            println(table.preparedInsertSql + ";")
        }
    }

    @Test
    @Disabled
    fun `print database update statements`() {
        printStatements { table ->
            println(table.preparedUpdateSql + ";")
        }
    }

    private fun printStatements(action: (SqlTable) -> Unit) {
        println("******** Customer Database ********")
        println("")
        CustomerDatabaseTables().forEachTable(action)
        println("")
        println("******** Diagram Database ********")
        println("")
        DiagramDatabaseTables().forEachTable(action)
        println("")
        println("******** Network Database ********")
        println("")
        NetworkDatabaseTables().forEachTable(action)
        println("")
    }

}

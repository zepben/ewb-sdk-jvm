/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.zepben.evolve.database.sqlite.customer.customerDatabaseTables
import com.zepben.evolve.database.sqlite.diagram.diagramDatabaseTables
import com.zepben.evolve.database.sqlite.network.networkDatabaseTables
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class GenerateSqliteSql {

    private val logger = LoggerFactory.getLogger(GenerateSqliteSql::class.java)

    @Test
    @Disabled
    fun runThisFunctionAsATestToGetSqlForAnSqliteDatabase() {
        customerDatabaseTables.forEachTable { table: SqliteTable -> printSqlFields(table) }
        networkDatabaseTables.forEachTable { table: SqliteTable -> printSqlFields(table) }
        diagramDatabaseTables.forEachTable { table: SqliteTable -> printSqlFields(table) }
    }

    private fun printSqlFields(table: SqliteTable) {

        logger.info(table.createTableSql() + ";")
        logger.info("")

        for (sql in table.createIndexesSql())
            logger.info("$sql;")
        logger.info("")

        logger.info(table.preparedInsertSql() + ";")
        logger.info("")

        logger.info(table.selectSql() + ";")
        logger.info("")

        logger.info(table.preparedUpdateSql() + ";")
        logger.info("")
    }

}

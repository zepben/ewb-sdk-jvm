/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite;

import com.zepben.cimbend.database.sqlite.tables.SqliteTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.zepben.cimbend.common.interop.JavaLambda.unit;

public class GenerateSqliteSql {

    private static final Logger logger = LoggerFactory.getLogger(GenerateSqliteSql.class);

    @Test
    @Disabled
    public void runThisFunctionAsATestToGetSqlForAnSqliteDatabase() {
        new DatabaseTables().forEachTable(unit(this::printSqlFields));
    }

    private void printSqlFields(SqliteTable table) {
        logger.info(table.createTableSql() + ";");
        logger.info("");

        for (String sql : table.createIndexesSql())
            logger.info(sql + ";");
        logger.info("");

        logger.info(table.preparedInsertSql() + ";");
        logger.info("");

        logger.info(table.selectSql() + ";");
        logger.info("");

        logger.info(table.preparedUpdateSql() + ";");
        logger.info("");
    }

}

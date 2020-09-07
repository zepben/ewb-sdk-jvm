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

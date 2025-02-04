/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

internal class SchemaUtils(
    private val databaseTables: BaseDatabaseTables,
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun createSchema(connection: Connection): Boolean =
        try {
            val versionTable = databaseTables.getTable<TableVersion>()
            logger.info("Creating database schema v${versionTable.supportedVersion}...")

            connection.createStatement().use { statement ->
                statement.queryTimeout = 2

                databaseTables.forEachTable {
                    statement.executeUpdate(it.createTableSql)
                }

                // Add the version number to the database.
                connection.prepareStatement(versionTable.preparedInsertSql).use { insert ->
                    insert.setInt(versionTable.VERSION.queryIndex, versionTable.supportedVersion)
                    insert.executeUpdate()
                }

                connection.commit()
                logger.info("Schema created.")
            }
            true
        } catch (e: SQLException) {
            logger.error("Failed to create database schema: " + e.message)
            false
        }

    fun getVersion(connection: Connection): Int? =
        connection.createStatement().use { statement ->
            val tableVersion = databaseTables.getTable<TableVersion>()
            try {
                statement.executeQuery(tableVersion.selectSql).use { rs ->
                    if (rs.next()) {
                        rs.getInt(tableVersion.VERSION.queryIndex)
                    } else null
                }
            } catch (e: SQLException) {
                null
            }
        }

}

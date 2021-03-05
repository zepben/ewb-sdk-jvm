/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database

import com.zepben.evolve.database.sqlite.DatabaseTables
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.tables.TableVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Output an SQL file of the schema for the current version to be used in tests.
 * Will save a new file as src/test/data/changeset<TableVersion.SUPPORTED_VERSION+1>.sql
 * with SUPPORTED_VERSIONs schema (including populating the version table with the current version)
 *
 * You can then modify this sql file with your required test data and use it via [com.zepben.evolve.database.sqlite.upgrade.ChangeSetTest.sqlDumpToDB]
 */
fun main(args: Array<String>) {
    if (args.isNotEmpty())
        create(args[1])
    else
        create()
}

val logger: Logger = LoggerFactory.getLogger("TestSchemaGenerator")

val databaseTables = DatabaseTables()

private fun create(url: String = "jdbc:sqlite::memory:"): Boolean {
    try {
        val versionTable = databaseTables.getTable(TableVersion::class.java)
        logger.info("Creating database schema v${versionTable.SUPPORTED_VERSION}")

        DriverManager.getConnection(url).use { c ->
            c.configureBatch(Connection::createStatement).createStatement().use { statement ->
                statement.queryTimeout = 2

                databaseTables.forEachTable { statement.executeUpdate(it.createTableSql()) }

                // Add the version number to the database.
                c.prepareStatement(versionTable.preparedInsertSql()).use { insert ->
                    insert.setInt(versionTable.VERSION.queryIndex, versionTable.SUPPORTED_VERSION)
                    insert.executeUpdate()
                }

                c.commit()

                val f = File("src/test/data/changeset${versionTable.SUPPORTED_VERSION}.sql")
                f.createNewFile()
                f.writer().use { writer ->
                    val rs = statement.executeQuery("select sql from sqlite_master")
                    while (rs.next()) {
                        writer.appendLine(rs.getString(1))
                        logger.info(rs.getString(1))
                    }
                    writer.appendLine("INSERT INTO version (version) VALUES (${versionTable.SUPPORTED_VERSION-1})")
                }
            }
        }
    } catch (e: SQLException) {
        logger.error("Failed to create database schema: " + e.message)
        return false
    }
    return true
}


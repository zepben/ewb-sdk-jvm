/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql.tables

import com.zepben.evolve.database.sql.extensions.executeConfiguredQuery
import java.sql.Connection
import java.sql.SQLException

/**
 * Interface representing a `version` table that should be implemented by DBMS specific version tables.
 * This allows for common operations between DBMSs, primarily checking the version in the version table.
 *
 * @property VERSION Column definition.
 * @property selectSql The select statement to read the version number.
 * @property supportedVersion The version number of the schema supported by the code.
 */
@Suppress("PropertyName")
interface TableVersion {

    val VERSION: Column

    val selectSql: String
    val supportedVersion: Int

    /**
     * Helper function to read the version from the database.
     */
    @Throws(SQLException::class)
    fun getVersion(connection: Connection): Int? =
        connection.prepareStatement(selectSql).use { statement ->
            runCatching {
                statement.executeConfiguredQuery().use { results ->
                    results.next()
                    results.getInt(VERSION.queryIndex)
                }
            }.getOrNull()
        }

}

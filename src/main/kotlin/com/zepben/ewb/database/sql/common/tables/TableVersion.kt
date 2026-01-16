/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.common.tables

import com.zepben.ewb.database.sql.extensions.executeConfiguredQuery
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
class TableVersion(
    val supportedVersion: Int
) : SqlTable() {

    val VERSION: Column = Column(++columnIndex, "version", Column.Type.STRING, Column.Nullable.NOT_NULL)

    override val name: String = "version"

    /**
     * Helper function to read the version from the database.
     *
     * @param connection The [Connection] to the database.
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

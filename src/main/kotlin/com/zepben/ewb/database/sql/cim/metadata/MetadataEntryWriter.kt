/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.metadata

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.cim.tables.TableMetadataDataSources
import com.zepben.ewb.database.sql.common.BaseEntryWriter
import com.zepben.ewb.database.sql.extensions.setInstant
import com.zepben.ewb.services.common.meta.DataSource
import com.zepben.ewb.services.common.meta.MetadataCollection

/**
 * A class for reading the [MetadataCollection] entries from the database.
 *
 * @param databaseTables The tables that are available in the database.
 */
internal class MetadataEntryWriter(
    val databaseTables: CimDatabaseTables
) : BaseEntryWriter() {

    /**
     * Write the [DataSource] fields to [TableMetadataDataSources].
     *
     * @param dataSource The [DataSource] to write to the database.
     *
     * @return true if the [DataSource] is successfully written to the database, otherwise false.
     */
    fun write(dataSource: DataSource): Boolean {
        val table = databaseTables.getTable<TableMetadataDataSources>()
        val insert = databaseTables.getInsert<TableMetadataDataSources>()

        insert.setString(table.SOURCE.queryIndex, dataSource.source)
        insert.setString(table.VERSION.queryIndex, dataSource.version)
        insert.setInstant(table.TIMESTAMP.queryIndex, dataSource.timestamp)

        return insert.tryExecuteSingleUpdate("data source")
    }

}

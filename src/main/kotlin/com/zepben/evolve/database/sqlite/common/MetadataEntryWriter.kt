/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.extensions.logFailure
import com.zepben.evolve.database.sqlite.extensions.setInstant
import com.zepben.evolve.database.sqlite.extensions.tryExecuteSingleUpdate
import com.zepben.evolve.database.sqlite.tables.TableMetadataDataSources
import com.zepben.evolve.services.common.meta.DataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MetadataEntryWriter(val databaseTables: BaseDatabaseTables) : BaseWriter() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun save(dataSource: DataSource): Boolean {
        val table = databaseTables.getTable<TableMetadataDataSources>()
        val insert = databaseTables.getInsert<TableMetadataDataSources>()

        insert.setString(table.SOURCE.queryIndex, dataSource.source)
        insert.setString(table.VERSION.queryIndex, dataSource.version)
        insert.setInstant(table.TIMESTAMP.queryIndex, dataSource.timestamp)

        return insert.tryExecuteSingleUpdate { insert.logFailure(logger, "data source") }
    }

}

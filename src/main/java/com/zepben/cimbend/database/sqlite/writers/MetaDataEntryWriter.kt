/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.common.meta.DataSource
import com.zepben.cimbend.database.sqlite.DatabaseTables
import com.zepben.cimbend.database.sqlite.extensions.setInstant
import com.zepben.cimbend.database.sqlite.tables.TableMetadataDataSources
import com.zepben.cimbend.database.sqlite.writers.WriteValidator.logFailure
import com.zepben.cimbend.database.sqlite.writers.WriteValidator.tryExecuteSingleUpdate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MetaDataEntryWriter(private val databaseTables: DatabaseTables) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun save(dataSource: DataSource): Boolean {
        val table = databaseTables.getTable(TableMetadataDataSources::class.java)
        val insert = databaseTables.getInsert(TableMetadataDataSources::class.java)

        insert.setString(table.SOURCE.queryIndex, dataSource.source)
        insert.setString(table.VERSION.queryIndex, dataSource.version)
        insert.setInstant(table.TIMESTAMP.queryIndex, dataSource.timestamp)

        return tryExecuteSingleUpdate(insert) { logFailure(logger, insert, "data source") }
    }

}

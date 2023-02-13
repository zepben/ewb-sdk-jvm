/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.TableMetadataDataSources
import com.zepben.evolve.services.common.meta.MetadataCollection
import java.sql.Statement


/**
 * Class for reading the [MetadataCollection] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class MetadataCollectionReader(
    databaseTables: DatabaseTables,
    val reader: MetadataEntryReader,
    getStatement: () -> Statement,
) : BaseCollectionReader(databaseTables, getStatement) {

    override fun load(): Boolean {
        var status = true

        status = status and loadEach("metadata data sources", TableMetadataDataSources(), reader::load)

        return status
    }

}

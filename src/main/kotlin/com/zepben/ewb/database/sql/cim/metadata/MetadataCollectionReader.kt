/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.metadata

import com.zepben.ewb.database.sql.cim.tables.TableMetadataDataSources
import com.zepben.ewb.database.sql.common.BaseCollectionReader
import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.services.common.meta.MetadataCollection
import java.sql.Connection

/**
 * Class for reading the [MetadataCollection] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection The [Connection] to the database.
 * @param reader The [MetadataEntryReader] used to read from the database.
 */
internal class MetadataCollectionReader(
    databaseTables: BaseDatabaseTables,
    connection: Connection,
    private val reader: MetadataEntryReader = MetadataEntryReader(),
) : BaseCollectionReader<MetadataCollection>(databaseTables, connection) {

    override fun read(data: MetadataCollection): Boolean =
        readEach<TableMetadataDataSources>(data, reader::read)

}

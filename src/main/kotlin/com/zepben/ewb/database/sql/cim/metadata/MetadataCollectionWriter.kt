/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.metadata

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.common.BaseCollectionWriter
import com.zepben.ewb.services.common.meta.MetadataCollection

/**
 * Class for writing the [MetadataCollection] to the database.
 *
 * @param databaseTables The tables available in the database.
 * @param writer The [MetadataEntryWriter] used to populate the database.
 */
internal class MetadataCollectionWriter(
    databaseTables: CimDatabaseTables,
    private val writer: MetadataEntryWriter = MetadataEntryWriter(databaseTables)
) : BaseCollectionWriter<MetadataCollection>() {

    override fun write(data: MetadataCollection): Boolean = data.run {
        @Suppress("Destructure")
        writeEach(dataSources, writer::write) { ds, e -> logger.error("Failed to write DataSource '${ds.source}': ${e.message}") }
    }

}

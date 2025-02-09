/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.metadata

import com.zepben.evolve.database.sqlite.cim.CimDatabaseTables
import com.zepben.evolve.database.sqlite.common.BaseCollectionWriter
import com.zepben.evolve.services.common.meta.MetadataCollection

/**
 * Class for writing the [MetadataCollection] to the database.
 *
 * @param databaseTables The tables available in the database.
 */
internal class MetadataCollectionWriter(
    databaseTables: CimDatabaseTables,
    private val writer: MetadataEntryWriter = MetadataEntryWriter(databaseTables)
) : BaseCollectionWriter<MetadataCollection>() {

    override fun write(data: MetadataCollection): Boolean = data.run {
        writeEach(dataSources, writer::write) { it, e -> logger.error("Failed to write DataSource '${it.source}': ${e.message}") }
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.metadata

import com.zepben.evolve.database.sqlite.cim.tables.TableMetadataDataSources
import com.zepben.evolve.database.sqlite.extensions.getInstant
import com.zepben.evolve.services.common.meta.DataSource
import com.zepben.evolve.services.common.meta.MetadataCollection
import java.sql.ResultSet
import java.time.Instant

/**
 * A class for reading the [MetadataCollection] entries from the database.
 */
internal class MetadataEntryReader {

    /**
     * Populate the [DataSource] fields from [TableMetadataDataSources].
     *
     * @param metadata The [MetadataCollection] to populate from the database.
     * @param table The database table to read the [DataSource] fields from.
     * @param resultSet The record in the database table containing the fields for this [DataSource].
     * @param setIdentifier A callback to set the identifier of the current row for logging purposes, which returns a copy of the provided string for
     *   fluent use.
     *
     * @return true if the [DataSource] is successfully read from the database, otherwise false.
     */
    fun read(metadata: MetadataCollection, table: TableMetadataDataSources, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val dataSource = DataSource(
            setIdentifier(resultSet.getString(table.SOURCE.queryIndex)),
            resultSet.getString(table.VERSION.queryIndex),
            resultSet.getInstant(table.TIMESTAMP.queryIndex) ?: Instant.EPOCH
        )

        return metadata.add(dataSource)
    }

}

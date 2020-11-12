/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.common.meta.DataSource
import com.zepben.cimbend.common.meta.MetadataCollection
import com.zepben.cimbend.database.sqlite.extensions.getInstant
import com.zepben.cimbend.database.sqlite.tables.TableMetadataDataSources
import java.sql.ResultSet
import java.time.Instant

class MetadataEntryReader(private val metadataCollection: MetadataCollection) {

    fun load(table: TableMetadataDataSources, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val dataSource = DataSource(
            setLastMRID(resultSet.getString(table.SOURCE.queryIndex)),
            resultSet.getString(table.VERSION.queryIndex),
            resultSet.getInstant(table.TIMESTAMP.queryIndex) ?: Instant.EPOCH
        )

        return metadataCollection.add(dataSource)
    }

}

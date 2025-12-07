/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.SqlTable

@Suppress("PropertyName")
abstract class TableDataSets : SqlTable() {

    val MRID: Column = Column(++columnIndex, "mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, Column.Nullable.NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, Column.Nullable.NULL)

    init {
        addUniqueIndexes(
            listOf(MRID)
        )

        addNonUniqueIndexes(
            listOf(NAME)
        )
    }
}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `NameType` columns required for the database table.
 *
 * @property NAME Name of the name type.
 * @property DESCRIPTION Description of the name type.
 */
@Suppress("PropertyName")
class TableNameTypes : SqlTable() {

    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NULL)

    override val name: String = "name_types"

    init {
        addUniqueIndexes(
            listOf(NAME)
        )
    }

}

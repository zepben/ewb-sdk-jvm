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
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `Name` columns required for the database table.
 *
 * @property NAME Any free text that name the object.
 * @property IDENTIFIED_OBJECT_MRID Identified object that this name designates.
 * @property NAME_TYPE_NAME Type of this name.
 */
@Suppress("PropertyName")
class TableNames : SqlTable() {

    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NOT_NULL)
    val IDENTIFIED_OBJECT_MRID: Column = Column(++columnIndex, "identified_object_mrid", Column.Type.STRING, NOT_NULL)
    val NAME_TYPE_NAME: Column = Column(++columnIndex, "name_type_name", Column.Type.STRING, NOT_NULL)

    override val name: String = "names"

    init {
        addUniqueIndexes(
            listOf(IDENTIFIED_OBJECT_MRID, NAME_TYPE_NAME, NAME)
        )

        addNonUniqueIndexes(
            listOf(IDENTIFIED_OBJECT_MRID),
            listOf(NAME),
            listOf(NAME_TYPE_NAME)
        )
    }

}

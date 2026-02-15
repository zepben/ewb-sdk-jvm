/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the ChangeSetMember columns required for the database table.
 *
 * @property CHANGE_SET_MRID A column storing the mRID of the ChangeSet this member belongs to.
 * @property TARGET_OBJECT_MRID A column storing the registered CIM object affected by this changeset.
 */
@Suppress("PropertyName")
abstract class TableChangeSetMembers : SqlTable() {

    val CHANGE_SET_MRID: Column = Column(++columnIndex, "change_set_mrid", STRING, NOT_NULL)
    val TARGET_OBJECT_MRID: Column = Column(++columnIndex, "target_object_mrid", STRING, NOT_NULL)

    init {
        addUniqueIndexes(listOf(CHANGE_SET_MRID, TARGET_OBJECT_MRID))

        addNonUniqueIndexes(
            listOf(TARGET_OBJECT_MRID),
            listOf(CHANGE_SET_MRID)
        )
    }
}

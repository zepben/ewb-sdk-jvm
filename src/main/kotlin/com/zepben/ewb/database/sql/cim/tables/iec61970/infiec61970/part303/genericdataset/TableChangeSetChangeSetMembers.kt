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

/**
 * A class representing the association between ChangeSet and ChangeSetMember.
 *
 * @property CHANGE_SET_MRID A column storing the mRID of ChangeSet.
 * @property CHANGE_SET_MEMBER_MRID A column storing the mRID of ChangeSetMember.
 */
@Suppress("PropertyName")
class TableChangeSetChangeSetMembers : SqlTable() {

    val CHANGE_SET_MRID: Column = Column(++columnIndex, "change_set_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val CHANGE_SET_MEMBER_MRID: Column = Column(++columnIndex, "change_set_member_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)

    override val name: String = "change_set_change_set_members"

    init {
        addUniqueIndexes(
            listOf(CHANGE_SET_MRID, CHANGE_SET_MEMBER_MRID)
        )

        addNonUniqueIndexes(
            listOf(CHANGE_SET_MRID),
            listOf(CHANGE_SET_MEMBER_MRID)
        )
    }

}

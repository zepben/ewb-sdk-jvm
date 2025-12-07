/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.database.sql.Column

@Suppress("PropertyName")
class TableObjectReverseModifications : TableChangeSetMembers() {

    val OBJECT_MODIFICATION_MRID: Column = Column(++columnIndex, "object_modification_mrid", Column.Type.STRING, Column.Nullable.NULL)

    override val name: String = "object_reverse_modifications"

    init {
        addNonUniqueIndexes(
            listOf(TARGET_OBJECT_MRID),
            listOf(CHANGE_SET_MRID)
        )
    }

}

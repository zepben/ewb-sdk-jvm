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

/**
 * A class representing the ObjectModification columns required for the database table.
 *
 * @property OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID A column storing the mRID of the expected IdentifiedObject in the base network before modification.
 */
@Suppress("PropertyName")
class TableObjectModifications : TableChangeSetMembers() {

    val OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID: Column = Column(++columnIndex, "object_reverse_modification_mrid", STRING, NOT_NULL)

    override val name: String = "object_modifications"

    init {
        addNonUniqueIndexes(listOf(OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID))
    }

}

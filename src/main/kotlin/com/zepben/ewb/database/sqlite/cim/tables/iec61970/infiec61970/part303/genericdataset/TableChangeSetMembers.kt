/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Type.STRING
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
abstract class TableChangeSetMembers : SqliteTable() {

    val CHANGE_SET_MRID: Column = Column(++columnIndex, "change_set_mrid", STRING, NOT_NULL)
    val TARGET_OBJECT_MRID: Column = Column(++columnIndex, "target_object_mrid", STRING, NOT_NULL)

}

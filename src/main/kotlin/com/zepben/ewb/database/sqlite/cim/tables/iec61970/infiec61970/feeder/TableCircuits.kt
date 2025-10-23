/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.feeder

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires.TableLines

@Suppress("PropertyName")
class TableCircuits : TableLines() {

    val LOOP_MRID: Column = Column(++columnIndex, "loop_mrid", Column.Type.STRING, NULL)

    override val name: String = "circuits"

    init {
        addNonUniqueIndexes(
            listOf(LOOP_MRID)
        )
    }

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires.TableLines
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Circuit` columns required for the database table.
 *
 * @property LOOP_MRID The [Loop] containing this [Circuit].
 */
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

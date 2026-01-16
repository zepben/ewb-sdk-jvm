/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `cut` columns required for the database table.
 *
 * @property LENGTH_FROM_TERMINAL_1 The length to the place where the cut is located starting from side one of the cut line segment, i.e. the
 * line segment Terminal with sequenceNumber equal to 1.
 * @property AC_LINE_SEGMENT_MRID The line segment to which the cut is applied.
 */
@Suppress("PropertyName")
class TableCuts : TableSwitches() {

    val LENGTH_FROM_TERMINAL_1: Column = Column(++columnIndex, "length_from_terminal_1", Column.Type.DOUBLE, NULL)
    val AC_LINE_SEGMENT_MRID: Column = Column(++columnIndex, "ac_line_segment_mrid", Column.Type.STRING, NULL)

    override val name: String = "cuts"

    init {
        addNonUniqueIndexes(
            listOf(AC_LINE_SEGMENT_MRID)
        )
    }

}

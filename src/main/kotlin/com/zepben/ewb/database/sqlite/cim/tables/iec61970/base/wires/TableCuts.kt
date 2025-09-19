/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

/**
 * A class representing the `cut` columns required for the database table.
 *
 * @property LENGTH_FROM_TERMINAL_1 A column storing the length to the place where the cut is located starting from side one of the cut line segment, i.e. the
 * line segment Terminal with sequenceNumber equal to 1.
 * @property AC_LINE_SEGMENT_MRID A column storing the line segment to which the cut is applied.
 */
@Suppress("PropertyName")
class TableCuts : TableSwitches() {

    val LENGTH_FROM_TERMINAL_1: Column = Column(++columnIndex, "length_from_terminal_1", "NUMBER", NULL)
    val AC_LINE_SEGMENT_MRID: Column = Column(++columnIndex, "ac_line_segment_mrid", "TEXT", NULL)

    override val name: String = "cuts"

    init {
        addNonUniqueIndexes(
            listOf(AC_LINE_SEGMENT_MRID)
        )
    }

}

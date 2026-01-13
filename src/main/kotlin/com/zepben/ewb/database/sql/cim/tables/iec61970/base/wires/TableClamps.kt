/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableConductingEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `clamp` columns required for the database table.
 *
 * @property LENGTH_FROM_TERMINAL_1 The length to the place where the clamp is located starting from side one of the line segment, i.e. the
 * line segment terminal with sequence number equal to 1.
 * @property AC_LINE_SEGMENT_MRID The line segment to which the clamp is connected.
 */
@Suppress("PropertyName")
class TableClamps : TableConductingEquipment() {

    val LENGTH_FROM_TERMINAL_1: Column = Column(++columnIndex, "length_from_terminal_1", Column.Type.DOUBLE, NULL)
    val AC_LINE_SEGMENT_MRID: Column = Column(++columnIndex, "ac_line_segment_mrid", Column.Type.STRING, NULL)

    override val name: String = "clamps"

    init {
        addNonUniqueIndexes(
            listOf(AC_LINE_SEGMENT_MRID)
        )
    }

}

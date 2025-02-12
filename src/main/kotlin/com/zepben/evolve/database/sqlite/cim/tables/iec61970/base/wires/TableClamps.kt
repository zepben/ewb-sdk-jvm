/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableConductingEquipment

/**
 * A class representing the `clamp` columns required for the database table.
 *
 * @property LENGTH_FROM_TERMINAL_1 A column storing the length to the place where the clamp is located starting from side one of the line segment, i.e. the
 * line segment terminal with sequence number equal to 1.
 * @property AC_LINE_SEGMENT_MRID A column storing the line segment to which the clamp is connected.
 */
@Suppress("PropertyName")
class TableClamps : TableConductingEquipment() {

    val LENGTH_FROM_TERMINAL_1: Column = Column(++columnIndex, "length_from_terminal_1", "NUMBER", NULL)
    val AC_LINE_SEGMENT_MRID: Column = Column(++columnIndex, "ac_line_segment_mrid", "TEXT", NULL)

    override val name: String = "clamps"

    override val nonUniqueIndexColumns: MutableList<List<Column>> = mutableListOf(
        listOf(AC_LINE_SEGMENT_MRID)
    )

}

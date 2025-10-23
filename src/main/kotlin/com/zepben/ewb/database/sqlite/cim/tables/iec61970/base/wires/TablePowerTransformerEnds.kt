/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TablePowerTransformerEnds : TableTransformerEnds() {

    val POWER_TRANSFORMER_MRID: Column = Column(++columnIndex, "power_transformer_mrid", Column.Type.STRING, NULL)
    val CONNECTION_KIND: Column = Column(++columnIndex, "connection_kind", Column.Type.STRING, NOT_NULL)
    val PHASE_ANGLE_CLOCK: Column = Column(++columnIndex, "phase_angle_clock", Column.Type.INTEGER, NULL)
    val B: Column = Column(++columnIndex, "b", Column.Type.DOUBLE, NULL)
    val B0: Column = Column(++columnIndex, "b0", Column.Type.DOUBLE, NULL)
    val G: Column = Column(++columnIndex, "g", Column.Type.DOUBLE, NULL)
    val G0: Column = Column(++columnIndex, "g0", Column.Type.DOUBLE, NULL)
    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "r0", Column.Type.DOUBLE, NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", Column.Type.INTEGER, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "x0", Column.Type.DOUBLE, NULL)

    override val name: String = "power_transformer_ends"

    init {
        addUniqueIndexes(
            listOf(POWER_TRANSFORMER_MRID, END_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(POWER_TRANSFORMER_MRID)
        )
    }

}

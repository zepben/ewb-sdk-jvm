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
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
class TablePhaseImpedanceData : SqliteTable() {

    val PER_LENGTH_PHASE_IMPEDANCE_MRID: Column = Column(++columnIndex, "per_length_phase_impedance_mrid", Column.Type.STRING, NOT_NULL)
    val FROM_PHASE: Column = Column(++columnIndex, "from_phase", Column.Type.STRING, NOT_NULL)
    val TO_PHASE: Column = Column(++columnIndex, "to_phase", Column.Type.STRING, NOT_NULL)
    val B: Column = Column(++columnIndex, "b", Column.Type.DOUBLE, NULL)
    val G: Column = Column(++columnIndex, "g", Column.Type.DOUBLE, NULL)
    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)

    override val name: String = "phase_impedance_data"

    init {
        addUniqueIndexes(
            listOf(PER_LENGTH_PHASE_IMPEDANCE_MRID, FROM_PHASE, TO_PHASE)
        )

        addNonUniqueIndexes(
            listOf(PER_LENGTH_PHASE_IMPEDANCE_MRID)
        )
    }

}

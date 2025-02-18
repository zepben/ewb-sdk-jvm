/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
class TablePhaseImpedanceData : SqliteTable() {

    val PER_LENGTH_PHASE_IMPEDANCE_MRID: Column = Column(++columnIndex, "per_length_phase_impedance_mrid", "TEXT", NOT_NULL)
    val FROM_PHASE: Column = Column(++columnIndex, "from_phase", "TEXT", NOT_NULL)
    val TO_PHASE: Column = Column(++columnIndex, "to_phase", "TEXT", NOT_NULL)
    val B: Column = Column(++columnIndex, "b", "NUMBER", NULL)
    val G: Column = Column(++columnIndex, "g", "NUMBER", NULL)
    val R: Column = Column(++columnIndex, "r", "NUMBER", NULL)
    val X: Column = Column(++columnIndex, "x", "NUMBER", NULL)

    override val name: String = "phase_impedance_data"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(PER_LENGTH_PHASE_IMPEDANCE_MRID, FROM_PHASE, TO_PHASE))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(PER_LENGTH_PHASE_IMPEDANCE_MRID))
        }
}

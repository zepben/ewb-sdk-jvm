/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `PhaseImpedanceData` columns required for the database table.
 *
 * @property PER_LENGTH_PHASE_IMPEDANCE_MRID The PerLengthPhaseImpedance this data is for.
 * @property FROM_PHASE Refer to the class description.
 * @property TO_PHASE Refer to the class description.
 * @property B Susceptance matrix element value, per length of unit.
 * @property G Conductance matrix element value, per length of unit.
 * @property R Resistance matrix element value, per length of unit.
 * @property X Reactance matrix element value, per length of unit.
 */
@Suppress("PropertyName")
class TablePhaseImpedanceData : SqlTable() {

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

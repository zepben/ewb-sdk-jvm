/**
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TablePowerSystemResources
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `AcLineSegmentPhase` columns required for the database table.
 *
 * @property AC_LINE_SEGMENT_MRID The line segment mRID to which the phase belongs.
 * @property PHASE The phase connection of the wire at both ends.
 * @property SEQUENCE_NUMBER Number designation for this line segment phase. Each line segment phase within a line segment should have a unique sequence number. This is useful for unbalanced modelling to bind the mathematical model (PhaseImpedanceData of PerLengthPhaseImpedance) with the connectivity model (this class) and the physical model (WirePosition) without tight coupling.
 * @property WIRE_INFO_MRID The wire info mRID for this phase of the AcLineSegment
 */
@Suppress("PropertyName")
class TableAcLineSegmentPhases : TablePowerSystemResources() {

    val AC_LINE_SEGMENT_MRID: Column = Column(++columnIndex, "ac_line_segment_mrid", Column.Type.STRING, NULL)
    val PHASE: Column = Column(++columnIndex, "phase", Column.Type.STRING, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NULL)
    val WIRE_INFO_MRID: Column = Column(++columnIndex, "wire_info_mrid", Column.Type.STRING, NULL)

    override val name: String = "ac_line_segment_phases"

    init {
        addUniqueIndexes(
            listOf(AC_LINE_SEGMENT_MRID, PHASE)
        )

        addNonUniqueIndexes(
            listOf(AC_LINE_SEGMENT_MRID),
            listOf(WIRE_INFO_MRID)
        )
    }

}

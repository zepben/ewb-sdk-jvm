/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TransformerEnd` columns required for the database table.
 *
 * @property GROUNDED (for Yn and Zn connections) True if the neutral is solidly grounded.
 * @property R_GROUND (for Yn and Zn connections) Resistance part of neutral impedance where 'grounded' is true
 * @property X_GROUND (for Yn and Zn connections) Reactive part of neutral impedance where 'grounded' is true
 * @property BASE_VOLTAGE_MRID Base voltage of the transformer end.  This is essential for PU calculation.
 * @property TERMINAL_MRID The terminal of the transformer that this end is associated with
 * @property END_NUMBER Number for this transformer end, corresponding to the end's order in the power transformer vector group or phase angle clock number.
 * Highest voltage winding should be 1. Each end within a power transformer should have a unique subsequent end number. Note the transformer end number need not
 * match the terminal sequence number.
 * @property STAR_IMPEDANCE_MRID (accurate for 2- or 3-winding transformers only) Pi-model impedances of this transformer end. By convention, for a two winding
 * transformer, the full values of the transformer should be entered on the high voltage end (endNumber=1).
 */
@Suppress("PropertyName")
abstract class TableTransformerEnds : TableIdentifiedObjects() {

    val END_NUMBER: Column = Column(++columnIndex, "end_number", Column.Type.INTEGER, NOT_NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)
    val BASE_VOLTAGE_MRID: Column = Column(++columnIndex, "base_voltage_mrid", Column.Type.STRING, NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", Column.Type.BOOLEAN, NULL)
    val R_GROUND: Column = Column(++columnIndex, "r_ground", Column.Type.DOUBLE, NULL)
    val X_GROUND: Column = Column(++columnIndex, "x_ground", Column.Type.DOUBLE, NULL)
    val STAR_IMPEDANCE_MRID: Column = Column(++columnIndex, "star_impedance_mrid", Column.Type.STRING, NULL)

    init {
        addNonUniqueIndexes(
            listOf(STAR_IMPEDANCE_MRID)
        )
    }

}

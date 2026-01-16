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

/**
 * A class representing the `PowerTransformerEnd` columns required for the database table.
 *
 * @property POWER_TRANSFORMER_MRID The power transformer of this power transformer end.
 * @property B Magnetizing branch susceptance (B mag).  The value can be positive or negative.
 * @property B0 Zero sequence magnetizing branch susceptance.
 * @property CONNECTION_KIND Kind of connection.
 * @property G Magnetizing branch conductance.
 * @property G0 Zero sequence magnetizing branch conductance (star-model).
 * @property PHASE_ANGLE_CLOCK Terminal voltage phase angle displacement where 360 degrees are represented with clock hours. The valid values
 *                           are 0 to 11. For example, for the secondary side end of a transformer with vector group code of 'Dyn11', specify the
 *                           connection kind as wye with neutral and specify the phase angle of the clock as 11.  The clock value of the transformer
 *                           end number specified as 1, is assumed to be zero.
 * @property R Resistance (star-model) of the transformer end in ohms. The attribute shall be equal or greater than zero for non-equivalent transformers.
 *             Do not read this directly, use [resistanceReactance().r] instead.
 * @property R0 Zero sequence series resistance (star-model) of the transformer end in ohms. Do not read this directly, use [resistanceReactance().r0] instead.
 * the high and low voltage sides shall be identical.
 * @property RATED_U  Rated voltage: phase-phase for three-phase windings, and either phase-phase or phase-neutral for single-phase windings.
 *                   A high voltage side, as given by TransformerEnd.endNumber, shall have a ratedU that is greater or equal than ratedU
 *                   for the lower voltage sides.
 * @property X Positive sequence series reactance (star-model) of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x] instead.
 * @property X0 Zero sequence series reactance of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x0] instead.
 */
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

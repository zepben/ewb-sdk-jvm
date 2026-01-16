/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PerLengthSequenceImpedance` columns required for the database table.
 *
 * @property R Positive sequence series resistance, per unit of length.
 * @property X Positive sequence series reactance, per unit of length.
 * @property BCH Positive sequence shunt (charging) susceptance, per unit of length.
 * @property GCH Positive sequence shunt (charging) conductance, per unit of length.
 * @property R0 Zero sequence series resistance, per unit of length.
 * @property X0 Zero sequence series reactance, per unit of length.
 * @property B0CH Zero sequence shunt (charging) susceptance, per unit of length.
 * @property G0CH Zero sequence shunt (charging) conductance, per unit of length.
 */
@Suppress("PropertyName")
class TablePerLengthSequenceImpedances : TablePerLengthImpedances() {

    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "r0", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "x0", Column.Type.DOUBLE, NULL)
    val BCH: Column = Column(++columnIndex, "bch", Column.Type.DOUBLE, NULL)
    val GCH: Column = Column(++columnIndex, "gch", Column.Type.DOUBLE, NULL)
    val B0CH: Column = Column(++columnIndex, "b0ch", Column.Type.DOUBLE, NULL)
    val G0CH: Column = Column(++columnIndex, "g0ch", Column.Type.DOUBLE, NULL)

    override val name: String = "per_length_sequence_impedances"

}

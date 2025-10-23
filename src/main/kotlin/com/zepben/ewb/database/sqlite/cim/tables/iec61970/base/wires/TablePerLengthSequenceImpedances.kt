/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

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

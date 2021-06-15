/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TablePerLengthSequenceImpedances : TablePerLengthImpedances() {

    val R = Column(++columnIndex, "r", "NUMBER", NULL)
    val X = Column(++columnIndex, "x", "NUMBER", NULL)
    val R0 = Column(++columnIndex, "r0", "NUMBER", NULL)
    val X0 = Column(++columnIndex, "x0", "NUMBER", NULL)
    val BCH = Column(++columnIndex, "bch", "NUMBER", NULL)
    val GCH = Column(++columnIndex, "gch", "NUMBER", NULL)
    val B0CH = Column(++columnIndex, "b0ch", "NUMBER", NULL)
    val G0CH = Column(++columnIndex, "g0ch", "NUMBER", NULL)

    override fun name(): String {
        return "per_length_sequence_impedances"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

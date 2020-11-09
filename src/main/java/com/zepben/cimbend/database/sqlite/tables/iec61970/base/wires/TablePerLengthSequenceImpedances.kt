/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TablePerLengthSequenceImpedances : TablePerLengthImpedances() {

    val R = Column(++columnIndex, "r", "NUMBER", NOT_NULL)
    val X = Column(++columnIndex, "x", "NUMBER", NOT_NULL)
    val R0 = Column(++columnIndex, "r0", "NUMBER", NOT_NULL)
    val X0 = Column(++columnIndex, "x0", "NUMBER", NOT_NULL)
    val BCH = Column(++columnIndex, "bch", "NUMBER", NOT_NULL)
    val GCH = Column(++columnIndex, "gch", "NUMBER", NOT_NULL)
    val B0CH = Column(++columnIndex, "b0ch", "NUMBER", NOT_NULL)
    val G0CH = Column(++columnIndex, "g0ch", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "per_length_sequence_impedances"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

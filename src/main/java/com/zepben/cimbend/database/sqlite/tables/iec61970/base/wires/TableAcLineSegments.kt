/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NULL

@Suppress("PropertyName")
class TableAcLineSegments : TableConductors() {

    val PER_LENGTH_SEQUENCE_IMPEDANCE_MRID =
        Column(++columnIndex, "per_length_sequence_impedance_mrid", "TEXT", NULL)

    override fun name(): String {
        return "ac_line_segments"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableBaseVoltages : TableIdentifiedObjects() {

    val NOMINAL_VOLTAGE = Column(++columnIndex, "base_voltage", "INTEGER", NOT_NULL)

    override fun name(): String {
        return "base_voltages"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

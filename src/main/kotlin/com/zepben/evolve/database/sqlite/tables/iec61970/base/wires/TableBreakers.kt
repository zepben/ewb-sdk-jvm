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
class TableBreakers : TableProtectedSwitches() {

    val IN_TRANSIT_TIME = Column(++columnIndex, "in_transit_time", "NUMBER", NULL)

    override fun name(): String {
        return "breakers"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

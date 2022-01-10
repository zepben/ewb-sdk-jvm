/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
class TablePowerElectronicsConnectionPhases : TablePowerSystemResources() {

    val P = Column(++columnIndex, "p", "NUMBER", NULL)
    val PHASE = Column(++columnIndex, "phase", "TEXT", NOT_NULL)
    val Q = Column(++columnIndex, "q", "NUMBER", NULL)

    override fun name(): String {
        return "power_electronics_connection_phase"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

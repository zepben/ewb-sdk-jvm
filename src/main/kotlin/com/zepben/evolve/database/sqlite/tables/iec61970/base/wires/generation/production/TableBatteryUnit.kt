/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableBatteryUnit : TablePowerElectronicsUnit() {

    val BATTERY_STATE = Column(++columnIndex, "battery_state", "TEXT", NOT_NULL)
    val RATED_E = Column(++columnIndex, "rated_e", "INTEGER", NOT_NULL)
    val STORED_E = Column(++columnIndex, "stored_e", "INTEGER", NOT_NULL)

    override fun name(): String {
        return "battery_unit"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

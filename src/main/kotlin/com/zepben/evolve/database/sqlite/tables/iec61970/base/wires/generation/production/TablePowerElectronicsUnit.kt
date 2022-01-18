/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires.generation.production

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableEquipment

@Suppress("PropertyName")
abstract class TablePowerElectronicsUnit : TableEquipment() {

    val POWER_ELECTRONICS_CONNECTION_MRID = Column(++columnIndex, "power_electronics_connection_mrid", "TEXT", NULL)
    val MAX_P = Column(++columnIndex, "max_p", "NUMBER", NULL)
    val MIN_P = Column(++columnIndex, "min_p", "NUMBER", NULL)

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()
        cols.add(listOf(POWER_ELECTRONICS_CONNECTION_MRID))
        return cols
    }

}

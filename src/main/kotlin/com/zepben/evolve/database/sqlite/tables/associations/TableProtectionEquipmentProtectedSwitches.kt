/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.associations

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableProtectionEquipmentProtectedSwitches : SqliteTable() {

    val PROTECTION_EQUIPMENT_MRID = Column(++columnIndex, "protection_equipment_mrid", "TEXT", NOT_NULL)
    val PROTECTED_SWITCH_MRID = Column(++columnIndex, "protected_switch_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "protection_equipment_protected_switches"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(PROTECTION_EQUIPMENT_MRID, PROTECTED_SWITCH_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(PROTECTION_EQUIPMENT_MRID))
        cols.add(listOf(PROTECTED_SWITCH_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}
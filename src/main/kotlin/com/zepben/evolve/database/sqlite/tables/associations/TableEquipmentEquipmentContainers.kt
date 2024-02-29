/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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
class TableEquipmentEquipmentContainers : SqliteTable() {

    val EQUIPMENT_MRID: Column = Column(++columnIndex, "equipment_mrid", "TEXT", NOT_NULL)
    val EQUIPMENT_CONTAINER_MRID: Column = Column(++columnIndex, "equipment_container_mrid", "TEXT", NOT_NULL)

    override fun name(): String {
        return "equipment_equipment_containers"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(EQUIPMENT_MRID, EQUIPMENT_CONTAINER_MRID))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(EQUIPMENT_MRID))
        cols.add(listOf(EQUIPMENT_CONTAINER_MRID))

        return cols
    }

    override val tableClass: Class<TableEquipmentEquipmentContainers> = this.javaClass
    override val tableClassInstance: TableEquipmentEquipmentContainers = this

}

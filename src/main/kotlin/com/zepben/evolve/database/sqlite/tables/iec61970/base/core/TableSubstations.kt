/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.core

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableSubstations : TableEquipmentContainers() {

    val SUB_GEOGRAPHICAL_REGION_MRID =
        Column(++columnIndex, "sub_geographical_region_mrid", "TEXT", NULL)

    override fun name(): String {
        return "substations"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(SUB_GEOGRAPHICAL_REGION_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

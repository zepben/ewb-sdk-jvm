/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.core

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableTerminals : TableAcDcTerminals() {

    val CONDUCTING_EQUIPMENT_MRID = Column(++columnIndex, "conducting_equipment_mrid", "TEXT", NULL)
    val SEQUENCE_NUMBER = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val CONNECTIVITY_NODE_MRID = Column(++columnIndex, "connectivity_node_mrid", "TEXT", NULL)
    val PHASES = Column(++columnIndex, "phases", "TEXT", NOT_NULL)

    override fun name(): String {
        return "terminals"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(CONDUCTING_EQUIPMENT_MRID, SEQUENCE_NUMBER))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(CONNECTIVITY_NODE_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

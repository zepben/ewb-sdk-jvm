/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TableRecloseDelays : SqliteTable() {

    val RELAY_INFO_MRID = Column(++columnIndex, "relay_info_mrid", "TEXT", NOT_NULL)
    val RECLOSE_DELAY = Column(++columnIndex, "reclose_delay", "NUMBER", NOT_NULL)
    val SEQUENCE_NUMBER = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)

    override fun name(): String {
        return "reclose_delays"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(RELAY_INFO_MRID, SEQUENCE_NUMBER))

        return cols
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(RELAY_INFO_MRID))

        return cols
    }

    override fun selectSql(): String {
        return "${super.selectSql()} ORDER BY relay_info_mrid, sequence_number ASC;"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

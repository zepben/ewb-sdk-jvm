/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.common

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.sqlite.tables.SqliteTable

@Suppress("PropertyName")
class TablePositionPoints : SqliteTable() {

    val LOCATION_MRID = Column(++columnIndex, "location_mrid", "TEXT", NOT_NULL)
    val SEQUENCE_NUMBER = Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL)
    val X_POSITION = Column(++columnIndex, "x_position", "NUMBER", NOT_NULL)
    val Y_POSITION = Column(++columnIndex, "y_position", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "position_points"
    }

    override fun uniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.uniqueIndexColumns()

        cols.add(listOf(LOCATION_MRID, SEQUENCE_NUMBER))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

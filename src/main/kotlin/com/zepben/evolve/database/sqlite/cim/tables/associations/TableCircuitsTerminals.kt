/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.associations

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.common.SqliteTable

/**
 * A class representing the association between Circuits and Terminals.
 *
 * @property CIRCUIT_MRID A column storing the mRID of Circuits.
 * @property TERMINAL_MRID A column storing the mRID of Terminals.
 */
@Suppress("PropertyName")
class TableCircuitsTerminals : SqliteTable() {

    val CIRCUIT_MRID: Column = Column(++columnIndex, "circuit_mrid", "TEXT", NOT_NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", "TEXT", NOT_NULL)

    override val name: String = "circuits_terminals"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(CIRCUIT_MRID, TERMINAL_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(CIRCUIT_MRID))
            add(listOf(TERMINAL_MRID))
        }

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between Circuits and Terminals.
 *
 * @property CIRCUIT_MRID The mRID of Circuits.
 * @property TERMINAL_MRID The mRID of Terminals.
 */
@Suppress("PropertyName")
class TableCircuitsTerminals : SqlTable() {

    val CIRCUIT_MRID: Column = Column(++columnIndex, "circuit_mrid", Column.Type.STRING, NOT_NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "circuits_terminals"

    init {
        addUniqueIndexes(
            listOf(CIRCUIT_MRID, TERMINAL_MRID)
        )

        addNonUniqueIndexes(
            listOf(CIRCUIT_MRID),
            listOf(TERMINAL_MRID)
        )
    }

}

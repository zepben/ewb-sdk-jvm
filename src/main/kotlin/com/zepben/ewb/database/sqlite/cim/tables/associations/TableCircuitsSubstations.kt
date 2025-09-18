/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.associations

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * A class representing the association between Circuits and Substations.
 *
 * @property CIRCUIT_MRID A column storing the mRID of Circuits.
 * @property SUBSTATION_MRID A column storing the mRID of Substations.
 */
@Suppress("PropertyName")
class TableCircuitsSubstations : SqliteTable() {

    val CIRCUIT_MRID: Column = Column(++columnIndex, "circuit_mrid", "TEXT", NOT_NULL)
    val SUBSTATION_MRID: Column = Column(++columnIndex, "substation_mrid", "TEXT", NOT_NULL)

    override val name: String = "circuits_substations"

    init {
        addUniqueIndexes(
            listOf(CIRCUIT_MRID, SUBSTATION_MRID)
        )

        addNonUniqueIndexes(
            listOf(CIRCUIT_MRID),
            listOf(SUBSTATION_MRID)
        )
    }

}

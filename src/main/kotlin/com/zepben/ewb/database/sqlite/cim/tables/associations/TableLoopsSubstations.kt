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
 * A class representing the association between Loops and Substations.
 *
 * @property LOOP_MRID A column storing the mRID of Loops.
 * @property SUBSTATION_MRID A column storing the mRID of Substations.
 * @property RELATIONSHIP A column storing the type of relationships between the Loop and the Substation.
 */
@Suppress("PropertyName")
class TableLoopsSubstations : SqliteTable() {

    val LOOP_MRID: Column = Column(++columnIndex, "loop_mrid", "TEXT", NOT_NULL)
    val SUBSTATION_MRID: Column = Column(++columnIndex, "substation_mrid", "TEXT", NOT_NULL)
    val RELATIONSHIP: Column = Column(++columnIndex, "relationship", "TEXT", NOT_NULL)

    override val name: String = "loops_substations"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(LOOP_MRID, SUBSTATION_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(LOOP_MRID))
            add(listOf(SUBSTATION_MRID))
        }

}

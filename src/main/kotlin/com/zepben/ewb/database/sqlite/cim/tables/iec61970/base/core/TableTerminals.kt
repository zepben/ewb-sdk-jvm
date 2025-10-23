/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableTerminals : TableAcDcTerminals() {

    val CONDUCTING_EQUIPMENT_MRID: Column = Column(++columnIndex, "conducting_equipment_mrid", Column.Type.STRING, NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)
    val CONNECTIVITY_NODE_MRID: Column = Column(++columnIndex, "connectivity_node_mrid", Column.Type.STRING, NULL)
    val PHASES: Column = Column(++columnIndex, "phases", Column.Type.STRING, NOT_NULL)

    override val name: String = "terminals"

    init {
        addUniqueIndexes(
            listOf(CONDUCTING_EQUIPMENT_MRID, SEQUENCE_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(CONNECTIVITY_NODE_MRID)
        )
    }

}

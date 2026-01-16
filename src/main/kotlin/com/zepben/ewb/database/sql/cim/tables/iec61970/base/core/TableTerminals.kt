/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.cim.iec61970.base.core.ConnectivityNode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Terminal` columns required for the database table.
 *
 * @property CONDUCTING_EQUIPMENT_MRID The conducting equipment of the terminal.  Conducting equipment have  terminals that may be connected to other
 *                               conducting equipment terminals via connectivity nodes or topological nodes.
 * @property SEQUENCE_NUMBER The orientation of the terminal connections for a multiple terminal conducting equipment.
 *                          The sequence numbering starts with 1 and additional terminals should follow in increasing order.
 *                          The first terminal is the "starting point" for a two terminal branch.
 * @property CONNECTIVITY_NODE_MRID The [ConnectivityNode] this [Terminal] is connected to, or `null` if this [Terminal] is disconnected.
 * @property PHASES Represents the normal network phasing condition.
 *                  If the attribute is missing three phases (ABC or ABCN) shall be assumed.
 */
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

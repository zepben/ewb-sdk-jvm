/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Feeder` columns required for the database table.
 *
 * @property NORMAL_HEAD_TERMINAL_MRID The normal head terminal or terminals of the feeder.
 * @property NORMAL_ENERGIZING_SUBSTATION_MRID The substation that nominally energizes the feeder.  Also used for naming purposes.
 */
@Suppress("PropertyName")
class TableFeeders : TableEquipmentContainers() {

    val NORMAL_HEAD_TERMINAL_MRID: Column = Column(++columnIndex, "normal_head_terminal_mrid", Column.Type.STRING, NULL)
    val NORMAL_ENERGIZING_SUBSTATION_MRID: Column =
        Column(++columnIndex, "normal_energizing_substation_mrid", Column.Type.STRING, NULL)

    override val name: String = "feeders"

    init {
        addNonUniqueIndexes(
            listOf(NORMAL_ENERGIZING_SUBSTATION_MRID)
        )
    }

}

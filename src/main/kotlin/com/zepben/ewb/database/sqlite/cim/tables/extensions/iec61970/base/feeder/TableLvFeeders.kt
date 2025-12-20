/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.feeder

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableEquipmentContainers

@Suppress("PropertyName")
class TableLvFeeders : TableEquipmentContainers() {

    val NORMAL_HEAD_TERMINAL_MRID: Column = Column(++columnIndex, "normal_head_terminal_mrid", Column.Type.STRING, NULL)
    val LV_SUBSTATION_MRID: Column = Column(++columnIndex, "normal_energizing_lv_substation_mrid", Column.Type.STRING, NULL)

    override val name: String = "lv_feeders"

    init {
        addNonUniqueIndexes(
            listOf(NORMAL_HEAD_TERMINAL_MRID),
            listOf(LV_SUBSTATION_MRID)
        )
    }

}

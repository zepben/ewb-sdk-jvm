/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableEquipmentContainers
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `LvFeeder` columns required for the database table.
 *
 * @property NORMAL_HEAD_TERMINAL_MRID The normal head terminal of this LV feeder, typically the LV terminal of a distribution substation.
 * @property LV_SUBSTATION_MRID The normally energizing [LvSubstation] for this [LvFeeder].
 */
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

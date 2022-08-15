/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.infiec61970.feeder

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableEquipmentContainers

@Suppress("PropertyName")
class TableLvFeeders : TableEquipmentContainers() {

    val NORMAL_HEAD_TERMINAL_MRID = Column(++columnIndex, "normal_head_terminal_mrid", "TEXT", NULL)

    override fun name(): String {
        return "lv_feeders"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this
}

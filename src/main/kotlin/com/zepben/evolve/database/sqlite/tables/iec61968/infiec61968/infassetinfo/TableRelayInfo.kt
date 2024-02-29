/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableRelayInfo : TableAssetInfo() {

    val CURVE_SETTING: Column = Column(++columnIndex, "curve_setting", "TEXT", NULL)
    val RECLOSE_FAST: Column = Column(++columnIndex, "reclose_fast", "BOOLEAN", NULL)

    override fun name(): String {
        return "relay_info"
    }

    override val tableClass: Class<TableRelayInfo> = this.javaClass
    override val tableClassInstance: TableRelayInfo = this

}

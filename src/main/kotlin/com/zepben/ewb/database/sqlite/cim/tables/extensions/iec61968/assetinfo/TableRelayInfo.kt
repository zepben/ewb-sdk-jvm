/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableRelayInfo : TableAssetInfo() {

    val CURVE_SETTING: Column = Column(++columnIndex, "curve_setting", "TEXT", NULL)
    val RECLOSE_FAST: Column = Column(++columnIndex, "reclose_fast", "BOOLEAN", NULL)

    override val name: String = "relay_info"

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `SwitchInfo` columns required for the database table.
 *
 * @property RATED_INTERRUPTING_TIME Switch rated interrupting time in seconds.
 */
@Suppress("PropertyName")
class TableSwitchInfo : TableAssetInfo() {

    val RATED_INTERRUPTING_TIME: Column = Column(++columnIndex, "rated_interrupting_time", Column.Type.DOUBLE, NULL)

    override val name: String = "switch_info"

}

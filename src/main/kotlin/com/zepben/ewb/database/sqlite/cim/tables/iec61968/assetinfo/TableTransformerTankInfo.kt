/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableTransformerTankInfo : TableAssetInfo() {

    val POWER_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "power_transformer_info_mrid", "TEXT", NULL)

    override val name: String = "transformer_tank_info"

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(POWER_TRANSFORMER_INFO_MRID))
        }


}

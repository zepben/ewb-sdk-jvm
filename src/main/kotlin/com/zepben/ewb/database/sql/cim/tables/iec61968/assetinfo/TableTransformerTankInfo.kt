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
 * A class representing the `TransformerTankInfo` columns required for the database table.
 *
 * @property POWER_TRANSFORMER_INFO_MRID Power transformer data that this tank description is part of.
 */
@Suppress("PropertyName")
class TableTransformerTankInfo : TableAssetInfo() {

    val POWER_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "power_transformer_info_mrid", Column.Type.STRING, NULL)

    override val name: String = "transformer_tank_info"

    init {
        addNonUniqueIndexes(
            listOf(POWER_TRANSFORMER_INFO_MRID)
        )
    }


}

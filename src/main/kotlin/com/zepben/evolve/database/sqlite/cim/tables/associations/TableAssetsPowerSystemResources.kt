/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.associations

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.common.SqliteTable

/**
 * A class representing the association between Assets and PowerSystemResources.
 *
 * @property ASSET_MRID A column storing the mRID of Assets.
 * @property POWER_SYSTEM_RESOURCE_MRID A column storing the mRID of PowerSystemResources.
 */
@Suppress("PropertyName")
class TableAssetsPowerSystemResources : SqliteTable() {

    val ASSET_MRID: Column = Column(++columnIndex, "asset_mrid", "TEXT", NOT_NULL)
    val POWER_SYSTEM_RESOURCE_MRID: Column = Column(++columnIndex, "power_system_resource_mrid", "TEXT", NOT_NULL)

    override val name: String = "assets_power_system_resources"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(ASSET_MRID, POWER_SYSTEM_RESOURCE_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(ASSET_MRID))
            add(listOf(POWER_SYSTEM_RESOURCE_MRID))
        }

}

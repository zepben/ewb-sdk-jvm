/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between Equipment and UsagePoints.
 *
 * @property EQUIPMENT_MRID The mRID of Equipment.
 * @property USAGE_POINT_MRID The mRID of UsagePoints.
 */
@Suppress("PropertyName")
class TableEquipmentUsagePoints : SqlTable() {

    val EQUIPMENT_MRID: Column = Column(++columnIndex, "equipment_mrid", Column.Type.STRING, NOT_NULL)
    val USAGE_POINT_MRID: Column = Column(++columnIndex, "usage_point_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "equipment_usage_points"

    init {
        addUniqueIndexes(
            listOf(EQUIPMENT_MRID, USAGE_POINT_MRID)
        )

        addNonUniqueIndexes(
            listOf(EQUIPMENT_MRID),
            listOf(USAGE_POINT_MRID)
        )
    }

}

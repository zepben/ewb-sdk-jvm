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
 * A class representing the association between UsagePoints and EndDevices.
 *
 * @property USAGE_POINT_MRID The mRID of UsagePoints.
 * @property END_DEVICE_MRID The mRID of EndDevices.
 */
@Suppress("PropertyName")
class TableUsagePointsEndDevices : SqlTable() {

    val USAGE_POINT_MRID: Column = Column(++columnIndex, "usage_point_mrid", Column.Type.STRING, NOT_NULL)
    val END_DEVICE_MRID: Column = Column(++columnIndex, "end_device_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "usage_points_end_devices"

    init {
        addUniqueIndexes(
            listOf(USAGE_POINT_MRID, END_DEVICE_MRID)
        )

        addNonUniqueIndexes(
            listOf(USAGE_POINT_MRID),
            listOf(END_DEVICE_MRID)
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.associations

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * A class representing the association between UsagePoints and EndDevices.
 *
 * @property USAGE_POINT_MRID A column storing the mRID of UsagePoints.
 * @property END_DEVICE_MRID A column storing the mRID of EndDevices.
 */
@Suppress("PropertyName")
class TableUsagePointsEndDevices : SqliteTable() {

    val USAGE_POINT_MRID: Column = Column(++columnIndex, "usage_point_mrid", "TEXT", NOT_NULL)
    val END_DEVICE_MRID: Column = Column(++columnIndex, "end_device_mrid", "TEXT", NOT_NULL)

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

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
 * A class representing the association between EndDevices and EndDeviceFunctions.
 *
 * @property END_DEVICE_MRID A column storing the mRID of EndDevices.
 * @property END_DEVICE_FUNCTION_MRID A column storing the mRID of EndDeviceFunctions.
 */
@Suppress("PropertyName")
class TableEndDevicesEndDeviceFunctions : SqliteTable() {

    val END_DEVICE_MRID: Column = Column(++columnIndex, "end_device_mrid", Column.Type.STRING, NOT_NULL)
    val END_DEVICE_FUNCTION_MRID: Column = Column(++columnIndex, "end_device_function_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "end_devices_end_device_functions"

    init {
        addUniqueIndexes(
            listOf(END_DEVICE_MRID, END_DEVICE_FUNCTION_MRID)
        )

        addNonUniqueIndexes(
            listOf(END_DEVICE_MRID),
            listOf(END_DEVICE_FUNCTION_MRID)
        )
    }

}

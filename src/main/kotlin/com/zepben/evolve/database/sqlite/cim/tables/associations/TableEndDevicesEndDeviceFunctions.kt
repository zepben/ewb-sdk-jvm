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
 * A class representing the association between EndDevices and EndDeviceFunctions.
 *
 * @property END_DEVICE_MRID A column storing the mRID of EndDevices.
 * @property END_DEVICE_FUNCTION_MRID A column storing the mRID of EndDeviceFunctions.
 */
@Suppress("PropertyName")
class TableEndDevicesEndDeviceFunctions : SqliteTable() {

    val END_DEVICE_MRID: Column = Column(++columnIndex, "end_device_mrid", "TEXT", NOT_NULL)
    val END_DEVICE_FUNCTION_MRID: Column = Column(++columnIndex, "end_device_function_mrid", "TEXT", NOT_NULL)

    override val name: String = "end_devices_end_device_functions"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(END_DEVICE_MRID, END_DEVICE_FUNCTION_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(END_DEVICE_MRID))
            add(listOf(END_DEVICE_FUNCTION_MRID))
        }

}

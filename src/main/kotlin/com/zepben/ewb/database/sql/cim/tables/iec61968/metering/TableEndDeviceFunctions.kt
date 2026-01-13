/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.metering

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetFunctions
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `EndDeviceFunction` columns required for the database table.
 *
 * @property ENABLED True if the function is enabled.
 */
@Suppress("PropertyName")
abstract class TableEndDeviceFunctions : TableAssetFunctions() {

    val ENABLED: Column = Column(++columnIndex, "enabled", Column.Type.BOOLEAN, NULL)

}

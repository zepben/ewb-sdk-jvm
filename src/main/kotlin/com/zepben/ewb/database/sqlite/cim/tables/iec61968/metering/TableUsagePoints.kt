/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableUsagePoints : TableIdentifiedObjects() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", Column.Type.STRING, NULL)
    val IS_VIRTUAL: Column = Column(++columnIndex, "is_virtual", Column.Type.BOOLEAN, NULL)
    val CONNECTION_CATEGORY: Column = Column(++columnIndex, "connection_category", Column.Type.STRING, NULL)
    val RATED_POWER: Column = Column(++columnIndex, "rated_power", Column.Type.INTEGER, NULL)
    val APPROVED_INVERTER_CAPACITY: Column = Column(++columnIndex, "approved_inverter_capacity", Column.Type.INTEGER, NULL)
    val PHASE_CODE: Column = Column(++columnIndex, "phase_code", Column.Type.STRING, NOT_NULL)

    override val name: String = "usage_points"

}

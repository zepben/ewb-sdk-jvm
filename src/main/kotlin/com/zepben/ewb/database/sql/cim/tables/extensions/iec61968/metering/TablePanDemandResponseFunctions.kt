/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61968.metering

import com.zepben.ewb.database.sql.cim.tables.iec61968.metering.TableEndDeviceFunctions
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PanDemandResponseFunction` columns required for the database table.
 *
 * @property KIND The kind of this function.
 * @property APPLIANCE The appliances being controlled.
 */
@Suppress("PropertyName")
class TablePanDemandResponseFunctions : TableEndDeviceFunctions() {

    val KIND: Column = Column(++columnIndex, "kind", Column.Type.STRING, NOT_NULL)
    val APPLIANCE: Column = Column(++columnIndex, "appliance", Column.Type.INTEGER, NULL)

    override val name: String = "pan_demand_response_functions"

}

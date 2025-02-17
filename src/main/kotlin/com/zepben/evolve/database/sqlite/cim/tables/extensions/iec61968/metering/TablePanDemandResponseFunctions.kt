/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.extensions.iec61968.metering

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.metering.TableEndDeviceFunctions

@Suppress("PropertyName")
class TablePanDemandResponseFunctions : TableEndDeviceFunctions() {

    val KIND: Column = Column(++columnIndex, "kind", "TEXT", NOT_NULL)
    val APPLIANCE: Column = Column(++columnIndex, "appliance", "INTEGER", NULL)

    override val name: String = "pan_demand_response_functions"

}

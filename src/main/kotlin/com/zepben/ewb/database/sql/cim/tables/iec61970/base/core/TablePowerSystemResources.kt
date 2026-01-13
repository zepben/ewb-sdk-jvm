/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PowerSystemResource` columns required for the database table.
 *
 * @property LOCATION_MRID Location of this power system resource.
 * @property NUM_CONTROLS Number of Control's known to associate with this [PowerSystemResource]
 */
@Suppress("PropertyName")
abstract class TablePowerSystemResources : TableIdentifiedObjects() {

    val LOCATION_MRID: Column = Column(++columnIndex, "location_mrid", Column.Type.STRING, NULL)
    val NUM_CONTROLS: Column = Column(++columnIndex, "num_controls", Column.Type.INTEGER, NULL)

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.meas

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Control` columns required for the database table.
 *
 * @property POWER_SYSTEM_RESOURCE_MRID The regulating device governed by this control output.
 */
@Suppress("PropertyName")
class TableControls : TableIoPoints() {

    val POWER_SYSTEM_RESOURCE_MRID: Column = Column(++columnIndex, "power_system_resource_mrid", Column.Type.STRING, NULL)

    override val name: String = "controls"

}

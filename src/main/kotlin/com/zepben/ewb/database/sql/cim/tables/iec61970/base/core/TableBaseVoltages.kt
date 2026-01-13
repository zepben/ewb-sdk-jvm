/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL

/**
 * A class representing the `BaseVoltage` columns required for the database table.
 *
 * @property NOMINAL_VOLTAGE The power system resource's base voltage.
 */
@Suppress("PropertyName")
class TableBaseVoltages : TableIdentifiedObjects() {

    val NOMINAL_VOLTAGE: Column = Column(++columnIndex, "nominal_voltage", Column.Type.INTEGER, NOT_NULL)

    override val name: String = "base_voltages"

}

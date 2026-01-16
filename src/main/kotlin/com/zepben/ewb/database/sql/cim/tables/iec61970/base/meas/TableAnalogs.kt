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
 * A class representing the `Analog` columns required for the database table.
 *
 * @property POSITIVE_FLOW_IN If true then this measurement is an active power, reactive power or current with the convention that a positive
 * value measured at the Terminal means power is flowing into the related PowerSystemResource.
 */
@Suppress("PropertyName")
class TableAnalogs : TableMeasurements() {

    val POSITIVE_FLOW_IN: Column = Column(++columnIndex, "positive_flow_in", Column.Type.BOOLEAN, NULL)

    override val name: String = "analogs"

}

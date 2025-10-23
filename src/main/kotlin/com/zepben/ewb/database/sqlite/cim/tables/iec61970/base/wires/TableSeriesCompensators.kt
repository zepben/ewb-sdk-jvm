/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableConductingEquipment

@Suppress("PropertyName")
class TableSeriesCompensators : TableConductingEquipment() {

    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val R0: Column = Column(++columnIndex, "r0", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val X0: Column = Column(++columnIndex, "x0", Column.Type.DOUBLE, NULL)
    val VARISTOR_RATED_CURRENT: Column = Column(++columnIndex, "varistor_rated_current", Column.Type.INTEGER, NULL)
    val VARISTOR_VOLTAGE_THRESHOLD: Column = Column(++columnIndex, "varistor_voltage_threshold", Column.Type.INTEGER, NULL)

    override val name: String = "series_compensators"

}

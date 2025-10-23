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

@Suppress("PropertyName")
class TableLinearShuntCompensators : TableShuntCompensators() {

    val B0_PER_SECTION: Column = Column(++columnIndex, "b0_per_section", Column.Type.DOUBLE, NULL)
    val B_PER_SECTION: Column = Column(++columnIndex, "b_per_section", Column.Type.DOUBLE, NULL)
    val G0_PER_SECTION: Column = Column(++columnIndex, "g0_per_section", Column.Type.DOUBLE, NULL)
    val G_PER_SECTION: Column = Column(++columnIndex, "g_per_section", Column.Type.DOUBLE, NULL)

    override val name: String = "linear_shunt_compensators"

}

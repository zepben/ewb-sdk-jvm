/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `LinearShuntCompensator` columns required for the database table.
 *
 * @property B0_PER_SECTION Zero sequence shunt (charging) susceptance per section
 * @property B_PER_SECTION Positive sequence shunt (charging) susceptance per section
 * @property G0_PER_SECTION Zero sequence shunt (charging) conductance per section
 * @property G_PER_SECTION Positive sequence shunt (charging) conductance per section
 */
@Suppress("PropertyName")
class TableLinearShuntCompensators : TableShuntCompensators() {

    val B0_PER_SECTION: Column = Column(++columnIndex, "b0_per_section", Column.Type.DOUBLE, NULL)
    val B_PER_SECTION: Column = Column(++columnIndex, "b_per_section", Column.Type.DOUBLE, NULL)
    val G0_PER_SECTION: Column = Column(++columnIndex, "g0_per_section", Column.Type.DOUBLE, NULL)
    val G_PER_SECTION: Column = Column(++columnIndex, "g_per_section", Column.Type.DOUBLE, NULL)

    override val name: String = "linear_shunt_compensators"

}

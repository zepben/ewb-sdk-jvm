/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sql.tables.Column
import com.zepben.evolve.database.sql.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableFuses : TableSwitches() {

    val FUNCTION_MRID: Column = Column(++columnIndex, "function_mrid", "TEXT", NULL)

    override val name: String = "fuses"

}

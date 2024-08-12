/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.cim.tables.Column
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NULL

@Suppress("PropertyName")
abstract class TableRotatingMachines : TableRegulatingCondEq() {

    val RATED_POWER_FACTOR: Column = Column(++columnIndex, "rated_power_factor", "NUMBER", NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", "NUMBER", NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", "NUMBER", NULL)
    val P: Column = Column(++columnIndex, "p", "NUMBER", NULL)
    val Q: Column = Column(++columnIndex, "q", "NUMBER", NULL)

    override val name: String = "rotating_machine"

}

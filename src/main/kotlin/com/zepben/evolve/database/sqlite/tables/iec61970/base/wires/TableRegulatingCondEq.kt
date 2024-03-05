/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
abstract class TableRegulatingCondEq : TableEnergyConnections() {

    val CONTROL_ENABLED: Column = Column(++columnIndex, "control_enabled", "BOOLEAN")
    val REGULATING_CONTROL_MRID: Column = Column(++columnIndex, "regulating_control_mrid", "TEXT", NULL)

}

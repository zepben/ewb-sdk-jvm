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
abstract class TableRegulatingCondEq : TableEnergyConnections() {

    val CONTROL_ENABLED: Column = Column(++columnIndex, "control_enabled", Column.Type.BOOLEAN, NULL)
    val REGULATING_CONTROL_MRID: Column = Column(++columnIndex, "regulating_control_mrid", Column.Type.STRING, NULL)

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TablePowerSystemResources

@Suppress("PropertyName")
abstract class TableProtectionRelayFunctions : TablePowerSystemResources() {

    val MODEL: Column = Column(++columnIndex, "model", Column.Type.STRING, NULL)
    val RECLOSING: Column = Column(++columnIndex, "reclosing", Column.Type.BOOLEAN, NULL)
    val RELAY_DELAY_TIME: Column = Column(++columnIndex, "relay_delay_time", Column.Type.DOUBLE, NULL)
    val PROTECTION_KIND: Column = Column(++columnIndex, "protection_kind", Column.Type.STRING, NOT_NULL)
    val DIRECTABLE: Column = Column(++columnIndex, "directable", Column.Type.BOOLEAN, NULL)
    val POWER_DIRECTION: Column = Column(++columnIndex, "power_direction", Column.Type.STRING, NOT_NULL)
    val RELAY_INFO_MRID: Column = Column(++columnIndex, "relay_info_mrid", Column.Type.STRING, NULL)

}

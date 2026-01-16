/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TablePowerSystemResources
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ProtectionRelayFunction` columns required for the database table.
 *
 * @property MODEL The protection equipment type name (manufacturer information)
 * @property RECLOSING True if the protection equipment is reclosing or False otherwise.
 * @property RELAY_DELAY_TIME The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property PROTECTION_KIND The kind of protection being provided by this [ProtectionRelayFunction].
 * @property DIRECTABLE Whether this [ProtectionRelayFunction] responds to power flow in a given direction.
 * @property POWER_DIRECTION The flow of power direction used by this [ProtectionRelayFunction].
 * @property RELAY_INFO_MRID The relay info this function belongs to.
 */
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

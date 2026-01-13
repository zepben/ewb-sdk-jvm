/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ProtectionRelayScheme` columns required for the database table.
 *
 * @property SYSTEM_MRID The system this scheme belongs to.
 */
@Suppress("PropertyName")
class TableProtectionRelaySchemes : TableIdentifiedObjects() {

    val SYSTEM_MRID: Column = Column(++columnIndex, "system_mrid", Column.Type.STRING, NULL)

    override val name: String = "protection_relay_schemes"

}

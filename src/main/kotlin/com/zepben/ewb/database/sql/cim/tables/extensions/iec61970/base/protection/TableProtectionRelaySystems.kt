/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL

/**
 * A class representing the `ProtectionRelaySystem` columns required for the database table.
 *
 * @property PROTECTION_KIND The kind of protection being provided by this protection equipment.
 */
@Suppress("PropertyName")
class TableProtectionRelaySystems : TableEquipment() {

    val PROTECTION_KIND: Column = Column(++columnIndex, "protection_kind", Column.Type.STRING, NOT_NULL)

    override val name: String = "protection_relay_systems"

}

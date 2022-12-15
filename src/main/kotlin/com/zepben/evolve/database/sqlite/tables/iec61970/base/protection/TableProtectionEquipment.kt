/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableEquipment

@Suppress("PropertyName")
abstract class TableProtectionEquipment : TableEquipment() {

    val RELAY_DELAY_TIME = Column(++columnIndex, "relay_delay_time", "NUMBER", NULL)
    val PROTECTION_KIND = Column(++columnIndex, "protection_kind", "TEXT", NOT_NULL)

}

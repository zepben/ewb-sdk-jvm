/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableEquipment

@Suppress("PropertyName")
class TableProtectionRelaySystems : TableEquipment() {

    val PROTECTION_KIND = Column(++columnIndex, "protection_kind", "TEXT", Column.Nullable.NOT_NULL)

    override fun name(): String {
        return "protection_relay_systems"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

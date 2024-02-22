/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableCurrentRelays : TableProtectionRelayFunctions() {

    val CURRENT_LIMIT_1 = Column(++columnIndex, "current_limit_1", "NUMBER", NULL)
    val INVERSE_TIME_FLAG = Column(++columnIndex, "inverse_time_flag", "BOOLEAN", NULL)
    val TIME_DELAY_1 = Column(++columnIndex, "time_delay_1", "NUMBER", NULL)

    override fun name(): String {
        return "current_relays"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

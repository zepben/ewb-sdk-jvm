/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.protection

import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection.TableProtectionRelayFunctions
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `CurrentRelay` columns required for the database table.
 *
 * @property CURRENT_LIMIT_1 Current limit number 1 for inverse time pickup in amperes.
 * @property INVERSE_TIME_FLAG Set true if the current relay has inverse time characteristic.
 * @property TIME_DELAY_1 Inverse time delay number 1 for current limit number 1 in seconds.
 */
@Suppress("PropertyName")
class TableCurrentRelays : TableProtectionRelayFunctions() {

    val CURRENT_LIMIT_1: Column = Column(++columnIndex, "current_limit_1", Column.Type.DOUBLE, NULL)
    val INVERSE_TIME_FLAG: Column = Column(++columnIndex, "inverse_time_flag", Column.Type.BOOLEAN, NULL)
    val TIME_DELAY_1: Column = Column(++columnIndex, "time_delay_1", Column.Type.DOUBLE, NULL)

    override val name: String = "current_relays"

}

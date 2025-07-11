/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableDirectionalCurrentRelays : TableProtectionRelayFunctions() {

    val DIRECTIONAL_CHARACTERISTIC_ANGLE: Column = Column(++columnIndex, "directional_characteristic_angle", "NUMBER", NULL)
    val POLARIZING_QUANTITY_TYPE: Column = Column(++columnIndex, "polarizing_quantity_type", "TEXT", NULL)
    val RELAY_ELEMENT_PHASE: Column = Column(++columnIndex, "relay_element_phase", "TEXT", NULL)
    val MINIMUM_PICKUP_CURRENT: Column = Column(++columnIndex, "minimum_pickup_current", "NUMBER", NULL)
    val CURRENT_LIMIT_1: Column = Column(++columnIndex, "current_limit_1", "NUMBER", NULL)
    val INVERSE_TIME_FLAG: Column = Column(++columnIndex, "inverse_time_flag", "NUMBER", NULL)
    val TIME_DELAY_1: Column = Column(++columnIndex, "time_delay_1", "NUMBER", NULL)

    override val name: String = "directional_current_relays"
}
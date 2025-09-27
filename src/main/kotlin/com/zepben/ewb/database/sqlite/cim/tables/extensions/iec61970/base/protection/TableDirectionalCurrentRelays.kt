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
import com.zepben.ewb.database.sql.Column.Type.*

/**
 * A class representing the DirectionalCurrentRelay columns required for the database table.
 *
 * @property DIRECTIONAL_CHARACTERISTIC_ANGLE A column storing the characteristic angle (in degrees) that defines the boundary between the operate and restrain regions of the directional element, relative to the polarizing quantity. Often referred to as Maximum Torque Angle (MTA) or Relay Characteristic Angle (RCA).
 * @property POLARIZING_QUANTITY_TYPE A column storing the type of voltage to be used for polarization. This guides the selection/derivation of voltage from the VTs.
 * @property RELAY_ELEMENT_PHASE A column storing the phase associated with this directional relay element. This helps in selecting the correct 'self-phase' or other phase-derived.
 * @property MINIMUM_PICKUP_CURRENT A column storing the minimum current magnitude required for the directional element to operate reliably and determine direction. This might be different from the main pickupCurrent for the overcurrent function.
 * @property CURRENT_LIMIT_1 A column storing the current limit number 1 for inverse time pickup in amperes.
 * @property INVERSE_TIME_FLAG A column storing the true if the current relay has inverse time characteristic.
 * @property TIME_DELAY_1 A column storing the inverse time delay number 1 for current limit number 1 in seconds.
 */
@Suppress("PropertyName")
class TableDirectionalCurrentRelays : TableProtectionRelayFunctions() {

    val DIRECTIONAL_CHARACTERISTIC_ANGLE: Column = Column(++columnIndex, "directional_characteristic_angle", DOUBLE, NULL)
    val POLARIZING_QUANTITY_TYPE: Column = Column(++columnIndex, "polarizing_quantity_type", STRING, NULL)
    val RELAY_ELEMENT_PHASE: Column = Column(++columnIndex, "relay_element_phase", STRING, NULL)
    val MINIMUM_PICKUP_CURRENT: Column = Column(++columnIndex, "minimum_pickup_current", DOUBLE, NULL)
    val CURRENT_LIMIT_1: Column = Column(++columnIndex, "current_limit_1", DOUBLE, NULL)
    val INVERSE_TIME_FLAG: Column = Column(++columnIndex, "inverse_time_flag", BOOLEAN, NULL)
    val TIME_DELAY_1: Column = Column(++columnIndex, "time_delay_1", DOUBLE, NULL)

    override val name: String = "directional_current_relays"

}

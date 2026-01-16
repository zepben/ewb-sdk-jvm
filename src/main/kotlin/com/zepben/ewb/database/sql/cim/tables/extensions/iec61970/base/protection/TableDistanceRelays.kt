/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.protection

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `DistanceRelay` columns required for the database table.
 *
 * @property BACKWARD_BLIND The reverse blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property BACKWARD_REACH The reverse reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite
 * direction of power flow for which the relay will provide protection.
 * @property BACKWARD_REACTANCE The reverse reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction
 * of power flow for which the relay will provide protection.
 * @property FORWARD_BLIND The forward blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property FORWARD_REACH The forward reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite
 * direction of power flow for which the relay will provide protection.
 * @property FORWARD_REACTANCE The forward reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction
 * of power flow for which the relay will provide protection.
 * @property OPERATION_PHASE_ANGLE1 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 1 relay.
 * @property OPERATION_PHASE_ANGLE2 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 2 relay.
 * @property OPERATION_PHASE_ANGLE3 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 3 relay.
 */
@Suppress("PropertyName")
class TableDistanceRelays : TableProtectionRelayFunctions() {

    val BACKWARD_BLIND: Column = Column(++columnIndex, "backward_blind", Column.Type.DOUBLE, NULL)
    val BACKWARD_REACH: Column = Column(++columnIndex, "backward_reach", Column.Type.DOUBLE, NULL)
    val BACKWARD_REACTANCE: Column = Column(++columnIndex, "backward_reactance", Column.Type.DOUBLE, NULL)
    val FORWARD_BLIND: Column = Column(++columnIndex, "forward_blind", Column.Type.DOUBLE, NULL)
    val FORWARD_REACH: Column = Column(++columnIndex, "forward_reach", Column.Type.DOUBLE, NULL)
    val FORWARD_REACTANCE: Column = Column(++columnIndex, "forward_reactance", Column.Type.DOUBLE, NULL)
    val OPERATION_PHASE_ANGLE1: Column = Column(++columnIndex, "operation_phase_angle1", Column.Type.DOUBLE, NULL)
    val OPERATION_PHASE_ANGLE2: Column = Column(++columnIndex, "operation_phase_angle2", Column.Type.DOUBLE, NULL)
    val OPERATION_PHASE_ANGLE3: Column = Column(++columnIndex, "operation_phase_angle3", Column.Type.DOUBLE, NULL)

    override val name: String = "distance_relays"

}

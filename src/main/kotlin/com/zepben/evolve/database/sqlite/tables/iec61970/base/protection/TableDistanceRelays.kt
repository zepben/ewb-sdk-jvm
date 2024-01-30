/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.protection

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableDistanceRelays : TableProtectionRelayFunctions() {

    val BACKWARD_BLIND = Column(++columnIndex, "backward_blind", "NUMBER", NULL)
    val BACKWARD_REACH = Column(++columnIndex, "backward_reach", "NUMBER", NULL)
    val BACKWARD_REACTANCE = Column(++columnIndex, "backward_reactance", "NUMBER", NULL)
    val FORWARD_BLIND = Column(++columnIndex, "forward_blind", "NUMBER", NULL)
    val FORWARD_REACH = Column(++columnIndex, "forward_reach", "NUMBER", NULL)
    val FORWARD_REACTANCE = Column(++columnIndex, "forward_reactance", "NUMBER", NULL)
    val OPERATION_PHASE_ANGLE1 = Column(++columnIndex, "operation_phase_angle1", "NUMBER", NULL)
    val OPERATION_PHASE_ANGLE2 = Column(++columnIndex, "operation_phase_angle2", "NUMBER", NULL)
    val OPERATION_PHASE_ANGLE3 = Column(++columnIndex, "operation_phase_angle3", "NUMBER", NULL)

    override fun name(): String {
        return "distance_relays"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

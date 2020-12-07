/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing.invalidphasing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.traversals.Tracker

/**
 * A tracker that tracks the [ConductingEquipment] that owns the [Terminal] regardless of how it is visited.
 */
internal class AssociatedTerminalTracker : Tracker<Terminal> {

    private val visited = mutableSetOf<ConductingEquipment>()

    override fun hasVisited(terminal: Terminal?): Boolean {
        // Any terminal that does not have a valid conducting equipment reference is considered visited.
        return terminal?.conductingEquipment?.let { visited.contains(it) } ?: true
    }

    override fun visit(terminal: Terminal?): Boolean {
        // We don't visit any terminal that does not have a valid conducting equipment reference.
        return terminal?.conductingEquipment?.let { visited.add(it) } ?: false
    }

    override fun clear() {
        visited.clear()
    }

}

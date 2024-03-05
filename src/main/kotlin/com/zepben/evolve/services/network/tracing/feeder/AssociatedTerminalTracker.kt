/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.traversals.Tracker

/**
 * A tracker that tracks the [ConductingEquipment] that owns the [Terminal] regardless of how it is visited.
 */
internal class AssociatedTerminalTracker : Tracker<Terminal> {

    private val visited = mutableSetOf<ConductingEquipment>()

    /**
     * Check to see if the terminal has already been visited. Any terminal that does not have a valid conducting
     * equipment reference is considered visited.
     */
    override fun hasVisited(item: Terminal): Boolean =
        item.conductingEquipment?.let { visited.contains(it) } ?: true

    /**
     * Visit the terminal. We don't visit any terminal that does not have a valid conducting equipment reference.
     */
    override fun visit(item: Terminal): Boolean =
        item.conductingEquipment?.let { visited.add(it) } ?: false

    override fun clear() {
        visited.clear()
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath

internal class NetworkTraceTracker(initialCapacity: Int) {

    private val visited = HashSet<Any?>(initialCapacity)

    fun hasVisited(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean = visited.contains(getKey(terminal, phases))

    fun visit(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean = visited.add(getKey(terminal, phases))

    fun clear() {
        visited.clear()
    }

    private fun getKey(terminal: Terminal, phases: Set<SinglePhaseKind>): Any {
        return if (phases.isEmpty())
            terminal
        else
            terminal to phases
    }
}

internal fun List<NominalPhasePath>.toPhasesSet(): Set<SinglePhaseKind> = if (this.isEmpty()) emptySet() else this.mapTo(mutableSetOf()) { it.to }

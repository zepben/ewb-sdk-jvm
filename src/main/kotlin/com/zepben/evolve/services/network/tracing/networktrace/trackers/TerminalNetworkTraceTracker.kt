/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.trackers

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath

internal class TerminalNetworkTraceTracker : NetworkTraceTracker {

    // Setting initial capacity greater than default of 16 as we suspect a majority of network traces will step on more than 12 terminals (load factor is 0.75).
    // Not sure what a sensible initial capacity actually is, but 16 just felt too small.
    private val visited = HashSet<Any?>(256)

    override fun hasVisited(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean = visited.contains(getKey(terminal, phases))

    override fun visit(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean = visited.add(getKey(terminal, phases))

    override fun clear() {
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

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

internal class RecursiveNetworkTraceTracker(
    val parent: RecursiveNetworkTraceTracker?,
    private val delegate: TerminalNetworkTraceTracker
) : NetworkTraceTracker {
    override fun hasVisited(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean {
        return delegate.hasVisited(terminal, phases) || run {
            var current = parent
            while (current != null) {
                if (current.hasVisited(terminal, phases))
                    return true
                current = current.parent
            }
            return false
        }
    }

    override fun visit(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean {
        var parent = parent
        while (parent != null) {
            if (parent.hasVisited(terminal, phases))
                return false
            parent = parent.parent
        }

        return delegate.visit(terminal, phases)
    }

    override fun clear() {
        var parent = parent
        while (parent != null) {
            parent.clear()
            parent = parent.parent
        }

        return delegate.clear()
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * A class that creates traversals that trace terminals containing a [SinglePhaseKind] from the start terminal using nominal phasing. You can add custom step
 * actions and stop conditions to the returned traversal.
 *
 * NOTE: This does not correctly support un-ganged switching and will bypass them.
 */
internal object AssociatedTerminalTrace {

    /**
     * @return a traversal ignoring open points.
     */
    internal fun newTrace(): BasicTraversal<Terminal> {
        return newTrace(OpenTest.IGNORE_OPEN)
    }

    /**
     * @return a traversal stopping at normally open points.
     */
    internal fun newNormalTrace(): BasicTraversal<Terminal> {
        return newTrace(OpenTest.NORMALLY_OPEN)
    }

    /**
     * @return a traversal stopping at currently open points.
     */
    internal fun newCurrentTrace(): BasicTraversal<Terminal> {
        return newTrace(OpenTest.CURRENTLY_OPEN)
    }

    /**
     * Queue any terminals that share the connectivity node with [terminal] on [traversal].
     */
    internal fun queueAssociated(traversal: BasicTraversal<Terminal>, terminal: Terminal) {
        terminal.connectivityNode?.terminals
            ?.filter { it != terminal }
            ?.forEach(traversal.queue()::add)
    }

    private fun newTrace(isOpenTest: OpenTest): BasicTraversal<Terminal> {
        return BasicTraversal(queueNext(isOpenTest), BasicQueue.depthFirst(), AssociatedTerminalTracker())
    }

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<Terminal> {
        return BasicTraversal.QueueNext { terminal, traversal ->
            terminal?.conductingEquipment?.let { conductingEquipment ->
                // Stop only if all phases are open.
                if (terminal.phases.singlePhases().any { phase -> !openTest.isOpen(conductingEquipment, phase) }) {
                    conductingEquipment.terminals
                        .filter { it != terminal }
                        .forEach { queueAssociated(traversal, it) }
                }
            }
        }
    }

}

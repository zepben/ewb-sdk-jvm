/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.phases.PhaseStep.Companion.continueAt
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.processQueue
import java.util.*
import java.util.function.Consumer

/**
 * A class that creates commonly used phase based traces. You can add custom step actions and stop conditions
 * to the returned traversal.
 */
object PhaseTrace {

    /**
     * @return a traversal that traces along phases in all directions through all open points.
     */
    fun newTrace(): BasicTraversal<PhaseStep> = newTrace(OpenTest.IGNORE_OPEN)

    /**
     * @return a traversal that traces along phases in all directions stopping at normally open points.
     */
    fun newNormalTrace(): BasicTraversal<PhaseStep> = newTrace(OpenTest.NORMALLY_OPEN)

    /**
     * @return a traversal that traces along phases in all directions stopping at currently open points.
     */
    fun newCurrentTrace(): BasicTraversal<PhaseStep> = newTrace(OpenTest.CURRENTLY_OPEN)

    /**
     * @return a traversal that traces along phases in a downstream direction stopping at normally open points.
     */
    fun newNormalDownstreamTrace(): BasicTraversal<PhaseStep> = newDownstreamTrace(OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES)

    /**
     * @return a traversal that traces along phases in a downstream direction stopping at currently open points.
     */
    fun newCurrentDownstreamTrace(): BasicTraversal<PhaseStep> = newDownstreamTrace(OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES)

    /**
     * @return a traversal that traces along phases in a upstream direction stopping at normally open points.
     */
    fun newNormalUpstreamTrace(): BasicTraversal<PhaseStep> = newUpstreamTrace(OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES)

    /**
     * @return a traversal that traces along phases in a upstream direction stopping at currently open points.
     */
    fun newCurrentUpstreamTrace(): BasicTraversal<PhaseStep> = newUpstreamTrace(OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES)

    private fun newTrace(isOpenTest: OpenTest): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNext(isOpenTest), processQueue { it.phases.size }, PhaseStepTracker())

    private fun newDownstreamTrace(isOpenTest: OpenTest, activePhases: PhaseSelector): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNextDownstream(isOpenTest, activePhases), processQueue { it.phases.size }, PhaseStepTracker())

    private fun newUpstreamTrace(isOpenTest: OpenTest, activePhases: PhaseSelector): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNextUpstream(isOpenTest, activePhases), processQueue { it.phases.size }, PhaseStepTracker())

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->
            val outPhases = mutableSetOf<SinglePhaseKind>()

            phaseStep.conductingEquipment.terminals.forEach {
                outPhases.clear()
                for (phase in phaseStep.phases) {
                    if (!openTest.isOpen(phaseStep.conductingEquipment, phase)) {
                        outPhases.add(phase)
                    }
                }

                queueConnected(traversal, it, outPhases)
            }
        }

    private fun queueNextDownstream(openTest: OpenTest, activePhases: PhaseSelector): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->
            val outPhases = mutableSetOf<SinglePhaseKind>()

            phaseStep.conductingEquipment.terminals.forEach(Consumer { terminal: Terminal ->
                outPhases.clear()
                getPhasesWithDirection(openTest, activePhases, terminal, phaseStep.phases, PhaseDirection.OUT, outPhases)

                queueConnected(traversal, terminal, outPhases)
            })
        }

    private fun queueConnected(traversal: BasicTraversal<PhaseStep>, terminal: Terminal, outPhases: Set<SinglePhaseKind>) {
        if (outPhases.isNotEmpty()) {
            connectedTerminals(terminal, outPhases).forEach {
                tryQueue(traversal, it, it.toNominalPhases)
            }
        }
    }

    private fun queueNextUpstream(openTest: OpenTest, activePhases: PhaseSelector): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->
            val inPhases = mutableSetOf<SinglePhaseKind>()

            phaseStep.conductingEquipment.terminals.forEach { terminal ->
                inPhases.clear()
                getPhasesWithDirection(openTest, activePhases, terminal, phaseStep.phases, PhaseDirection.IN, inPhases)
                if (inPhases.isNotEmpty()) {
                    connectedTerminals(terminal, inPhases).forEach { cr ->
                        // When going upstream, we only want to traverse to connected terminals that have an out direction
                        val outPhases = cr.toNominalPhases
                            .filter { phase: SinglePhaseKind? -> activePhases.status(cr.toTerminal, phase!!).direction.has(PhaseDirection.OUT) }
                            .toSet()

                        if (outPhases.isNotEmpty())
                            tryQueue(traversal, cr, outPhases)
                    }
                }
            }
        }

    private fun tryQueue(traversal: BasicTraversal<PhaseStep>, cr: ConnectivityResult, outPhases: Collection<SinglePhaseKind>) {
        cr.to?.let { traversal.queue.add(continueAt(it, outPhases, cr.from)) }
    }

    private fun getPhasesWithDirection(
        openTest: OpenTest,
        activePhases: PhaseSelector,
        terminal: Terminal,
        candidatePhases: Set<SinglePhaseKind>,
        direction: PhaseDirection,
        matchedPhases: MutableSet<SinglePhaseKind>
    ) {
        val conductingEquipment = Objects.requireNonNull(terminal.conductingEquipment)!!
        for (phase in candidatePhases) {
            if (terminal.phases.singlePhases().contains(phase) && !openTest.isOpen(conductingEquipment, phase)) {
                if (activePhases.status(terminal, phase).direction.has(direction)) {
                    matchedPhases.add(phase)
                }
            }
        }
    }

}

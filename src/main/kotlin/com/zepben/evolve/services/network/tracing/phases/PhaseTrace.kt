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
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseStep.Companion.continueAt
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.processQueue

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
    fun newNormalDownstreamTrace(): BasicTraversal<PhaseStep> =
        newDownstreamTrace(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)

    /**
     * @return a traversal that traces along phases in a downstream direction stopping at currently open points.
     */
    fun newCurrentDownstreamTrace(): BasicTraversal<PhaseStep> =
        newDownstreamTrace(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)

    /**
     * @return a traversal that traces along phases in an upstream direction stopping at normally open points.
     */
    fun newNormalUpstreamTrace(): BasicTraversal<PhaseStep> =
        newUpstreamTrace(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)

    /**
     * @return a traversal that traces along phases in an upstream direction stopping at currently open points.
     */
    fun newCurrentUpstreamTrace(): BasicTraversal<PhaseStep> =
        newUpstreamTrace(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)

    private fun newTrace(isOpenTest: OpenTest): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNext(isOpenTest), processQueue { it.phases.size }, PhaseStepTracker())

    private fun newDownstreamTrace(isOpenTest: OpenTest, activeDirection: DirectionSelector): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNextDownstream(isOpenTest, activeDirection), processQueue { it.phases.size }, PhaseStepTracker())

    private fun newUpstreamTrace(isOpenTest: OpenTest, activeDirection: DirectionSelector): BasicTraversal<PhaseStep> =
        BasicTraversal(queueNextUpstream(isOpenTest, activeDirection), processQueue { it.phases.size }, PhaseStepTracker())

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->
            val downPhases = mutableSetOf<SinglePhaseKind>()

            phaseStep.conductingEquipment.terminals.forEach {
                downPhases.clear()
                for (phase in phaseStep.phases) {
                    if (!openTest.isOpen(phaseStep.conductingEquipment, phase)) {
                        downPhases.add(phase)
                    }
                }

                queueConnected(traversal, it, downPhases)
            }
        }

    private fun queueNextDownstream(openTest: OpenTest, activeDirection: DirectionSelector): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->
            phaseStep.conductingEquipment.terminals.forEach {
                queueConnected(traversal, it, getPhasesWithDirection(openTest, activeDirection, it, phaseStep.phases, FeederDirection.DOWNSTREAM))
            }
        }

    private fun queueConnected(traversal: BasicTraversal<PhaseStep>, terminal: Terminal, downPhases: Set<SinglePhaseKind>) {
        if (downPhases.isNotEmpty()) {
            connectedTerminals(terminal, downPhases).forEach {
                tryQueue(traversal, it, it.toNominalPhases)
            }
        }
    }

    private fun queueNextUpstream(openTest: OpenTest, activeDirection: DirectionSelector): BasicTraversal.QueueNext<PhaseStep> =
        BasicTraversal.QueueNext { phaseStep, traversal ->

            phaseStep.conductingEquipment.terminals.forEach { terminal ->
                val upPhases = getPhasesWithDirection(openTest, activeDirection, terminal, phaseStep.phases, FeederDirection.UPSTREAM)
                if (upPhases.isNotEmpty()) {
                    connectedTerminals(terminal, upPhases).forEach { cr ->
                        // When going upstream, we only want to traverse to connected terminals that have a DOWNSTREAM direction
                        if (activeDirection.select(cr.toTerminal).value.has(FeederDirection.DOWNSTREAM))
                            tryQueue(traversal, cr, cr.toNominalPhases)
                    }
                }
            }
        }

    private fun tryQueue(traversal: BasicTraversal<PhaseStep>, cr: ConnectivityResult, downPhases: Collection<SinglePhaseKind>) {
        cr.to?.let { traversal.queue.add(continueAt(it, downPhases, cr.from)) }
    }

    private fun getPhasesWithDirection(
        openTest: OpenTest,
        activeDirection: DirectionSelector,
        terminal: Terminal,
        candidatePhases: Set<SinglePhaseKind>,
        direction: FeederDirection
    ): Set<SinglePhaseKind> {
        val matchedPhases = mutableSetOf<SinglePhaseKind>()

        if (!activeDirection.select(terminal).value.has(direction))
            return matchedPhases

        val conductingEquipment = terminal.conductingEquipment!!
        for (phase in candidatePhases) {
            if (terminal.phases.singlePhases.contains(phase) && !openTest.isOpen(conductingEquipment, phase))
                matchedPhases.add(phase)
        }

        return matchedPhases
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.WeightedPriorityQueue.Companion.branchQueue
import com.zepben.evolve.services.network.tracing.traversalV2.WeightedPriorityQueue.Companion.processQueue

/**
 * Convenience class that provides methods for removing phases on a [NetworkService]
 * This class is backed by a [NetworkTrace].
 */
class RemovePhases(
    val stateOperators: NetworkStateOperators
) {

    /**
     * Remove all traced phases from the specified network.
     *
     * @param networkService The network service to remove traced phasing from.
     */
    fun run(networkService: NetworkService) {
        networkService.sequenceOf<Terminal>().forEach {
            it.normalPhases.phaseStatusInternal = 0u
            it.currentPhases.phaseStatusInternal = 0u
        }
    }

    /**
     * Allows the removal of traced phases from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the phase removal.
     * @param nominalPhasesToEbb The nominal phases to remove traced phasing from. Defaults to all phases.
     */
    @JvmOverloads
    fun run(terminal: Terminal, nominalPhasesToEbb: PhaseCode = terminal.phases) {
        run(terminal, nominalPhasesToEbb.singlePhases.toSet())
    }

    /**
     * Allows the removal of traced phases from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the phase removal.
     * @param nominalPhasesToEbb The nominal phases to remove traced phasing from.
     */
    fun run(terminal: Terminal, nominalPhasesToEbb: Set<SinglePhaseKind>) {
        createTrace().run(terminal, EbbPhases(nominalPhasesToEbb), terminal.phases)
    }

    private fun createTrace(): NetworkTrace<EbbPhases> =
        Tracing.connectedTerminalTrace(
            networkStateOperators = stateOperators,
            queueFactory = { processQueue { it.data.phasesToEbb.size } },
            branchQueueFactory = { branchQueue { it.data.phasesToEbb.size } },
            computeNextT = ::computeNextEbbPhases
        )
            .addStepAction { (path, ebbPhases), _ ->
                ebbPhases.ebbedPhases = ebb(path.toTerminal, ebbPhases.phasesToEbb)
            }
            .addQueueCondition { nextStep, _ ->
                nextStep.data.phasesToEbb.isNotEmpty()
            }

    private fun computeNextEbbPhases(step: NetworkTraceStep<EbbPhases>, context: StepContext, nextPath: StepPath): EbbPhases {
        val phasesToEbb = nextPath.nominalPhasePaths.asSequence().map { it.to }.filter { it in step.data.phasesToEbb }.toSet()
        return EbbPhases(phasesToEbb)
    }

    private fun ebb(terminal: Terminal, phasesToEbb: Set<SinglePhaseKind>): Set<SinglePhaseKind> {
        val phases = stateOperators.phaseStatus(terminal)
        return phasesToEbb
            .asSequence()
            .filter { phases[it] != SinglePhaseKind.NONE }
            .toSet()
            .onEach { phases[it] = SinglePhaseKind.NONE }
    }

    private class EbbPhases(val phasesToEbb: Set<SinglePhaseKind>, var ebbedPhases: Set<SinglePhaseKind> = emptySet())

}

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
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue.Companion.branchQueue
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue.Companion.processQueue

/**
 * Convenience class that provides methods for removing phases on a [NetworkService]
 * This class is backed by a [NetworkTrace].
 */
class RemovePhases {

    /**
     * Remove all traced phases from the specified network.
     *
     * @param networkService The network service to remove traced phasing from.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing phases.
     */
    @JvmOverloads
    fun run(networkService: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        networkService.sequenceOf<Terminal>().forEach {
            networkStateOperators.phaseStatus(it).phaseStatusInternal = 0u
        }
    }

    /**
     * Removes all nominal phases a terminal traced phases and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the phase removal.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing phases.
     */
    @JvmOverloads
    fun run(terminal: Terminal, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        run(terminal, terminal.phases, networkStateOperators)
    }

    /**
     * Allows the removal of traced phases from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the phase removal.
     * @param nominalPhasesToEbb The nominal phases to remove traced phasing from. Defaults to all phases.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing phases.
     */
    @JvmOverloads
    fun run(terminal: Terminal, nominalPhasesToEbb: PhaseCode, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        run(terminal, nominalPhasesToEbb.singlePhases.toSet(), networkStateOperators)
    }

    /**
     * Allows the removal of traced phases from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the phase removal.
     * @param nominalPhasesToEbb The nominal phases to remove traced phasing from.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing phases.
     */
    fun run(terminal: Terminal, nominalPhasesToEbb: Set<SinglePhaseKind>, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        createTrace(networkStateOperators).run(terminal, EbbPhases(nominalPhasesToEbb), terminal.phases)
    }

    private fun createTrace(stateOperators: NetworkStateOperators): NetworkTrace<EbbPhases> =
        Tracing.networkTraceBranching(
            networkStateOperators = stateOperators,
            actionStepType = NetworkTraceActionType.ALL_STEPS,
            queueFactory = { processQueue { it.data.phasesToEbb.size } },
            branchQueueFactory = { branchQueue { it.data.phasesToEbb.size } },
            computeData = ::computeNextEbbPhases
        )
            .addStepAction { (path, ebbPhases), _ ->
                ebbPhases.ebbedPhases = ebb(stateOperators, path.toTerminal, ebbPhases.phasesToEbb)
            }
            .addQueueCondition { nextStep, _, _, _ ->
                nextStep.data.phasesToEbb.isNotEmpty()
            }

    @Suppress("UNUSED_PARAMETER")
    private fun computeNextEbbPhases(step: NetworkTraceStep<EbbPhases>, context: StepContext, nextPath: NetworkTraceStep.Path): EbbPhases {
        val phasesToEbb = nextPath.nominalPhasePaths.asSequence().map { it.to }.filter { it in step.data.phasesToEbb }.toSet()
        return EbbPhases(phasesToEbb)
    }

    private fun ebb(stateOperators: NetworkStateOperators, terminal: Terminal, phasesToEbb: Set<SinglePhaseKind>): Set<SinglePhaseKind> {
        val phases = stateOperators.phaseStatus(terminal)
        return phasesToEbb
            .asSequence()
            .filter { phases[it] != SinglePhaseKind.NONE }
            .toSet()
            .onEach { phases[it] = SinglePhaseKind.NONE }
    }

    private class EbbPhases(val phasesToEbb: Set<SinglePhaseKind>, var ebbedPhases: Set<SinglePhaseKind> = emptySet())

}

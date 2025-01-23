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
import com.zepben.evolve.cim.iec61970.base.wires.EnergySource
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityInternal
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting phases on a [NetworkService]
 * This class is backed by a [NetworkTrace].
 */
class SetPhases {

    private class PhasesToFlow(val nominalPhasePaths: List<NominalPhasePath>, var stepFlowedPhases: Boolean = false)

    /**
     * Apply phases and flow from all energy sources in the network.
     * This will apply [Terminal.phases] to all terminals on each [EnergySource] and then flow along the connected network.
     *
     * @param network The network in which to apply phases.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @JvmOverloads
    fun run(network: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        val trace = createNetworkTrace(networkStateOperators)
        network.sequenceOf<EnergySource>()
            .flatMap { it.terminals.asSequence() }
            .forEach {
                applyPhases(networkStateOperators, it, it.phases.singlePhases)
                runTerminal(it, networkStateOperators, trace)
            }
    }

    /**
     * Apply phases to the [terminal] and flow.
     *
     * @param terminal The terminal to start applying phases from.
     * @param phases The phases to apply. Must only contain ABCN.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @Throws(IllegalArgumentException::class)
    @JvmOverloads
    fun run(terminal: Terminal, phases: PhaseCode, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        run(terminal, phases.singlePhases, networkStateOperators)
    }

    /**
     * Apply phases to the [terminal] and flow.
     *
     * @param terminal The terminal to start applying phases from.
     * @param phases The phases to apply. Must only contain ABCN.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @Throws(IllegalArgumentException::class)
    @JvmOverloads
    fun run(terminal: Terminal, phases: List<SinglePhaseKind>, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        if (phases.size != terminal.phases.singlePhases.size) {
            throw IllegalArgumentException(
                "Attempted to apply phases $phases to $terminal with nominal phases ${terminal.phases}. " +
                    "Number of phases to apply must match the number of nominal phases. Found ${phases.size}, expected ${terminal.phases.singlePhases.size}"
            )
        }

        applyPhases(networkStateOperators, terminal, phases)
        runTerminal(terminal, networkStateOperators)
    }

    /**
     * Spread phases from the [seedTerminal] to the [startTerminal] and flow. The [seedTerminal] and [startTerminal] must have the same [Terminal.conductingEquipment].
     *
     * @param seedTerminal The terminal to from which to spread phases.
     * @param startTerminal The terminal to spread phases to and start the trace from.
     * @param phases The nominal phases on which to spread phases from the seed terminal.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @JvmOverloads
    fun run(
        seedTerminal: Terminal,
        startTerminal: Terminal,
        phases: List<SinglePhaseKind>,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL
    ) {
        val nominalPhasePaths = getNominalPhasePaths(networkStateOperators, seedTerminal, startTerminal, phases.asSequence())
        if (flowPhases(networkStateOperators, seedTerminal, startTerminal, nominalPhasePaths)) {
            run(startTerminal, networkStateOperators)
        }
    }

    /**
     * Flow phases already set on the given [terminal].
     */
    @JvmOverloads
    fun run(terminal: Terminal, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        runTerminal(terminal, networkStateOperators)
    }

    /**
     * Apply nominal phases from the [fromTerminal] to the [toTerminal].
     *
     * @param fromTerminal The terminal to from which to spread phases.
     * @param toTerminal The terminal to spread phases to.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @JvmOverloads
    fun spreadPhases(
        fromTerminal: Terminal,
        toTerminal: Terminal,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL
    ) {
        spreadPhases(fromTerminal, toTerminal, fromTerminal.phases.singlePhases, networkStateOperators)
    }

    /**
     * Apply phases from the [fromTerminal] to the [toTerminal].
     *
     * @param fromTerminal The terminal to from which to spread phases.
     * @param toTerminal The terminal to spread phases to.
     * @param phases The nominal phases on which to spread phases.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @JvmOverloads
    fun spreadPhases(
        fromTerminal: Terminal,
        toTerminal: Terminal,
        phases: List<SinglePhaseKind>,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL
    ) {
        val paths = getNominalPhasePaths(networkStateOperators, fromTerminal, toTerminal, phases.asSequence())
        flowPhases(networkStateOperators, fromTerminal, toTerminal, paths)
    }

    private fun runTerminal(
        terminal: Terminal,
        networkStateOperators: NetworkStateOperators,
        trace: NetworkTrace<PhasesToFlow> = createNetworkTrace(networkStateOperators)
    ) {
        val nominalPhasePaths = terminal.phases.map { NominalPhasePath(SinglePhaseKind.NONE, it) }
        trace.run(terminal, PhasesToFlow(nominalPhasePaths), canStopOnStartItem = false)
        trace.reset()
    }

    private fun List<NominalPhasePath>.toPhases(): Sequence<SinglePhaseKind> = this.asSequence().map { it.to }

    private fun createNetworkTrace(stateOperators: NetworkStateOperators): NetworkTrace<PhasesToFlow> = Tracing.networkTraceBranching(
        networkStateOperators = stateOperators,
        actionStepType = NetworkTraceActionType.ALL_STEPS,
        queueFactory = { WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() } },
        branchQueueFactory = { WeightedPriorityQueue.branchQueue { it.path.toTerminal.phases.numPhases() } },
        computeData = computeNextPhasesToFlow(stateOperators)
    )
        .addQueueCondition { nextStep, _, _, _ ->
            nextStep.data.nominalPhasePaths.isNotEmpty()
        }
        .addStepAction { (path, phasesToFlow), ctx ->
            // We always assume the first step terminal already has the phases applied, so we don't do anything on the first step
            phasesToFlow.stepFlowedPhases = if (!ctx.isStartItem) {
                flowPhases(stateOperators, path.fromTerminal, path.toTerminal, phasesToFlow.nominalPhasePaths)
            } else {
                true
            }
        }

    private fun computeNextPhasesToFlow(stateOperators: NetworkStateOperators): ComputeData<PhasesToFlow> =
        ComputeData { step, _, nextPath ->
            // If the current step didn't flow any phases, we don't attempt to flow any further.
            if (!step.data.stepFlowedPhases) {
                PhasesToFlow(emptyList())
            } else {
                val phasePaths = getNominalPhasePaths(stateOperators, nextPath.fromTerminal, nextPath.toTerminal, step.data.nominalPhasePaths.toPhases())
                PhasesToFlow(phasePaths)
            }
        }


    private fun applyPhases(stateOperators: NetworkStateOperators, terminal: Terminal, phases: List<SinglePhaseKind>) {
        val tracedPhases = stateOperators.phaseStatus(terminal)
        terminal.phases.singlePhases.forEachIndexed { index, nominalPhase ->
            tracedPhases[nominalPhase] = phases[index].takeUnless { it in PhaseCode.XY } ?: SinglePhaseKind.NONE
        }
    }

    private fun getNominalPhasePaths(
        stateOperators: NetworkStateOperators,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        phases: Sequence<SinglePhaseKind>
    ): List<NominalPhasePath> {
        val tracedInternally = fromTerminal.conductingEquipment == toTerminal.conductingEquipment
        val phasesToFlow = getPhasesToFlow(stateOperators, fromTerminal, phases, tracedInternally)

        return if (tracedInternally) {
            TerminalConnectivityInternal.between(fromTerminal, toTerminal, phasesToFlow).nominalPhasePaths
        } else {
            TerminalConnectivityConnected.terminalConnectivity(fromTerminal, toTerminal, phasesToFlow).nominalPhasePaths
        }
    }

    private fun getPhasesToFlow(
        stateOperators: NetworkStateOperators,
        terminal: Terminal,
        phases: Sequence<SinglePhaseKind>,
        internalFlow: Boolean
    ): Set<SinglePhaseKind> =
        if (internalFlow) {
            terminal.conductingEquipment?.let { ce -> phases.filter { !stateOperators.isOpen(ce, it) }.toSet() } ?: emptySet()
        } else {
            phases.toSet()
        }

    private fun flowPhases(
        stateOperators: NetworkStateOperators,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        nominalPhasePaths: List<NominalPhasePath>,
    ): Boolean {
        val fromPhases = stateOperators.phaseStatus(fromTerminal)
        val toPhases = stateOperators.phaseStatus(toTerminal)

        var changedPhases = false
        for ((from, to) in nominalPhasePaths) {
            try {
                // If the path comes from NONE, then we want to apply the `to phase`.
                val phase = if (from != SinglePhaseKind.NONE)
                    fromPhases[from]
                else if (to !in PhaseCode.XY)
                    to
                else
                    toPhases[to]

                if ((phase != SinglePhaseKind.NONE) && toPhases.set(to, phase))
                    changedPhases = true
            } catch (ex: UnsupportedOperationException) {
                val phaseDesc = if (from == to)
                    "$from"
                else
                    "path $from to $to"

                val terminalDesc = if (fromTerminal.conductingEquipment == toTerminal.conductingEquipment)
                    "from $fromTerminal to $toTerminal through ${fromTerminal.conductingEquipment?.typeNameAndMRID()}"
                else
                    "between $fromTerminal on ${fromTerminal.conductingEquipment?.typeNameAndMRID()} and $toTerminal on ${toTerminal.conductingEquipment?.typeNameAndMRID()}"

                throw IllegalStateException(
                    "Attempted to flow conflicting phase ${fromPhases[from]} onto ${toPhases[to]} on nominal phase $phaseDesc. This occurred while " +
                        "flowing $terminalDesc. This is caused by missing open points, or incorrect phases in upstream equipment that should be " +
                        "corrected in the source data."
                )
            }
        }
        return changedPhases
    }
}

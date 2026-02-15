/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.EnergySource
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.ewb.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.ewb.services.network.tracing.connectivity.TerminalConnectivityInternal
import com.zepben.ewb.services.network.tracing.connectivity.TransformerPhasePaths
import com.zepben.ewb.services.network.tracing.networktrace.ComputeData
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTrace
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.ewb.services.network.tracing.networktrace.Tracing
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.traversal.WeightedPriorityQueue
import org.slf4j.Logger

/**
 * Convenience class that provides methods for setting phases on a [NetworkService]
 * This class is backed by a [NetworkTrace].
 */
class SetPhases(
    private val debugLogger: Logger?
) {

    private class PhasesToFlow(val nominalPhasePaths: List<NominalPhasePath>, var stepFlowedPhases: Boolean = false) {
        override fun toString(): String =
            "PhasesToFlow(nominalPhasePaths=$nominalPhasePaths, stepFlowedPhases=$stepFlowedPhases)"
    }

    /**
     * Apply phases and flow from all energy sources in the network.
     * This will apply [Terminal.phases] to all terminals on each [EnergySource] and then flow along the connected network.
     *
     * @param network The network in which to apply phases.
     * @param networkStateOperators The [NetworkStateOperators] to be used when setting phases.
     */
    @JvmOverloads
    fun run(network: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        runTerminals(
            network.sequenceOf<EnergySource>()
                .flatMap { it.terminals.asSequence() }
                .onEach { applyPhases(networkStateOperators, it, it.phases.singlePhases) }
                .toList(),
            networkStateOperators
        )
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
        runTerminals(listOf(terminal), networkStateOperators)
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
        runTerminals(listOf(terminal), networkStateOperators)
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

    private fun runTerminals(terminals: List<Terminal>, networkStateOperators: NetworkStateOperators) {
        val partiallyEnergisedTransformers = mutableSetOf<PowerTransformer>()
        val trace = createNetworkTrace(networkStateOperators, partiallyEnergisedTransformers)

        terminals.forEach { trace.runTerminal(it) }

        // Go back and add any missing phases to transformers that were energised from a downstream side with fewer phases
        // when they were in parallel, that successfully energised all the upstream side phases. This setup stops the spread
        // from coming back down the upstream (it is fully energised) and processing the transformer correctly.
        debugLogger?.info("Reprocessing partially energised transformers...")
        partiallyEnergisedTransformers.asSequence()
            .map { tx -> tx.terminals.map { it to networkStateOperators.notFullyEnergised(it) } }
            .filter { terminalsByEnergisation -> terminalsByEnergisation.any { it.second } }
            .forEach { terminalsByEnergisation ->
                val (partiallyEnergised, fullyEnergised) = terminalsByEnergisation.partition { it.second }
                partiallyEnergised.forEach { (partial) ->
                    fullyEnergised.forEach { (full) ->
                        flowTransformerPhases(networkStateOperators, full, partial, allowSuspectFlow = true)
                    }

                    // Now we have repaired the partially energised terminals, continue applying it downstream.
                    trace.runTerminal(partial)
                }
            }
        debugLogger?.info("Reprocessing complete.")
    }

    private fun NetworkTrace<PhasesToFlow>.runTerminal(terminal: Terminal) {
        run(terminal, PhasesToFlow(terminal.phases.map { NominalPhasePath(SinglePhaseKind.NONE, it) }), canStopOnStartItem = false)

        // This is called in a loop so we need to reset it for each call. We choose to do this after to release the memory used by the trace once
        // it is finished, rather than before, which has would be marginally quicker on the first call, but would hold onto the memory as long
        // as the `SetPhases` instance is referenced.
        reset()
    }

    private fun List<NominalPhasePath>.toPhases(): Sequence<SinglePhaseKind> = this.asSequence().map { it.to }

    private fun createNetworkTrace(
        stateOperators: NetworkStateOperators,
        partiallyEnergisedTransformers: MutableSet<PowerTransformer>
    ): NetworkTrace<PhasesToFlow> =
        Tracing.networkTraceBranching(
            networkStateOperators = stateOperators,
            actionStepType = NetworkTraceActionType.ALL_STEPS,
            debugLogger,
            name = "SetPhases(${stateOperators.description})",
            queueFactory = { WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() } },
            branchQueueFactory = { WeightedPriorityQueue.branchQueue { it.path.toTerminal.phases.numPhases() } },
            computeData = computeNextPhasesToFlow(stateOperators)
        )
            .addQueueCondition { nextStep, _, _, _ -> nextStep.data.nominalPhasePaths.isNotEmpty() }
            .addStepAction { (path, phasesToFlow), ctx ->
                // We always assume the first step terminal already has the phases applied, so we don't do anything on the first step
                phasesToFlow.stepFlowedPhases =
                    ctx.isStartItem || flowPhases(stateOperators, path.fromTerminal, path.toTerminal, phasesToFlow.nominalPhasePaths)

                // If we flowed phases but failed to completely energise a transformer, keep track of it for reprocessing later.
                if (phasesToFlow.stepFlowedPhases && (path.toEquipment is PowerTransformer) && stateOperators.notFullyEnergised(path.toTerminal))
                    partiallyEnergisedTransformers.add(path.toEquipment)
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
        phases: Sequence<SinglePhaseKind> = fromTerminal.phases.singlePhases.asSequence()
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
    ): Boolean =
        if ((fromTerminal.conductingEquipment == toTerminal.conductingEquipment) && (fromTerminal.conductingEquipment is PowerTransformer))
            flowTransformerPhases(stateOperators, fromTerminal, toTerminal, nominalPhasePaths, allowSuspectFlow = false)
        else
            flowStraightPhases(stateOperators, fromTerminal, toTerminal, nominalPhasePaths)

    private fun flowStraightPhases(
        stateOperators: NetworkStateOperators,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        nominalPhasePaths: List<NominalPhasePath>,
    ): Boolean {
        val fromPhases = stateOperators.phaseStatus(fromTerminal)
        val toPhases = stateOperators.phaseStatus(toTerminal)

        var updatedPhases = false

        nominalPhasePaths.forEach { (from, to) ->
            trySetPhase(fromPhases[from], fromTerminal, fromPhases, from, toTerminal, toPhases, to) { updatedPhases = true }
        }

        return updatedPhases
    }

    private fun flowTransformerPhases(
        stateOperators: NetworkStateOperators,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        nominalPhasePaths: List<NominalPhasePath>? = null,
        allowSuspectFlow: Boolean
    ): Boolean {
        val paths = nominalPhasePaths ?: getNominalPhasePaths(stateOperators, fromTerminal, toTerminal)

        // If this transformer doesn't mess with phases (or only adds or removes a neutral), just use the straight processor. We use the number of phases
        // rather than the phases themselves to correctly handle the shift from known to unknown phases. e.g. AB -> XY.
        if (fromTerminal.phases.withoutNeutral().numPhases() == toTerminal.phases.withoutNeutral().numPhases())
            return flowTransformerPhasesAddingNeutral(stateOperators, fromTerminal, toTerminal, paths)

        val fromPhases = stateOperators.phaseStatus(fromTerminal)
        val toPhases = stateOperators.phaseStatus(toTerminal)

        // Split the phases into ones we need to flow directly, and ones that have been added by a transformer. In the case of an added Y
        // phase (SWER -> LV2 transformer) we need to flow the phases before we can calculate the missing phase.
        val (flowPhases, addPhases) = paths.partition { it.from != SinglePhaseKind.NONE }
        var updatedPhases = false
        flowPhases.forEach { (from, to) ->
            trySetPhase(fromPhases[from], fromTerminal, fromPhases, from, toTerminal, toPhases, to) { updatedPhases = true }
        }
        addPhases.forEach { (_, to) ->
            tryAddPhase(fromTerminal, fromPhases, toTerminal, toPhases, to, allowSuspectFlow) { updatedPhases = true }
        }

        return updatedPhases
    }

    private fun flowTransformerPhasesAddingNeutral(
        stateOperators: NetworkStateOperators,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        paths: List<NominalPhasePath>,
    ): Boolean {
        val updatedPhases = flowStraightPhases(stateOperators, fromTerminal, toTerminal, paths.filter { it != TransformerPhasePaths.addNeutral })

        // Only add the neutral if we added a phases to the transformer, otherwise you will flag an energised neutral with no active phases. We
        // check to see if we need to add the neutral to prevent adding it when we traverse through the transformer in the opposite direction.
        if (updatedPhases && (TransformerPhasePaths.addNeutral in paths))
            stateOperators.phaseStatus(toTerminal)[SinglePhaseKind.N] = SinglePhaseKind.N

        return updatedPhases
    }

    private fun trySetPhase(
        phase: SinglePhaseKind,
        fromTerminal: Terminal,
        fromPhases: PhaseStatus,
        from: SinglePhaseKind,
        toTerminal: Terminal,
        toPhases: PhaseStatus,
        to: SinglePhaseKind,
        onSuccess: () -> Unit
    ) {
        try {
            if ((phase != SinglePhaseKind.NONE) && toPhases.set(to, phase)) {
                debugLogger?.info("   ${fromTerminal.mRID}[$from] -> ${toTerminal.mRID}[$to]: set to $phase")
                onSuccess()
            }
        } catch (_: UnsupportedOperationException) {
            throwCrossPhaseException(fromTerminal, fromPhases, from, toTerminal, toPhases, to)
        }
    }

    private fun tryAddPhase(
        fromTerminal: Terminal,
        fromPhases: PhaseStatus,
        toTerminal: Terminal,
        toPhases: PhaseStatus,
        to: SinglePhaseKind,
        allowSuspectFlow: Boolean,
        onSuccess: () -> Unit
    ) {
        // The phases that can be added are ABCN and Y, so for all cases other than Y we can just use the added phase. For Y we need to look
        // at what the phases on the other side of the transformer are to determine what has been added.
        val phase = when (to) {
            SinglePhaseKind.Y -> toPhases[to].unlessNone() ?: fromPhases[fromTerminal.phases.singlePhases.first()].toYPhase(allowSuspectFlow)
            else -> to
        }

        trySetPhase(phase, fromTerminal, fromPhases, SinglePhaseKind.NONE, toTerminal, toPhases, to, onSuccess)
    }

    private fun throwCrossPhaseException(
        fromTerminal: Terminal,
        fromPhases: PhaseStatus,
        from: SinglePhaseKind,
        toTerminal: Terminal,
        toPhases: PhaseStatus,
        to: SinglePhaseKind
    ): Nothing {
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
                "flowing $terminalDesc. This is often caused by missing open points, or incorrect phases in upstream equipment that should be " +
                "corrected in the source data."
        )
    }

    private fun NetworkStateOperators.notFullyEnergised(terminal: Terminal): Boolean =
        phaseStatus(terminal).let { phasesStatus ->
            terminal.phases.singlePhases.any { phasesStatus[it] == SinglePhaseKind.NONE }
        }

    private fun SinglePhaseKind.unlessNone(): SinglePhaseKind? =
        takeUnless { it == SinglePhaseKind.NONE }

    //
    // NOTE: If we are adding Y to a C <-> XYN transformer we will leave it de-energised to prevent cross-phase energisation when there is
    //       a parallel C to XN transformer. This can be changed if the entire way XY mappings are reworked to use traced phases instead
    //       of the X and Y, which includes in straight paths to prevent cross-phase wiring.
    //
    //       Due to both AB and AC energising X with A, until the above is fixed we don't know which one we are using, so if we aren't allowing
    //       suspect flows we will also leave it de-energised to prevent cross-phase energisation when you have parallel XY <-> XN transformers
    //       on an AC line (adds B to the Y "C wire"). If we are allowing suspect flows for partially energised transformers on a second pass
    //       we will default these to use AB.
    //
    private fun SinglePhaseKind.toYPhase(allowSuspectFlow: Boolean): SinglePhaseKind = when (this) {
        SinglePhaseKind.A -> if (allowSuspectFlow) SinglePhaseKind.B else SinglePhaseKind.NONE // SinglePhaseKind.B
        SinglePhaseKind.B -> SinglePhaseKind.C
        SinglePhaseKind.C -> SinglePhaseKind.NONE // SinglePhaseKind.A
        else -> SinglePhaseKind.NONE
    }

}

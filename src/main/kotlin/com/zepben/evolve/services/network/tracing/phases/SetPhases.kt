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
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityInternal
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting phases on a [NetworkService]
 * This class is backed by a [BranchRecursiveTraversal].
 */
class SetPhases(
    private val terminalConnectivityInternal: TerminalConnectivityInternal = TerminalConnectivityInternal()
) {

    /**
     * The [BranchRecursiveTraversal] used when tracing the normal state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val normalTraversal: BranchRecursiveTraversal<Terminal> = BranchRecursiveTraversal(
        { current, traversal -> setPhasesAndQueueNext(traversal, current, OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES) },
        { WeightedPriorityQueue.processQueue { it.phases.numPhases() } },
        { BasicTracker() },
        { WeightedPriorityQueue.branchQueue { it.phases.numPhases() } }
    )

    /**
     * The [BranchRecursiveTraversal] used when tracing the current state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val currentTraversal: BranchRecursiveTraversal<Terminal> = BranchRecursiveTraversal(
        { current, traversal -> setPhasesAndQueueNext(traversal, current, OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES) },
        { WeightedPriorityQueue.processQueue { it.phases.numPhases() } },
        { BasicTracker() },
        { WeightedPriorityQueue.branchQueue { it.phases.numPhases() } }
    )

    /**
     * Apply phases from all sources in the network.
     *
     * @param network The network in which to apply phases.
     */
    fun run(network: NetworkService) {
        val terminals = network.sequenceOf<EnergySource>()
            .flatMap { it.terminals.asSequence() }
            .toList()
            .onEach {
                applyPhases(it, PhaseSelector.NORMAL_PHASES, it.phases.singlePhases)
                applyPhases(it, PhaseSelector.CURRENT_PHASES, it.phases.singlePhases)
            }

        run(terminals)
    }

    /**
     * Apply phases from the [terminal].
     *
     * @param terminal The terminal to start applying phases from.
     * @param phases The phases to apply. Must only contain ABCN.
     */
    @JvmOverloads
    @Throws(IllegalArgumentException::class)
    fun run(terminal: Terminal, phases: PhaseCode = terminal.phases) {
        run(terminal, phases.singlePhases)
    }

    /**
     * Apply phases from the [terminal].
     *
     * @param terminal The terminal to start applying phases from.
     * @param phases The phases to apply. Must only contain ABCN.
     */
    @Throws(IllegalArgumentException::class)
    fun run(terminal: Terminal, phases: List<SinglePhaseKind>) {
        if (phases.size != terminal.phases.singlePhases.size) {
            throw IllegalArgumentException(
                "Attempted to apply phases $phases to $terminal with nominal phases ${terminal.phases}. " +
                    "Number of phases to apply must match the number of nominal phases. Found ${phases.size}, expected ${terminal.phases.singlePhases.size}"
            )
        }

        applyPhases(terminal, PhaseSelector.NORMAL_PHASES, phases)
        applyPhases(terminal, PhaseSelector.CURRENT_PHASES, phases)

        normalTraversal.tracker.clear()
        currentTraversal.tracker.clear()

        run(listOf(terminal))
    }

    /**
     * Apply phases from the [terminal] on the selected phases. Only spreads existing phases.
     *
     * @param terminal The terminal to from which to spread phases.
     * @param phaseSelector The selector to use to spread the phases. Must be [PhaseSelector.NORMAL_PHASES] or [PhaseSelector.CURRENT_PHASES]
     *
     * @return True if any phases were spread, otherwise false.
     */
    fun run(terminal: Terminal, phaseSelector: PhaseSelector) {
        when (phaseSelector) {
            PhaseSelector.NORMAL_PHASES -> run(listOf(terminal), normalTraversal, PhaseSelector.NORMAL_PHASES)
            PhaseSelector.CURRENT_PHASES -> run(listOf(terminal), currentTraversal, PhaseSelector.CURRENT_PHASES)
            else -> throw IllegalArgumentException("Invalid PhaseSelector specified. Must be PhaseSelector.NORMAL_PHASES or PhaseSelector.CURRENT_PHASES")
        }
    }

    /**
     * Apply phases from the [fromTerminal] to the [toTerminal].
     *
     * @param fromTerminal The terminal to from which to spread phases.
     * @param toTerminal The terminal to spread phases to.
     * @param phasesToFlow The nominal phases on which to spread phases.
     * @param phaseSelector The selector to use to spread the phases.
     *
     * @return A set of [SinglePhaseKind] that were updated. This will be empty if there were no updates.
     */
    @JvmOverloads
    fun spreadPhases(
        fromTerminal: Terminal,
        toTerminal: Terminal,
        phaseSelector: PhaseSelector,
        phasesToFlow: Set<SinglePhaseKind> = fromTerminal.phases.singlePhases.toSet()
    ): Set<SinglePhaseKind> {
        val cr = terminalConnectivityInternal.between(fromTerminal, toTerminal, phasesToFlow)
        return flowViaPaths(cr, phaseSelector)
    }

    private fun applyPhases(terminal: Terminal, phaseSelector: PhaseSelector, phases: List<SinglePhaseKind>) {
        val tracedPhases = phaseSelector.phases(terminal)

        terminal.phases.singlePhases.forEachIndexed { index, nominalPhase ->
            tracedPhases[nominalPhase] = phases[index].takeUnless { it in PhaseCode.XY } ?: SinglePhaseKind.NONE
        }
    }

    private fun run(startTerminals: List<Terminal>) {
        run(startTerminals, normalTraversal, PhaseSelector.NORMAL_PHASES)
        run(startTerminals, currentTraversal, PhaseSelector.CURRENT_PHASES)
    }

    private fun run(
        startTerminals: List<Terminal>,
        traversal: BranchRecursiveTraversal<Terminal>,
        phaseSelector: PhaseSelector
    ) {
        for (terminal in startTerminals)
            runTerminal(terminal, traversal, phaseSelector)
    }

    private fun runTerminal(start: Terminal, traversal: BranchRecursiveTraversal<Terminal>, phaseSelector: PhaseSelector) {
        runFromTerminal(traversal, start, phaseSelector, start.phases.singlePhases.toSet())
    }

    private fun runFromTerminal(
        traversal: BranchRecursiveTraversal<Terminal>,
        terminal: Terminal,
        phaseSelector: PhaseSelector,
        phasesToFlow: Set<SinglePhaseKind>
    ) {
        traversal.reset().tracker.visit(terminal)

        flowToConnectedTerminalsAndQueue(traversal, terminal, phaseSelector, phasesToFlow)

        traversal.run()
    }

    private fun setPhasesAndQueueNext(traversal: BranchRecursiveTraversal<Terminal>, current: Terminal, openTest: OpenTest, phaseSelector: PhaseSelector) {
        val phasesToFlow = getPhasesToFlow(current, openTest)

        current.conductingEquipment?.terminals?.forEach {
            if (it != current) {
                val phasesFlowed = flowThroughEquipment(traversal, current, it, phaseSelector, phasesToFlow)
                if (phasesFlowed.isNotEmpty())
                    flowToConnectedTerminalsAndQueue(traversal, it, phaseSelector, phasesFlowed)
            }
        }
    }

    private fun flowThroughEquipment(
        traversal: BranchRecursiveTraversal<Terminal>,
        fromTerminal: Terminal,
        toTerminal: Terminal,
        phaseSelector: PhaseSelector,
        phasesToFlow: Set<SinglePhaseKind>
    ): Set<SinglePhaseKind> {
        traversal.tracker.visit(toTerminal)
        return spreadPhases(fromTerminal, toTerminal, phaseSelector, phasesToFlow)
    }

    /**
     * Applies all the [phasesToFlow] from the [fromTerminal] to the connected terminals and queues them.
     */
    private fun flowToConnectedTerminalsAndQueue(
        traversal: BranchRecursiveTraversal<Terminal>,
        fromTerminal: Terminal,
        phaseSelector: PhaseSelector,
        phasesToFlow: Set<SinglePhaseKind>
    ) {
        val connectivityResults = NetworkService.connectedTerminals(fromTerminal, phasesToFlow)

        val useBranchQueue = (connectivityResults.size > 1) || ((fromTerminal.conductingEquipment?.numTerminals() ?: 0) > 2)

        connectivityResults.forEach { cr ->
            if (flowViaPaths(cr, phaseSelector).isNotEmpty()) {
                if (useBranchQueue)
                    traversal.branchQueue.add(traversal.branchSupplier().setStart(cr.toTerminal))
                else
                    traversal.queue.add(cr.toTerminal)
            }
        }
    }

    private fun flowViaPaths(cr: ConnectivityResult, phaseSelector: PhaseSelector): Set<SinglePhaseKind> {
        val fromPhases = phaseSelector.phases(cr.fromTerminal)
        val toPhases = phaseSelector.phases(cr.toTerminal)

        val changedPhases = mutableSetOf<SinglePhaseKind>()
        for ((from, to) in cr.nominalPhasePaths) {
            try {
                // If the path comes from NONE, then we want to apply the `to phase`.
                val phase = if (from != SinglePhaseKind.NONE)
                    fromPhases[from]
                else if (to !in PhaseCode.XY)
                    to
                else
                    toPhases[to]

                if ((phase != SinglePhaseKind.NONE) && toPhases.set(to, phase))
                    changedPhases.add(to)
            } catch (ex: UnsupportedOperationException) {
                val phaseDesc = if (from == to)
                    "$from"
                else
                    "path $from to $to"

                val terminalDesc = if (cr.from == cr.to)
                    "from ${cr.fromTerminal} to ${cr.toTerminal} through ${cr.from?.typeNameAndMRID()}"
                else
                    "between ${cr.fromTerminal} on ${cr.from?.typeNameAndMRID()} and ${cr.toTerminal} on ${cr.to?.typeNameAndMRID()}"

                throw IllegalStateException(
                    "Attempted to flow conflicting phase ${fromPhases[from]} onto ${toPhases[to]} on nominal phase $phaseDesc. This occurred while " +
                        "flowing $terminalDesc. This is caused by missing open points, or incorrect phases in upstream equipment that should be " +
                        "corrected in the source data."
                )
            }
        }
        return changedPhases
    }

    private fun getPhasesToFlow(terminal: Terminal, openTest: OpenTest): MutableSet<SinglePhaseKind> =
        terminal.conductingEquipment?.let { ce ->
            terminal.phases.singlePhases
                .asSequence()
                .filter { !openTest.isOpen(ce, it) }
                .toMutableSet()
        } ?: mutableSetOf()

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.annotations.EverythingIsNonnullByDefault
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.branchQueue
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.processQueue
import java.util.*
import java.util.function.Consumer

/**
 * Convenience class that provides methods for removing phases on a [NetworkService]
 * This class is backed by a [BranchRecursiveTraversal].
 */
@EverythingIsNonnullByDefault
class RemovePhases {

    private val normalTraversal: BranchRecursiveTraversal<EbbPhases> = BranchRecursiveTraversal(
        { current, traversal -> ebbOutAndQueue(traversal, current, PhaseSelector.NORMAL_PHASES) },
        { processQueue { it.numPhases() } },
        { BasicTracker() },
        { branchQueue { it.numPhases() } }
    )

    private val currentTraversal: BranchRecursiveTraversal<EbbPhases> = BranchRecursiveTraversal(
        { current, traversal -> ebbOutAndQueue(traversal, current, PhaseSelector.CURRENT_PHASES) },
        { processQueue { it.numPhases() } },
        { BasicTracker() },
        { branchQueue { it.numPhases() } }
    )

    fun run(start: ConductingEquipment) {
        start.terminals.forEach { run(it) }
    }

    fun run(start: ConductingEquipment, nominalPhasesToEbb: Set<SinglePhaseKind>) {
        start.terminals.forEach { run(it, nominalPhasesToEbb) }
    }

    @JvmOverloads
    fun run(terminal: Terminal, nominalPhasesToEbb: Set<SinglePhaseKind> = terminal.phases.singlePhases().toSet()) {
        val start = EbbPhases(terminal, nominalPhasesToEbb)

        runFromOutTerminal(normalTraversal, start)
        runFromOutTerminal(currentTraversal, start)
    }

    private fun runFromOutTerminal(traversal: BranchRecursiveTraversal<EbbPhases>, start: EbbPhases) {
        traversal.reset()
            .run(start)
    }

    private fun ebbOutAndQueue(traversal: BranchRecursiveTraversal<EbbPhases>, current: EbbPhases?, phaseSelector: PhaseSelector) {
        val processedNominalPhases = ebbPhases(current!!.terminal, current.nominalPhasesToEbb, FeederDirection.DOWNSTREAM, phaseSelector)
        val connectedTerminals = connectedTerminals(current.terminal, processedNominalPhases)

        if (connectedTerminals.isEmpty())
            return

        val terminalsByPhase = mutableMapOf<SinglePhaseKind, MutableSet<ConnectivityResult>>()
        val otherFeedsByPhase = mutableMapOf<SinglePhaseKind, MutableSet<ConnectivityResult>>()
        sortTerminalsByPhase(connectedTerminals, terminalsByPhase, otherFeedsByPhase, phaseSelector)

        //
        // For each nominal phase check the number of other feeds:
        //    0:  remove in phase from all connected terminals.
        //    1:  remove in phase only from the other feed.
        //    2+: do not queue or remove anything else as everything is still being fed.
        //
        // To do this we collect the phases back into a set to avoid multi tracing the network.
        //
        val phasesByTerminalsToEbbAndQueue: MutableMap<Terminal, MutableSet<SinglePhaseKind>> = HashMap()
        processedNominalPhases.forEach(Consumer { phase: SinglePhaseKind ->
            // Check if any of the connected terminals are also feeding the connectivity node.
            val feedTerminals = otherFeedsByPhase.getOrDefault(phase, emptySet())
            if (feedTerminals.isEmpty()) addTerminalPhases(
                terminalsByPhase[phase],
                phase,
                phasesByTerminalsToEbbAndQueue
            ) else if (feedTerminals.size == 1) addTerminalPhases(feedTerminals, phase, phasesByTerminalsToEbbAndQueue)
        })

        phasesByTerminalsToEbbAndQueue.forEach { (terminal: Terminal, phases: Set<SinglePhaseKind>) ->
            val hadInPhases = ebbPhases(terminal, phases, FeederDirection.UPSTREAM, phaseSelector)
            Objects.requireNonNull(terminal.conductingEquipment)!!.terminals.forEach(Consumer { t: Terminal ->
                if (t != terminal) traversal.queue.add(
                    EbbPhases(
                        t,
                        hadInPhases
                    )
                )
            })
        }
    }

    private fun ebbPhases(terminal: Terminal, phases: Set<SinglePhaseKind>, direction: FeederDirection, phaseSelector: PhaseSelector): Set<SinglePhaseKind> {
        val hadPhases: MutableSet<SinglePhaseKind> = mutableSetOf()
        phases.forEach(Consumer { phase: SinglePhaseKind ->
            val status = phaseSelector.status(terminal, phase)
            if (status.remove(status.phase, direction)) hadPhases.add(phase)
        })
        return hadPhases
    }

    private fun sortTerminalsByPhase(
        connectedTerminals: List<ConnectivityResult>,
        terminalsByPhase: MutableMap<SinglePhaseKind, MutableSet<ConnectivityResult>>,
        otherFeedsByPhase: MutableMap<SinglePhaseKind, MutableSet<ConnectivityResult>>,
        phaseSelector: PhaseSelector
    ) {
        connectedTerminals.forEach(
            Consumer { cr: ConnectivityResult ->
                cr.nominalPhasePaths
                    .forEach(Consumer { (from, to) ->
                        terminalsByPhase.computeIfAbsent(from) { mutableSetOf() }
                            .add(cr)
                        if (phaseSelector.status(
                                cr.toTerminal,
                                to
                            ).direction.has(FeederDirection.BOTH)
                        ) otherFeedsByPhase.computeIfAbsent(from) { mutableSetOf() }
                            .add(cr)
                    })
            }
        )
    }

    private fun addTerminalPhases(
        terminals: Set<ConnectivityResult>?,
        phase: SinglePhaseKind,
        phasesByTerminalsToEbbAndQueue: MutableMap<Terminal, MutableSet<SinglePhaseKind>>
    ) {
        if (terminals == null) return
        terminals.forEach(
            Consumer { terminal: ConnectivityResult ->
                phasesByTerminalsToEbbAndQueue.computeIfAbsent(terminal.toTerminal) { mutableSetOf() }
                    .add(terminal.nominalPhasePaths
                        .stream()
                        .filter { (from) -> from === phase }
                        .map(NominalPhasePath::to)
                        .findFirst()
                        .orElse(SinglePhaseKind.NONE)
                    )
            }
        )
    }

    class EbbPhases(val terminal: Terminal, val nominalPhasesToEbb: Set<SinglePhaseKind>) {
        fun numPhases(): Int {
            return nominalPhasesToEbb.size
        }
    }

}

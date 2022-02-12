/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
import com.zepben.evolve.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.branchQueue
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.processQueue

/**
 * Convenience class that provides methods for removing phases on a [NetworkService]
 * This class is backed by a [BranchRecursiveTraversal].
 */
class RemovePhases {

    /**
     * The [BranchRecursiveTraversal] used when tracing the normal state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val normalTraversal = BranchRecursiveTraversal<EbbPhases>(
        { current, traversal -> ebbAndQueue(traversal, current, PhaseSelector.NORMAL_PHASES) },
        { processQueue { it.nominalPhases.size } },
        { BasicTracker() },
        { branchQueue { it.nominalPhases.size } }
    )

    /**
     * The [BranchRecursiveTraversal] used when tracing the current state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    private val currentTraversal = BranchRecursiveTraversal<EbbPhases>(
        { current, traversal -> ebbAndQueue(traversal, current, PhaseSelector.CURRENT_PHASES) },
        { processQueue { it.nominalPhases.size } },
        { BasicTracker() },
        { branchQueue { it.nominalPhases.size } }
    )

    private val traversals = listOf(normalTraversal, currentTraversal)

    /**
     * Remove all traced phases from the specified network.
     *
     * @param networkService The network service to remove traced phasing from.
     */
    fun run(networkService: NetworkService) {
        networkService.sequenceOf<Terminal>().forEach {
            it.tracedPhases.phaseStatusInternal = 0u
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
        traversals.forEach { it.reset().run(EbbPhases(terminal, nominalPhasesToEbb)) }
    }

    private fun ebbAndQueue(traversal: BranchRecursiveTraversal<EbbPhases>, ebbPhases: EbbPhases, phaseSelector: PhaseSelector) {
        val ebbedPhases = ebb(ebbPhases.terminal, ebbPhases.nominalPhases, phaseSelector)

        connectedTerminals(ebbPhases.terminal, ebbPhases.nominalPhases).forEach { cr ->
            queueThroughEquipment(traversal, cr.to, cr.toTerminal, ebbFromConnectedTerminal(ebbedPhases, cr, phaseSelector))
        }
    }

    private fun ebb(terminal: Terminal, phasesToEbb: Set<SinglePhaseKind>, phaseSelector: PhaseSelector): Set<SinglePhaseKind> {
        val phases = phaseSelector.phases(terminal)
        return phasesToEbb
            .asSequence()
            .filter { phases[it] != SinglePhaseKind.NONE }
            .toSet()
            .onEach { phases[it] = SinglePhaseKind.NONE }
    }

    private fun ebbFromConnectedTerminal(phasesToEbb: Set<SinglePhaseKind>, cr: ConnectivityResult, phaseSelector: PhaseSelector): Set<SinglePhaseKind> {
        val connectedPhases = phasesToEbb
            .asSequence()
            .mapNotNull { phase -> cr.nominalPhasePaths.firstOrNull { it.from == phase }?.to }
            .toSet()

        return ebb(cr.toTerminal, connectedPhases, phaseSelector)
    }

    private fun queueThroughEquipment(
        traversal: BranchRecursiveTraversal<EbbPhases>,
        conductingEquipment: ConductingEquipment?,
        terminal: Terminal,
        phasesToEbb: Set<SinglePhaseKind>
    ) {
        conductingEquipment?.apply {
            terminals
                .asSequence()
                .filter { it != terminal }
                .forEach { traversal.queue.add(EbbPhases(it, phasesToEbb)) }
        }
    }

    class EbbPhases(val terminal: Terminal, val nominalPhases: Set<SinglePhaseKind>)

}

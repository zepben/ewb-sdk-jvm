/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting feeder direction on a [NetworkService]
 * This class is backed by a [BranchRecursiveTraversal].
 */
class SetDirection {

    /**
     * The [BranchRecursiveTraversal] used when tracing the normal state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val normalTraversal: BranchRecursiveTraversal<Terminal> = BranchRecursiveTraversal(
        { terminal, traversal -> setDownstreamAndQueueNext(traversal, terminal, OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION) },
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
        { terminal, traversal -> setDownstreamAndQueueNext(traversal, terminal, OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION) },
        { WeightedPriorityQueue.processQueue { it.phases.numPhases() } },
        { BasicTracker() },
        { WeightedPriorityQueue.branchQueue { it.phases.numPhases() } }
    )

    /**
     * Apply feeder directions from all feeder head terminals in the network.
     *
     * @param network The network in which to apply feeder directions.
     */
    fun run(network: NetworkService) {
        run(network.sequenceOf<Feeder>().mapNotNull { it.normalHeadTerminal }.toList())
    }

    /**
     * Apply [FeederDirection.DOWNSTREAM] from the [terminal].
     *
     * @param terminal The terminal to start applying feeder direction from.
     */
    fun run(terminal: Terminal) {
        run(listOf(terminal))
    }

    private fun run(startTerminals: List<Terminal>) {
        normalTraversal.tracker.clear()
        currentTraversal.tracker.clear()

        startTerminals.forEach {
            normalTraversal.reset().run(it)
            currentTraversal.reset().run(it)
        }
    }

    private fun setDownstreamAndQueueNext(
        traversal: BranchRecursiveTraversal<Terminal>,
        terminal: Terminal,
        openTest: OpenTest,
        directionSelector: DirectionSelector
    ) {
        val direction = directionSelector.select(terminal)
        if (!direction.add(FeederDirection.DOWNSTREAM))
            return

        val connected = terminal.connectivityNode?.let { cn -> cn.terminals.filter { it != terminal } } ?: emptyList()
        val processor = ::flowUpstreamAndQueueNextStraight.takeIf { connected.size == 1 } ?: ::flowUpstreamAndQueueNextBranch

        connected.forEach {
            processor(traversal, it, openTest, directionSelector)
        }
    }

    private fun isFeederHeadTerminal(terminal: Terminal): Boolean =
        terminal.conductingEquipment?.run {
            containers
                .asSequence()
                .filterIsInstance<Feeder>()
                .any { it.normalHeadTerminal == terminal }
        } ?: false

    private fun flowUpstreamAndQueueNextStraight(
        traversal: BranchRecursiveTraversal<Terminal>,
        terminal: Terminal,
        openTest: OpenTest,
        directionSelector: DirectionSelector
    ) {
        if (!traversal.tracker.visit(terminal))
            return

        if (terminal.conductingEquipment?.numTerminals() == 2)
            flowUpstreamAndQueueNext(terminal, openTest, directionSelector, traversal.queue::add)
        else
            flowUpstreamAndQueueNext(terminal, openTest, directionSelector) { traversal.startNewBranch(it) }
    }

    private fun flowUpstreamAndQueueNextBranch(
        traversal: BranchRecursiveTraversal<Terminal>,
        terminal: Terminal,
        openTest: OpenTest,
        directionSelector: DirectionSelector
    ) {
        // We don't want to visit the upstream terminal if we have branched as it prevents the downstream path of a loop processing correctly, but we
        // still need to make sure we don't re-visit the upstream terminal.
        if (traversal.hasVisited(terminal))
            return

        flowUpstreamAndQueueNext(terminal, openTest, directionSelector) { traversal.startNewBranch(it) }
    }

    private fun flowUpstreamAndQueueNext(
        terminal: Terminal,
        openTest: OpenTest,
        directionSelector: DirectionSelector,
        queue: (Terminal) -> Unit
    ) {
        val direction = directionSelector.select(terminal)
        if (!direction.add(FeederDirection.UPSTREAM))
            return

        if (isFeederHeadTerminal(terminal))
            return

        val ce = terminal.conductingEquipment ?: return
        if (terminal.phases.singlePhases.all { openTest.isOpen(ce, it) })
            return

        ce.terminals
            .asSequence()
            .filter { it != terminal }
            .forEach { queue(it) }
    }

    private fun BranchRecursiveTraversal<Terminal>.startNewBranch(terminal: Terminal) {
        branchQueue.add(branchSupplier().setStart(terminal))
    }

}

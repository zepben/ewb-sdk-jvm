/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.branchQueue
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue.Companion.processQueue

/**
 * Convenience class that provides methods for removing feeder direction on a [NetworkService]
 * This class is backed by a [BranchRecursiveTraversal].
 */
@Suppress("MemberVisibilityCanBePrivate")
class RemoveDirection {

    /**
     * The [BranchRecursiveTraversal] used when tracing the normal state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    val normalTraversal: BranchRecursiveTraversal<TerminalDirection> = BranchRecursiveTraversal(
        { current, traversal -> ebbAndQueue(traversal, current, DirectionSelector.NORMAL_DIRECTION) },
        { processQueue { it.weight } },
        { BasicTracker() },
        { branchQueue { it.weight } }
    )

    /**
     * The [BranchRecursiveTraversal] used when tracing the current state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    val currentTraversal: BranchRecursiveTraversal<TerminalDirection> = BranchRecursiveTraversal(
        { current, traversal -> ebbAndQueue(traversal, current, DirectionSelector.CURRENT_DIRECTION) },
        { processQueue { it.weight } },
        { BasicTracker() },
        { branchQueue { it.weight } }
    )

    /**
     * Remove all feeder directions from the specified network.
     *
     * @param networkService The network service to remove feeder directions from.
     */
    fun run(networkService: NetworkService) {
        networkService.sequenceOf<Terminal>().forEach {
            it.normalFeederDirection = FeederDirection.NONE
            it.currentFeederDirection = FeederDirection.NONE
        }
    }

    /**
     * Allows the removal of feeder direction from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the direction removal.
     * @param direction The feeder direction to remove. Defaults to all present directions. Specifying [FeederDirection.BOTH] will cause all directions
     *                  to be cleared from all connected equipment.
     */
    @JvmOverloads
    fun run(terminal: Terminal, direction: FeederDirection = FeederDirection.NONE) {
        runFromTerminal(normalTraversal, TerminalDirection(terminal, direction.orElse(terminal.normalFeederDirection)))
        runFromTerminal(currentTraversal, TerminalDirection(terminal, direction.orElse(terminal.currentFeederDirection)))
    }

    private fun runFromTerminal(traversal: BranchRecursiveTraversal<TerminalDirection>, start: TerminalDirection) {
        traversal.reset()
            .run(start)
    }

    private fun ebbAndQueue(traversal: BranchRecursiveTraversal<TerminalDirection>, current: TerminalDirection, directionSelector: DirectionSelector) {
        if (!directionSelector.select(current.terminal).remove(current.directionToEbb))
            return

        val otherTerminals = current.terminal.connectivityNode?.let { cn -> cn.terminals.filter { it != current.terminal } } ?: emptyList()

        if (current.directionToEbb == FeederDirection.BOTH) {
            otherTerminals
                .asSequence()
                .filter { directionSelector.select(it).remove(FeederDirection.BOTH) }
                .forEach { queueIfRequired(traversal, it, FeederDirection.BOTH, directionSelector) }
        } else {
            //
            // Check the number of other terminals with same direction:
            //    0:  remove opposite direction from all other terminals.
            //    1:  remove opposite direction from only the matched terminal.
            //    2+: do not queue or remove anything else as everything is still valid.
            //
            val oppositeDirection = current.directionToEbb.findOpposite()
            val matchingTerminals = otherTerminals.filter { current.directionToEbb in directionSelector.select(it).value }
            when (matchingTerminals.size) {
                0 -> {
                    otherTerminals
                        .asSequence()
                        .filter { directionSelector.select(it).remove(oppositeDirection) }
                        .forEach { queueIfRequired(traversal, it, oppositeDirection, directionSelector) }

                    otherTerminals.forEach { traversal.queue.add(TerminalDirection(it, oppositeDirection)) }
                }

                1 -> {
                    matchingTerminals.first().also {
                        if (directionSelector.select(it).remove(oppositeDirection))
                            queueIfRequired(traversal, it, oppositeDirection, directionSelector)
                    }
                }
            }
        }
    }

    private fun queueIfRequired(
        traversal: BranchRecursiveTraversal<TerminalDirection>,
        terminal: Terminal,
        directionEbbed: FeederDirection,
        directionSelector: DirectionSelector
    ) {
        val ce = terminal.conductingEquipment ?: return
        val otherTerminals = ce.terminals.filter { it != terminal }

        if (directionEbbed == FeederDirection.BOTH)
            otherTerminals.forEach { traversal.queue.add(TerminalDirection(it, directionEbbed)) }
        else {
            //
            // Check the number of other terminals with same direction:
            //    0:  remove opposite direction from all other terminals.
            //    1:  remove opposite direction from only the matched terminal.
            //    2+: do not queue or remove anything else as everything is still valid.
            //
            val oppositeDirection = directionEbbed.findOpposite()
            val matchingTerminals = otherTerminals.filter { directionEbbed in directionSelector.select(it).value }
            when (matchingTerminals.size) {
                0 -> otherTerminals.forEach { traversal.queue.add(TerminalDirection(it, oppositeDirection)) }
                1 -> traversal.queue.add(TerminalDirection(matchingTerminals.first(), oppositeDirection))
            }
        }
    }

    private fun FeederDirection.orElse(default: FeederDirection): FeederDirection =
        takeUnless { it == FeederDirection.NONE } ?: default

    private fun FeederDirection.findOpposite(): FeederDirection =
        // This will never be called for NONE or BOTH.
        when (this) {
            FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
            else -> FeederDirection.UPSTREAM
        }

    class TerminalDirection(val terminal: Terminal, val directionToEbb: FeederDirection) {

        val weight = terminal.phases.numPhases()

    }

}

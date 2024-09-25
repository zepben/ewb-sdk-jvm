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
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.conditions.OpenCondition
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

/**
 * Convenience class that provides methods for removing feeder direction on a [NetworkService]
 */
@Suppress("MemberVisibilityCanBePrivate")
class RemoveDirection {

    // TODO: Remove combined normal and current
    val directionRemovedKey = "DIRECTION_REMOVED"
    val normalTraversal: NetworkTrace<FeederDirection> = createTrace(DirectionSelector.NORMAL_DIRECTION, OpenTest.NORMALLY_OPEN)
    val currentTraversal: NetworkTrace<FeederDirection> = createTrace(DirectionSelector.CURRENT_DIRECTION, OpenTest.CURRENTLY_OPEN)

    private fun createTrace(directionSelector: DirectionSelector, openTest: OpenTest): NetworkTrace<FeederDirection> {
        return Tracing.connectedTerminalTrace(
            WeightedPriorityQueue.processQueue { it.path.toTerminal?.phases?.numPhases() ?: 1 },
            computeNextT = computeNextDirectionToRemove(directionSelector)
        )
            .addQueueCondition(OpenCondition(openTest))
            .addStepAction { item, context -> removeDirection(item, context, directionSelector) }
            .addQueueCondition { item, context ->
                item.data != FeederDirection.NONE && context.getValue<Boolean>(directionRemovedKey) == true
            }
    }

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
        runFromTerminal(normalTraversal, terminal, direction.orElse(terminal.normalFeederDirection))
        runFromTerminal(currentTraversal, terminal, direction.orElse(terminal.currentFeederDirection))
    }

    private fun runFromTerminal(traversal: NetworkTrace<FeederDirection>, start: Terminal, directionToRemove: FeederDirection) {
        traversal.reset().run(start, directionToRemove, false)
    }

    private fun computeNextDirectionToRemove(directionSelector: DirectionSelector): ComputeNextTWithPaths<FeederDirection> =
        ComputeNextTWithPaths { currentStep: NetworkTraceStep<FeederDirection>, _: StepContext, nextPath: StepPath, nextPaths: List<StepPath> ->
            when (currentStep.data) {
                FeederDirection.NONE -> FeederDirection.NONE
                FeederDirection.BOTH -> FeederDirection.BOTH
                else -> {
                    val directionEbbed = currentStep.data
                    if (nextPaths.size == 1) {
                        // If there is only one connected terminal, always remove the opposite direction
                        directionEbbed.findOpposite()
                    } else {
                        //
                        // Check the number of other terminals with same direction:
                        //    0:  remove opposite direction from all other terminals.
                        //    1:  remove opposite direction from only the matched terminal.
                        //    2+: do not queue or remove anything else as everything is still valid.
                        //
                        val matchingTerminals = nextPaths.count {
                            it.toTerminal != currentStep.path.toTerminal &&
                                directionSelector.selectOrNull(it.toTerminal)?.value?.contains(directionEbbed) == true
                        }

                        when {
                            matchingTerminals == 0 -> directionEbbed.findOpposite()
                            matchingTerminals == 1 && directionSelector.selectOrNull(nextPath.toTerminal)?.value?.contains(currentStep.data) == true -> directionEbbed.findOpposite()
                            else -> FeederDirection.NONE
                        }
                    }
                }
            }
        }

    private fun removeDirection(item: NetworkTraceStep<FeederDirection>, context: StepContext, directionSelector: DirectionSelector) {
        val wasRemoved = directionSelector.selectOrNull(item.path.toTerminal)?.remove(item.data)
        context.setValue(directionRemovedKey, wasRemoved ?: false)
    }

    private fun FeederDirection.orElse(default: FeederDirection): FeederDirection =
        takeUnless { it == FeederDirection.NONE } ?: default

    private fun FeederDirection.findOpposite(): FeederDirection =
        // This will never be called for NONE or BOTH.
        when (this) {
            FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
            else -> FeederDirection.UPSTREAM
        }

}

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
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue

private typealias DirectionToRemove = FeederDirection

/**
 * Convenience class that provides methods for removing feeder direction on a [NetworkService]
 */
class RemoveDirection(
    networkStateOperators: NetworkStateOperators
) {

    private val directionRemovedKey = RemoveDirection::class.simpleName + "::REMOVED"
    private val directionOperators: FeederDirectionStateOperations = networkStateOperators

    private val traversal: NetworkTrace<DirectionToRemove> = Tracing.connectedTerminalTrace(
        networkStateOperators = networkStateOperators,
        queue = WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() },
        computeNextT = ::computeNextDirectionToRemove
    )
        .addNetworkCondition { stopAtOpen() }
        .addStepAction { item, context ->
            val wasRemoved = directionOperators.removeDirection(item.path.toTerminal, item.data)
            context.setValue(directionRemovedKey, wasRemoved)
        }
        .addQueueCondition { (_, directionToRemove), context ->
            directionToRemove != FeederDirection.NONE && context.getValue<Boolean>(directionRemovedKey) == true
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
        val directionToRemove = direction.takeUnless { it == FeederDirection.NONE } ?: directionOperators.getDirection(terminal)
        traversal.reset().run(terminal, directionToRemove, false)
    }

    private fun computeNextDirectionToRemove(
        currentStep: NetworkTraceStep<DirectionToRemove>,
        context: StepContext,
        nextPath: StepPath,
        nextPaths: List<StepPath>
    ): FeederDirection {
        return when (currentStep.data) {
            FeederDirection.NONE -> FeederDirection.NONE
            FeederDirection.BOTH -> FeederDirection.BOTH
            else -> {
                val directionRemoved = currentStep.data
                if (nextPaths.size == 1) {
                    // If there is only one connected terminal, always remove the opposite direction
                    directionRemoved.findOpposite()
                } else {
                    //
                    // Check the number of other terminals with same direction:
                    //    0:  remove opposite direction from all other terminals.
                    //    1:  remove opposite direction from only the matched terminal.
                    //    2+: do not queue or remove anything else as everything is still valid.
                    //
                    val matchingTerminals = nextPaths.count {
                        it.toTerminal != currentStep.path.toTerminal && directionRemoved in directionOperators.getDirection(it.toTerminal)
                    }

                    when {
                        matchingTerminals == 0 -> directionRemoved.findOpposite()
                        matchingTerminals == 1 && directionRemoved in directionOperators.getDirection(nextPath.toTerminal) -> directionRemoved.findOpposite()
                        else -> FeederDirection.NONE
                    }
                }
            }
        }
    }

    private fun FeederDirection.findOpposite(): FeederDirection =
        // This will never be called for NONE or BOTH.
        when (this) {
            FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
            else -> FeederDirection.UPSTREAM
        }

}

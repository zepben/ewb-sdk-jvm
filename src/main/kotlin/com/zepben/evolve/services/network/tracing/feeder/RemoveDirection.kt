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
import com.zepben.evolve.services.network.tracing.networktrace.ComputeDataWithPaths
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue

/**
 * Convenience class that provides methods for removing feeder direction on a [NetworkService]
 */
class RemoveDirection {

    private class DirectionToRemove(val direction: FeederDirection, var removedDirection: Boolean = false)

    private fun createTrace(stateOperators: NetworkStateOperators): NetworkTrace<DirectionToRemove> = Tracing.networkTrace(
        networkStateOperators = stateOperators,
        actionStepType = NetworkTraceActionType.ALL_STEPS,
        queue = WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() },
        computeData = computeNextDirectionToRemove(stateOperators)
    )
        .addCondition { stopAtOpen() }
        .addStepAction { item, _ ->
            item.data.removedDirection = stateOperators.removeDirection(item.path.toTerminal, item.data.direction)
        }
        .addQueueCondition { (_, nextDirectionToRemove), _, (_, currentDirectionToRemove), _ ->
            nextDirectionToRemove.direction != FeederDirection.NONE && currentDirectionToRemove.removedDirection
        }

    /**
     * Allows the removal of present feeder directions from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the direction removal.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing directions.
     */
    @JvmOverloads
    fun run(terminal: Terminal, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) =
        run(terminal, FeederDirection.NONE, networkStateOperators)

    /**
     * Allows the removal of feeder direction from a terminal and the connected equipment chain.
     *
     * @param terminal The terminal from which to start the direction removal.
     * @param direction The feeder direction to remove. Defaults to all present directions. Specifying [FeederDirection.BOTH] will cause all directions
     *                  to be cleared from all connected equipment.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing directions.
     */
    @JvmOverloads
    fun run(terminal: Terminal, direction: FeederDirection, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        val directionToRemove = direction.takeUnless { it == FeederDirection.NONE } ?: networkStateOperators.getDirection(terminal)
        createTrace(networkStateOperators).run(terminal, DirectionToRemove(directionToRemove), canStopOnStartItem = false)
    }

    private fun computeNextDirectionToRemove(stateOperators: NetworkStateOperators): ComputeDataWithPaths<DirectionToRemove> =
        ComputeDataWithPaths { currentStep, _, nextPath, nextPaths ->
            if (!currentStep.data.removedDirection) {
                return@ComputeDataWithPaths DirectionToRemove(FeederDirection.NONE)
            }

            val directionToRemove = when (val directionRemoved = currentStep.data.direction) {
                FeederDirection.NONE -> FeederDirection.NONE
                FeederDirection.BOTH -> FeederDirection.BOTH
                else -> {
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
                            it.toTerminal != currentStep.path.toTerminal && directionRemoved in stateOperators.getDirection(it.toTerminal)
                        }

                        when {
                            matchingTerminals == 0 -> directionRemoved.findOpposite()
                            matchingTerminals == 1 && directionRemoved in stateOperators.getDirection(nextPath.toTerminal) -> directionRemoved.findOpposite()
                            else -> FeederDirection.NONE
                        }
                    }
                }
            }

            DirectionToRemove(directionToRemove)
        }

    private fun FeederDirection.findOpposite(): FeederDirection =
        // This will never be called for NONE or BOTH.
        when (this) {
            FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
            else -> FeederDirection.UPSTREAM
        }

}

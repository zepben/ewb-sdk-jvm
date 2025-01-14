/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue

/**
 * Convenience class that provides methods for clearing feeder direction on a [NetworkService]
 */
class ClearDirection {

    /*
     * NOTE: We used to try and remove directions in a single pass rather than clearing (and the reapplying where needed) to be more efficient.
     *       However, this caused all sorts of pain when trying to determine which directions to remove from dual fed equipment that contains inner loops.
     *       We decided it is so much simpler to just clear the directions and reapply from other feeder heads even if its a bit more computationally expensive.
     */

    /**
     * Clears the feeder direction from a terminal and the connected equipment chain.
     * This clears directions even if equipment is dual fed. A set of feeder head terminals encountered while running will be returned and directions
     * can be reapplied if required using [SetDirection].
     *
     * @param terminal The terminal from which to start the direction removal.
     * @param networkStateOperators The [NetworkStateOperators] to be used when removing directions.
     * @return A set of feeder head terminals encountered when clearing directions
     */
    @JvmOverloads
    fun run(terminal: Terminal, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): Set<Terminal> {
        val feederHeadTerminals = mutableSetOf<Terminal>()
        createTrace(networkStateOperators, feederHeadTerminals).run(terminal, canStopOnStartItem = false)
        return feederHeadTerminals
    }

    private fun createTrace(stateOperators: NetworkStateOperators, visitedFeederHeadTerminals: MutableSet<Terminal>): NetworkTrace<Unit> = Tracing.networkTrace(
        networkStateOperators = stateOperators,
        actionStepType = NetworkTraceActionType.ALL_STEPS,
        queue = WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() },
    )
        .addCondition { stopAtOpen() }
        .addQueueCondition { (nextPath), _, _, _ ->
            stateOperators.getDirection(nextPath.toTerminal) != FeederDirection.NONE
        }
        .addStepAction { item, _ ->
            stateOperators.setDirection(item.path.toTerminal, FeederDirection.NONE)
        }
        .addStepAction { item, _ ->
            if (item.path.toTerminal.isFeederHeadTerminal())
                visitedFeederHeadTerminals.add(item.path.toTerminal)
        }

}

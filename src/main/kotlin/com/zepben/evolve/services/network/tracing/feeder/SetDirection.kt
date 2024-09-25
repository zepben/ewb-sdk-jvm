/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.traversalV2.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting feeder direction on a [NetworkService]
 */
class SetDirection(
    internal val networkStateOperators: NetworkStateOperators,
) {
    private val directionSelector: DirectionSelector = networkStateOperators.directionSelector

    private val traversal: NetworkTrace<FeederDirection> = Tracing.connectedTerminalTrace(
        { WeightedPriorityQueue.processQueue { it.path.toTerminal?.phases?.numPhases() ?: 1 } },
        { WeightedPriorityQueue.branchQueue { it.path.toTerminal?.phases?.numPhases() ?: 1 } },
        computeNextT = { step: NetworkTraceStep<FeederDirection>, _, nextPath ->
            val directionApplied = step.data
            val nextDirection = when (directionApplied) {
                FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
                FeederDirection.DOWNSTREAM -> FeederDirection.UPSTREAM
                FeederDirection.BOTH -> FeederDirection.BOTH
                else -> FeederDirection.NONE
            }

            if (nextDirection == FeederDirection.NONE ||
                directionSelector.selectOrNull(nextPath.toTerminal)?.value?.contains(nextDirection) == true
            )
                FeederDirection.NONE
            else
                nextDirection
        }
    )
        .addCondition(stopAtOpen(networkStateOperators.openTest))
        .addStopCondition { (path), _ ->
            isFeederHeadTerminal(path.toTerminal) || reachedSubstationTransformer(path.toTerminal)
        }
        .addQueueCondition { (_, directionToApply), _ ->
            directionToApply != FeederDirection.NONE
        }
        .addStepAction { (path, directionToApply), _ ->
            directionSelector.selectOrNull(path.toTerminal)?.add(directionToApply)
        }

    /**
     * Apply [FeederDirection.DOWNSTREAM] from the [terminal].
     *
     * @param terminal The terminal to start applying direction from.
     */
    fun run(terminal: Terminal) {
        traversal.reset().run(terminal, FeederDirection.DOWNSTREAM, false)
    }

    private fun isFeederHeadTerminal(terminal: Terminal?): Boolean =
        terminal?.conductingEquipment?.run {
            containers
                .asSequence()
                .filterIsInstance<Feeder>()
                .any { it.normalHeadTerminal == terminal }
        } ?: false

    private fun reachedSubstationTransformer(terminal: Terminal?): Boolean =
        terminal?.conductingEquipment.let { ce -> (ce is PowerTransformer) && ce.substations.isNotEmpty() }

}

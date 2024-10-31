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
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting feeder direction on a [NetworkService]
 */
class SetDirection(
    internal val networkStateOperators: NetworkStateOperators,
) {

    private val traversal: NetworkTrace<FeederDirection> = Tracing.terminalNetworkTrace(
        networkStateOperators = networkStateOperators,
        { WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() } },
        { WeightedPriorityQueue.branchQueue { it.path.toTerminal.phases.numPhases() } },
        computeNextT = { step: NetworkTraceStep<FeederDirection>, _, nextPath ->
            val directionApplied = step.data
            val nextDirection = when (directionApplied) {
                FeederDirection.UPSTREAM -> FeederDirection.DOWNSTREAM
                FeederDirection.DOWNSTREAM -> FeederDirection.UPSTREAM
                FeederDirection.BOTH -> FeederDirection.BOTH
                else -> FeederDirection.NONE
            }

            // NOTE: Stopping / short circuiting by checking that the next direction is already present in the toTerminal,
            //       causes stop certain looping network configurations not to be reprocessed. This means that some parts of
            //       loops do not end up with BOTH directions. This is done to stop massive computational blowout on
            //       on large networks with weird looping connectivity that rarely happens in reality.
            if (nextDirection == FeederDirection.NONE || nextDirection in networkStateOperators.getDirection(nextPath.toTerminal))
                FeederDirection.NONE
            else
                nextDirection
        }
    )
        .addCondition(stopAtOpen(networkStateOperators::isOpen))
        .addStopCondition { (path), _ ->
            isFeederHeadTerminal(path.toTerminal) || reachedSubstationTransformer(path.toTerminal)
        }
        .addQueueCondition { (_, directionToApply), _, _, _ ->
            directionToApply != FeederDirection.NONE
        }
        .addStepAction { (path, directionToApply), _ ->
            networkStateOperators.addDirection(path.toTerminal, directionToApply)
        }

    /**
     * Apply feeder directions from all closed feeder head terminals in the network.
     *
     * @param network The network in which to apply feeder directions.
     */
    fun run(network: NetworkService) {
        network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal }
            .forEach {
                val feederHead = requireNotNull(it.conductingEquipment) { "head terminals require conducting equipment to apply feeder directions" }

                if (!networkStateOperators.isOpen(feederHead, null))
                    run(it)
            }
    }

    /**
     * Apply [FeederDirection.DOWNSTREAM] from the [terminal].
     *
     * @param terminal The terminal to start applying direction from.
     */
    fun run(terminal: Terminal) {
        traversal.reset().run(terminal, FeederDirection.DOWNSTREAM, canStopOnStartItem = false)
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

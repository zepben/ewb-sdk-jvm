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
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.WeightedPriorityQueue
import org.slf4j.Logger

/**
 * Convenience class that provides methods for setting feeder direction on a [NetworkService]
 */
class SetDirection(
    private val debugLogger: Logger?
) {

    private fun computeData(
        reprocessedLoopTerminals: MutableSet<Terminal>,
        stateOperators: NetworkStateOperators,
        step: NetworkTraceStep<FeederDirection>,
        nextPath: NetworkTraceStep.Path
    ): FeederDirection {
        if (nextPath.toEquipment is BusbarSection) {
            return FeederDirection.CONNECTOR
        }

        val nextDirection = when {
            step.data == FeederDirection.NONE -> FeederDirection.NONE
            nextPath.tracedInternally -> FeederDirection.DOWNSTREAM
            nextPath.toEquipment is Cut -> FeederDirection.UPSTREAM
            nextPath.didTraverseAcLineSegment -> FeederDirection.DOWNSTREAM
            else -> FeederDirection.UPSTREAM
        }

        //
        // NOTE: Stopping / short-circuiting by checking that the next direction is already present in the toTerminal,
        //       causes certain looping network configurations not to be reprocessed. This means that some parts of
        //       loops do not end up with BOTH directions. This is done to stop massive computational blowout on
        //       large networks with weird looping connectivity that rarely happens in reality.
        //
        //       To allow these parts of the loop to be correctly processed without the computational blowout, we allow
        //       a single re-pass over the loop, controlled by the `reprocessedLoopTerminals` set.
        //
        val nextTerminalDirection = stateOperators.getDirection(nextPath.toTerminal)
        return when {
            nextDirection == FeederDirection.NONE -> FeederDirection.NONE
            nextDirection !in nextTerminalDirection -> nextDirection
            (nextTerminalDirection == FeederDirection.BOTH) && reprocessedLoopTerminals.add(nextPath.toTerminal) -> nextDirection
            else -> FeederDirection.NONE
        }
    }

    private fun createTraversal(stateOperators: NetworkStateOperators): NetworkTrace<FeederDirection> {
        // Used to track the loops we have already reprocessed to prevent computation blowouts.
        val reprocessedLoopTerminals = mutableSetOf<Terminal>()

        return Tracing.networkTraceBranching(
            networkStateOperators = stateOperators,
            actionStepType = NetworkTraceActionType.ALL_STEPS,
            debugLogger,
            name = "SetDirection(${stateOperators.description})",
            { WeightedPriorityQueue.processQueue { it.path.toTerminal.phases.numPhases() } },
            { WeightedPriorityQueue.branchQueue { it.path.toTerminal.phases.numPhases() } }
        ) { step: NetworkTraceStep<FeederDirection>, _, nextPath ->
            computeData(reprocessedLoopTerminals, stateOperators, step, nextPath)
        }
            .addCondition { stopAtOpen() }
            .addStopCondition { (path), _ ->
                path.toTerminal.isFeederHeadTerminal() || reachedSubstationTransformer(path.toTerminal)
            }
            .addQueueCondition { (_, directionToApply), _, _, _ ->
                directionToApply != FeederDirection.NONE
            }
            .addStepAction { (path, directionToApply), _ ->
                stateOperators.addDirection(path.toTerminal, directionToApply)
            }
    }

    /**
     * Apply feeder directions from all closed feeder head terminals in the network.
     *
     * @param network The network in which to apply feeder directions.
     * @param networkStateOperators The [NetworkStateOperators] to be used when applying directions.
     */
    @JvmOverloads
    fun run(network: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal }
            .forEach {
                val feederHead = requireNotNull(it.conductingEquipment) { "head terminals require conducting equipment to apply feeder directions" }

                if (!networkStateOperators.isOpen(feederHead, null))
                    run(it, networkStateOperators)
            }
    }

    /**
     * Apply [FeederDirection.DOWNSTREAM] from the [terminal].
     *
     * @param terminal The terminal to start applying direction from.
     */
    @JvmOverloads
    fun run(terminal: Terminal, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
        createTraversal(networkStateOperators).run(terminal, FeederDirection.DOWNSTREAM, canStopOnStartItem = false)
    }

    private fun reachedSubstationTransformer(terminal: Terminal?): Boolean =
        terminal?.conductingEquipment.let { ce -> (ce is PowerTransformer) && ce.substations.isNotEmpty() }

}

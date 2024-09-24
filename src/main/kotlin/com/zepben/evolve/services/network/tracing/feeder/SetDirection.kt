/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.traversalV2.WeightedPriorityQueue

/**
 * Convenience class that provides methods for setting feeder direction on a [NetworkService]
 */
class SetDirection {

    /**
     * The [NetworkTrace] used when tracing the normal state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    // TODO: Why is this public?
    @Suppress("MemberVisibilityCanBePrivate")
    val normalTraversal: NetworkTrace<FeederDirection> = createTrace(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)

    /**
     * The [NetworkTrace] used when tracing the current state of the network.
     *
     * NOTE: If you add stop conditions to this traversal it may no longer work correctly, use at your own risk.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    // TODO: Why is this public?
    val currentTraversal: NetworkTrace<FeederDirection> = createTrace(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)

    private fun createTrace(openTest: OpenTest, directionSelector: DirectionSelector): NetworkTrace<FeederDirection> {
        return Tracing.connectedTerminalTrace(
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
            .addCondition(stopAtOpen(openTest))
            .addStopCondition { (path), _ ->
                isFeederHeadTerminal(path.toTerminal) || reachedSubstationTransformer(path.toTerminal)
            }
            .addQueueCondition { (_, directionToApply), _ ->
                directionToApply != FeederDirection.NONE
            }
            .addStepAction { (path, directionToApply), context ->
                setDirection(path, directionSelector, directionToApply)
            }
    }

    /**
     * Apply feeder directions from all feeder head terminals in the network.
     *
     * @param network The network in which to apply feeder directions.
     */
    fun run(network: NetworkService) {
        val headTerminals = network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal }
            .filter { !it.conductingEquipment.isNormallyOpenSwitch() }
            .onEach {
                it.normalFeederDirection = FeederDirection.DOWNSTREAM
                it.currentFeederDirection = FeederDirection.DOWNSTREAM
            }
            .associateWith { FeederDirection.DOWNSTREAM }
            .toList()
        run(headTerminals)
    }

    /**
     * Apply [FeederDirection.DOWNSTREAM] from the [terminal].
     *
     * @param terminal The terminal to start applying feeder direction from.
     */
    fun run(terminal: Terminal) {
        run(listOf(terminal to FeederDirection.DOWNSTREAM))
    }

    private fun run(startTerminals: List<Pair<Terminal, FeederDirection>>) {
        startTerminals.forEach { (terminal, directionToApply) ->
            normalTraversal.reset().run(terminal, directionToApply, false)
            currentTraversal.reset().run(terminal, directionToApply, false)
        }
    }

    private fun setDirection(path: StepPath, directionSelector: DirectionSelector, directionToApply: FeederDirection) {
        directionSelector.selectOrNull(path.toTerminal)?.add(directionToApply)
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

    private fun ConductingEquipment?.isNormallyOpenSwitch(): Boolean =
        (this is Switch) && isNormallyOpen()

}

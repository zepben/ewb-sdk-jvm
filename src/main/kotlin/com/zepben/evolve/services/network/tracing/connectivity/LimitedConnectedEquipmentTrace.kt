/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 * This file is part of ewb-network-routes.
 *
 * ewb-network-routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ewb-network-routes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ewb-network-routes.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection

/**
 * A class for finding the connected equipment.
 *
 * @param createTraversal Get the [ConnectedEquipmentTraversal] used to traverse the network. Should be either [Tracing.normalConnectedEquipmentTrace]` or
 * [Tracing.currentConnectedEquipmentTrace], depending on the network state you want to trace.
 * @param getTerminalDirection Used to get the [FeederDirection] of a [Terminal]. Should be either [Terminal.normalFeederDirection] or
 * [Terminal.currentFeederDirection], depending on the network state you want to trace.
 */
class LimitedConnectedEquipmentTrace(
    private val createTraversal: () -> ConnectedEquipmentTraversal,
    private val getTerminalDirection: (Terminal) -> FeederDirection
) {

    /**
     * Run the trace from the [startingEquipment].
     *
     * @param startingEquipment The [ConductingEquipment] to start tracing from.
     * @param maximumSteps The maximum number of steps to trace out [1..100]. Defaults to 1.
     * @param feederDirection The optional [FeederDirection] of the connected equipment you want to return. Default null (all).
     */
    fun run(startingEquipment: List<ConductingEquipment>, maximumSteps: Int = 1, feederDirection: FeederDirection? = null): Map<ConductingEquipment, Int> {
        val checkSteps = maximumSteps.coerceAtLeast(1).coerceAtMost(100)

        val matchingEquipment = feederDirection?.let {
            runWithDirection(startingEquipment, checkSteps, it)
        } ?: runWithoutDirection(startingEquipment, checkSteps)

        return matchingEquipment
            .groupBy({ it.conductingEquipment }, { it.step })
            .mapValues { (_, s) -> s.minOrNull()!! }
    }

    private fun runWithDirection(
        startingEquipment: List<ConductingEquipment>,
        maximumSteps: Int,
        feederDirection: FeederDirection
    ): List<ConductingEquipmentStep> {
        val matchingEquipment = mutableListOf<ConductingEquipmentStep>().apply { addAll(startingEquipment.map { ConductingEquipmentStep(it) }) }

        startingEquipment
            .asSequence()
            .flatMap { it.terminals }
            .filter { getTerminalDirection(it) == feederDirection }
            .flatMap { it.connectedTerminals() }
            .mapNotNull { it.conductingEquipment }
            .forEach { start ->
                createTraversal().apply {
                    addStopCondition { (_, step) -> step >= maximumSteps - 1 }
                    addStopCondition { (ce, _) -> ce in startingEquipment }
                    addStopCondition { (ce, _) -> ce.terminals.none { getTerminalDirection(it) == feederDirection } }
                    addStepAction { matchingEquipment.add(ConductingEquipmentStep(it.conductingEquipment, it.step + 1)) }

                    run(start)
                }
            }

        return if ((feederDirection == FeederDirection.BOTH) || (feederDirection == FeederDirection.NONE))
            matchingEquipment.filter { (ce, _) -> ce.terminals.any { getTerminalDirection(it) == feederDirection } }
        else
            matchingEquipment
    }

    private fun runWithoutDirection(startingEquipment: List<ConductingEquipment>, maximumSteps: Int): List<ConductingEquipmentStep> {
        val matchingEquipment = mutableListOf<ConductingEquipmentStep>()

        startingEquipment.forEach { start ->
            createTraversal().apply {
                addStopCondition { (_, step) -> step >= maximumSteps }
                addStepAction { matchingEquipment.add(it) }

                run(start, false)
            }
        }

        return matchingEquipment
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext

/**
 * Convenience class that provides methods for assigning HV/MV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [NetworkTrace].
 */
class AssignToFeeders(
    private val networkStateOperators: NetworkStateOperators
) {
    fun run(network: NetworkService) {
        val terminalToAuxEquipment = network.sequenceOf<AuxiliaryEquipment>()
            .filter { it.terminal != null }
            .groupBy { it.terminal!! }

        val feederStartPoints = network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal?.conductingEquipment }
            .toSet()

        network.sequenceOf<Feeder>().forEach { run(it, feederStartPoints, terminalToAuxEquipment) }
    }

    private fun run(
        feeder: Feeder,
        feederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
    ) {
        val headTerminal = feeder.normalHeadTerminal ?: return
        val traversal = createTrace(terminalToAuxEquipment, feederStartPoints, listOf(feeder))
        traversal.run(headTerminal, canStopOnStartItem = false)
    }

    private fun createTrace(
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        feederStartPoints: Set<ConductingEquipment>,
        feedersToAssign: List<Feeder>,
    ): NetworkTrace<Unit> {
        return Tracing.connectedTerminalTrace(networkStateOperators)
            .addNetworkCondition { stopAtOpen() }
            .addStopCondition { (path), _ -> feederStartPoints.contains(path.toEquipment) }
            .addQueueCondition { (path), _ -> !reachedSubstationTransformer(path.toEquipment) }
            .addQueueCondition { (path), _ -> !reachedLv(path.toEquipment) }
            .addStepAction { (path), context -> process(path, context, terminalToAuxEquipment, feedersToAssign) }
    }

    private val reachedSubstationTransformer: (ConductingEquipment) -> Boolean = { ce ->
        ce is PowerTransformer && ce.substations.isNotEmpty()
    }

    private val reachedLv: (ConductingEquipment) -> Boolean = { ce ->
        ce.baseVoltage?.let { it.nominalVoltage < 1000 } ?: false
    }

    private fun process(
        stepPath: StepPath,
        stepContext: StepContext,
        terminalToAuxEquipment: Map<Terminal, Collection<AuxiliaryEquipment>>,
        feedersToAssign: List<Feeder>
    ) {
        if (stepPath.tracedInternally && !stepContext.isStartItem)
            return

        terminalToAuxEquipment[stepPath.toTerminal]?.forEach { auxEq ->
            feedersToAssign.forEach { feeder -> networkStateOperators.associateEquipmentAndContainer(auxEq, feeder) }
        }

        feedersToAssign.forEach { feeder -> networkStateOperators.associateEquipmentAndContainer(stepPath.toEquipment, feeder) }
        when (stepPath.toEquipment) {
            is ProtectedSwitch ->
                stepPath.toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                    feedersToAssign.forEach {
                        networkStateOperators.associateEquipmentAndContainer(system, it)
                    }
                }
        }
    }

}

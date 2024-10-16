/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * Convenience class that provides methods for assigning LV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToLvFeeders(
    private val networkStateOperators: NetworkStateOperators
) {
    fun run(network: NetworkService) {
        val terminalToAuxEquipment = network.sequenceOf<AuxiliaryEquipment>()
            .filter { it.terminal != null }
            .groupBy { it.terminal!! }

        val lvFeederStartPoints = network.sequenceOf<LvFeeder>()
            .mapNotNull { lvFeeder ->
                lvFeeder.normalHeadTerminal?.conductingEquipment?.also { headEquipment ->
                    headEquipment.normalFeeders.forEach { feeder ->
                        feeder.addNormalEnergizedLvFeeder(lvFeeder)
                        lvFeeder.addNormalEnergizingFeeder(feeder)
                    }
                }
            }
            .toSet()

        network.sequenceOf<LvFeeder>().forEach { run(it, lvFeederStartPoints, terminalToAuxEquipment) }
    }

    private fun run(
        lvFeeder: LvFeeder,
        feederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
    ) {
        val headTerminal = lvFeeder.normalHeadTerminal ?: return
        val traversal = createTrace(terminalToAuxEquipment, feederStartPoints, listOf(lvFeeder))
        traversal.run(headTerminal, canStopOnStartItem = false)
    }

    private fun createTrace(
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        lvFeederStartPoints: Set<ConductingEquipment>,
        lvFeedersToAssign: List<LvFeeder>,
    ): NetworkTrace<Unit> {
        return Tracing.connectedTerminalTrace(networkStateOperators)
            .addNetworkCondition { stopAtOpen() }
            .addStopCondition { (path), _ -> lvFeederStartPoints.contains(path.toEquipment) }
            .addQueueCondition { (path), _ -> !reachedHv(path.toEquipment) }
            .addStepAction { (path), context -> process(path, context, terminalToAuxEquipment, lvFeederStartPoints, lvFeedersToAssign) }
    }

    private val reachedHv: (ConductingEquipment) -> Boolean = { ce ->
        ce.baseVoltage?.let { it.nominalVoltage >= 1000 } ?: false
    }

    private fun process(
        stepPath: StepPath,
        stepContext: StepContext,
        terminalToAuxEquipment: Map<Terminal, Collection<AuxiliaryEquipment>>,
        lvFeederStartPoints: Set<ConductingEquipment>,
        lvFeedersToAssign: List<LvFeeder>
    ) {
        if (stepPath.tracedInternally && !stepContext.isStartItem)
            return

        if (lvFeederStartPoints.contains(stepPath.toEquipment)) {
            stepPath.toEquipment.normalFeeders.forEach { feeder ->
                lvFeedersToAssign.forEach { lvFeeder ->
                    feeder.addNormalEnergizedLvFeeder(lvFeeder)
                    lvFeeder.addNormalEnergizingFeeder(feeder)
                }
            }
        }

        terminalToAuxEquipment[stepPath.toTerminal]?.forEach { auxEq ->
            lvFeedersToAssign.forEach { feeder -> networkStateOperators.associateEquipmentAndContainer(auxEq, feeder) }
        }

        lvFeedersToAssign.forEach { feeder -> networkStateOperators.associateEquipmentAndContainer(stepPath.toEquipment, feeder) }

        if (stepPath.toEquipment is ProtectedSwitch) {
            stepPath.toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                lvFeedersToAssign.forEach {
                    networkStateOperators.associateEquipmentAndContainer(system, it)
                }
            }
        }
    }

}

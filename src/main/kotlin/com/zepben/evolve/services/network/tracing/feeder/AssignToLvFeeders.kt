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
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext

/**
 * Convenience class that provides methods for assigning LV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [NetworkTrace].
 */
class AssignToLvFeeders {

    @JvmOverloads
    fun run(network: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL) {
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
                    headEquipment.currentFeeders.forEach { feeder ->
                        feeder.addCurrentEnergizedLvFeeder(lvFeeder)
                        lvFeeder.addCurrentEnergizingFeeder(feeder)
                    }
                }
            }
            .toSet()

        network.sequenceOf<LvFeeder>().forEach { run(networkStateOperators, it, lvFeederStartPoints, terminalToAuxEquipment) }
    }

    private fun run(
        stateOperators: NetworkStateOperators,
        lvFeeder: LvFeeder,
        feederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
    ) {
        val headTerminal = lvFeeder.normalHeadTerminal ?: return
        val traversal = createTrace(stateOperators, terminalToAuxEquipment, feederStartPoints, listOf(lvFeeder))
        traversal.run(headTerminal, canStopOnStartItem = false)
    }

    private fun createTrace(
        stateOperators: NetworkStateOperators,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        lvFeederStartPoints: Set<ConductingEquipment>,
        lvFeedersToAssign: List<LvFeeder>,
    ): NetworkTrace<Unit> {
        return Tracing.networkTrace(stateOperators, NetworkTraceActionType.ALL_STEPS)
            .addCondition { stopAtOpen() }
            .addStopCondition { (path), _ -> lvFeederStartPoints.contains(path.toEquipment) }
            .addQueueCondition { (path), _, _, _ -> !reachedHv(path.toEquipment) }
            .addStepAction { (path), context ->
                process(stateOperators, path, context, terminalToAuxEquipment, lvFeederStartPoints, lvFeedersToAssign)
            }
    }

    private val reachedHv: (ConductingEquipment) -> Boolean = { ce ->
        ce.baseVoltage?.let { it.nominalVoltage >= 1000 } ?: false
    }

    private fun process(
        stateOperators: NetworkStateOperators,
        stepPath: NetworkTraceStep.Path,
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
            lvFeedersToAssign.forEach { feeder -> stateOperators.associateEquipmentAndContainer(auxEq, feeder) }
        }

        lvFeedersToAssign.forEach { feeder -> stateOperators.associateEquipmentAndContainer(stepPath.toEquipment, feeder) }

        if (stepPath.toEquipment is ProtectedSwitch) {
            stepPath.toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                lvFeedersToAssign.forEach {
                    stateOperators.associateEquipmentAndContainer(system, it)
                }
            }
        }
    }

}

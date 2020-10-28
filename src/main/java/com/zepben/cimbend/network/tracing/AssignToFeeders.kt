/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.Feeder
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.Breaker
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.network.NetworkService
import com.zepben.traversals.BasicTraversal

/**
 * Convenience class that provides methods for assigning feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToFeeders {

    private val normalTraversal: BasicTraversal<PhaseStep> = PhaseTrace.newNormalTrace()
    private val currentTraversal: BasicTraversal<PhaseStep> = PhaseTrace.newCurrentTrace()
    private lateinit var activeFeeder: Feeder

    init {
        normalTraversal.addStopCondition(::isFeederCb)
        normalTraversal.addStopCondition(::notFromTxPrimaryVoltage)
        normalTraversal.addStepAction { phaseStep, _ -> processNormal(phaseStep) }

        currentTraversal.addStopCondition(::isFeederCb)
        currentTraversal.addStopCondition(::notFromTxPrimaryVoltage)
        currentTraversal.addStepAction { phaseStep, _ -> processCurrent(phaseStep) }
    }

    fun run(network: NetworkService) {
        network.sequenceOf<Feeder>().forEach(this::run)
    }

    fun run(feeder: Feeder) {
        activeFeeder = feeder

        val headTerminal = feeder.normalHeadTerminal ?: return

        run(normalTraversal, headTerminal)
        run(currentTraversal, headTerminal)
    }

    private fun run(traversal: BasicTraversal<PhaseStep>, headTerminal: Terminal) {
        traversal.reset()

        val headEquipment = headTerminal.conductingEquipment ?: return
        traversal.queue().add(PhaseStep.startAt(headEquipment, headTerminal.phases))

        NetworkService.connectedTerminals(headTerminal).forEach {
            it.to()?.let { to ->
                traversal.queue().add(PhaseStep.startAt(to, headTerminal.phases))
            }
        }

        traversal.run()
    }

    private fun isFeederCb(phaseStep: PhaseStep) = isFeederCb(phaseStep.conductingEquipment())
    private fun isFeederCb(conductingEquipment: ConductingEquipment) = conductingEquipment is Breaker && conductingEquipment.isSubstationBreaker

    private fun notFromTxPrimaryVoltage(phaseStep: PhaseStep): Boolean {
        val conductingEquipment = phaseStep.conductingEquipment()
        if (conductingEquipment !is PowerTransformer) return false
        val previous = phaseStep.previous() ?: return false
        return highestTxVoltage(conductingEquipment) != previous.baseVoltage?.nominalVoltage
    }

    private fun highestTxVoltage(powerTransformer: PowerTransformer): Int? {
        if (powerTransformer.baseVoltage != null)
            return powerTransformer.baseVoltageValue

        var maxValue: Int? = null
        powerTransformer.ends.forEach { end ->
            maxValue = maxOf(maxValue ?: 0, end.baseVoltage?.nominalVoltage ?: end.ratedU)
        }
        return maxValue
    }

    private fun processNormal(phaseStep: PhaseStep) =
        process(phaseStep.conductingEquipment(), ConductingEquipment::addContainer, Feeder::addEquipment)

    private fun processCurrent(phaseStep: PhaseStep) =
        process(phaseStep.conductingEquipment(), ConductingEquipment::addCurrentFeeder, Feeder::addCurrentEquipment)

    private fun process(
        conductingEquipment: ConductingEquipment,
        assignFeederToEquipment: (ConductingEquipment, Feeder) -> Unit,
        assignEquipmentToFeeder: (Feeder, ConductingEquipment) -> Unit
    ) {
        assignFeederToEquipment(conductingEquipment, activeFeeder)
        assignEquipmentToFeeder(activeFeeder, conductingEquipment)
    }
}

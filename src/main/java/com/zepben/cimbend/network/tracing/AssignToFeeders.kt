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
        normalTraversal.addStepAction(::processNormal)
        currentTraversal.addStepAction(::processCurrent)
    }

    fun run(network: NetworkService) {
        val feederStartPoints = network.sequenceOf(Feeder::class)
            .mapNotNull { it.normalHeadTerminal }
            .mapNotNull { it.conductingEquipment }
            .toSet()

        configureStopConditions(normalTraversal, feederStartPoints)
        configureStopConditions(currentTraversal, feederStartPoints)

        network.sequenceOf<Feeder>().forEach(::run)
    }

    private fun run(feeder: Feeder) {
        activeFeeder = feeder

        val headTerminal = feeder.normalHeadTerminal ?: return

        run(normalTraversal, headTerminal)
        run(currentTraversal, headTerminal)
    }

    private fun run(traversal: BasicTraversal<PhaseStep>, headTerminal: Terminal) {
        traversal.reset()

        NetworkService.connectedTerminals(headTerminal).forEach {
            it.to?.let { to ->
                traversal.queue().add(PhaseStep.startAt(to, headTerminal.phases))
            }
        }

        traversal.run()
    }

    private fun configureStopConditions(traversal: BasicTraversal<PhaseStep>, feederStartPoints: Set<ConductingEquipment>) {
        traversal.clearStopConditions()
        traversal.addStopCondition(reachedEquipment(feederStartPoints))
        traversal.addStopCondition(reachedSubstationTransformer)
    }

    private val reachedEquipment: (Set<ConductingEquipment>) -> (PhaseStep) -> Boolean = { { ps: PhaseStep -> it.contains(ps.conductingEquipment()) } }

    private val reachedSubstationTransformer: (PhaseStep) -> Boolean = { ps: PhaseStep ->
        val ce = ps.conductingEquipment()
        ce is PowerTransformer && ce.substations.isNotEmpty()
    }

    private fun processNormal(phaseStep: PhaseStep, isStopping: Boolean): Unit =
        process(phaseStep.conductingEquipment(), ConductingEquipment::addContainer, Feeder::addEquipment, isStopping)

    private fun processCurrent(phaseStep: PhaseStep, isStopping: Boolean): Unit =
        process(phaseStep.conductingEquipment(), ConductingEquipment::addCurrentFeeder, Feeder::addCurrentEquipment, isStopping)

    private fun process(
        conductingEquipment: ConductingEquipment,
        assignFeederToEquipment: (ConductingEquipment, Feeder) -> Unit,
        assignEquipmentToFeeder: (Feeder, ConductingEquipment) -> Unit,
        isStopping: Boolean
    ) {
        if (isStopping && conductingEquipment is PowerTransformer)
            return

        assignFeederToEquipment(conductingEquipment, activeFeeder)
        assignEquipmentToFeeder(activeFeeder, conductingEquipment)
    }

}

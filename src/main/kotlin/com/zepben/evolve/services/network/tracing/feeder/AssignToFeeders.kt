/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * Convenience class that provides methods for assigning HV/MV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToFeeders {

    private val normalTraversal: BasicTraversal<Terminal> = AssociatedTerminalTrace.newNormalTrace()
    private val currentTraversal: BasicTraversal<Terminal> = AssociatedTerminalTrace.newCurrentTrace()
    private lateinit var activeFeeder: Feeder

    init {
        normalTraversal.addStepAction(::processNormal)
        currentTraversal.addStepAction(::processCurrent)
    }

    fun run(network: NetworkService) {
        val feederStartPoints = network.sequenceOf<Feeder>()
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

    private fun run(traversal: BasicTraversal<Terminal>, headTerminal: Terminal) {
        traversal.reset()

        traversal.tracker.visit(headTerminal)
        traversal.applyStepActions(headTerminal, false)
        AssociatedTerminalTrace.queueAssociated(traversal, headTerminal)

        traversal.run()
    }

    private fun configureStopConditions(traversal: BasicTraversal<Terminal>, feederStartPoints: Set<ConductingEquipment>) {
        traversal.clearStopConditions()
        traversal.addStopCondition(reachedEquipment(feederStartPoints))
        traversal.addStopCondition(reachedSubstationTransformer)
        traversal.addStopCondition(reachedLv)
    }

    private val reachedEquipment: (Set<ConductingEquipment>) -> (Terminal) -> Boolean = { { terminal: Terminal -> it.contains(terminal.conductingEquipment) } }

    private val reachedSubstationTransformer: (Terminal) -> Boolean = { ps: Terminal ->
        val ce = ps.conductingEquipment
        ce is PowerTransformer && ce.substations.isNotEmpty()
    }

    private val reachedLv: (Terminal) -> Boolean = { terminal ->
        terminal.conductingEquipment?.baseVoltage?.let { it.nominalVoltage < 1000 } ?: false
    }

    private fun processNormal(terminal: Terminal, isStopping: Boolean) {
        if (!isStopping || !reachedLv(terminal) && !reachedSubstationTransformer(terminal))
            terminal.conductingEquipment?.let { ce ->
                ce.addContainer(activeFeeder)
                activeFeeder.addEquipment(ce)

                ce.normalLvFeeders
                    .filter { it.normalHeadTerminal?.conductingEquipment == ce }
                    .forEach { lvFeeder ->
                        lvFeeder.addNormalEnergizingFeeder(activeFeeder)
                        activeFeeder.addNormalEnergizedLvFeeder(lvFeeder)
                    }
            }
    }

    private fun processCurrent(terminal: Terminal, isStopping: Boolean) {
        if (!isStopping || !reachedLv(terminal) && !reachedSubstationTransformer(terminal))
            terminal.conductingEquipment?.let { ce ->
                ce.addCurrentContainer(activeFeeder)
                activeFeeder.addCurrentEquipment(ce)
            }
    }

}

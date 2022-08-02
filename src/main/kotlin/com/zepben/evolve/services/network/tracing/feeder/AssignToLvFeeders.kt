/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
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
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * Convenience class that provides methods for assigning LV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToLvFeeders {

    private val normalTraversal: BasicTraversal<Terminal> = AssociatedTerminalTrace.newNormalTrace()
    private val currentTraversal: BasicTraversal<Terminal> = AssociatedTerminalTrace.newCurrentTrace()
    private lateinit var activeLvFeeder: LvFeeder

    init {
        normalTraversal.addStepAction(::processNormal)
        currentTraversal.addStepAction(::processCurrent)
    }

    fun run(network: NetworkService) {
        val lvFeederStartPoints = network.sequenceOf(LvFeeder::class)
            .mapNotNull { it.normalHeadTerminal }
            .mapNotNull { it.conductingEquipment }
            .toSet()

        configureStopConditions(normalTraversal, lvFeederStartPoints)
        configureStopConditions(currentTraversal, lvFeederStartPoints)

        network.sequenceOf<LvFeeder>().forEach(::run)
    }

    private fun run(lvFeeder: LvFeeder) {
        activeLvFeeder = lvFeeder

        val headTerminal = lvFeeder.normalHeadTerminal ?: return

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

    private fun configureStopConditions(traversal: BasicTraversal<Terminal>, lvFeederStartPoints: Set<ConductingEquipment>) {
        traversal.clearStopConditions()
        traversal.addStopCondition(reachedEquipment(lvFeederStartPoints))
        traversal.addStopCondition(reachedHv)
    }

    private val reachedEquipment: (Set<ConductingEquipment>) -> (Terminal) -> Boolean = { { terminal: Terminal -> it.contains(terminal.conductingEquipment) } }

    private val reachedHv: (Terminal) -> Boolean = { terminal ->
        when (val ec = terminal.conductingEquipment) {
            null -> false
            is PowerTransformer -> {
                ec.ends.any { end -> (end.baseVoltage?.nominalVoltage ?: end.ratedU ?: Int.MAX_VALUE) >= 1000 }
            }
            else -> {
                ec.baseVoltage?.let { it.nominalVoltage >= 1000 } ?: false
            }
        }
    }

    private fun processNormal(terminal: Terminal, isStopping: Boolean): Unit =
        process(terminal.conductingEquipment, ConductingEquipment::addContainer, LvFeeder::addEquipment, isStopping)

    private fun processCurrent(terminal: Terminal, isStopping: Boolean): Unit =
        process(terminal.conductingEquipment, ConductingEquipment::addCurrentContainer, LvFeeder::addCurrentEquipment, isStopping)

    private fun process(
        conductingEquipment: ConductingEquipment?,
        assignFeederToEquipment: (ConductingEquipment, LvFeeder) -> Unit,
        assignEquipmentToFeeder: (LvFeeder, ConductingEquipment) -> Unit,
        isStopping: Boolean
    ) {
        conductingEquipment?.let {
            assignFeederToEquipment(it, activeLvFeeder)
            assignEquipmentToFeeder(activeLvFeeder, it)
        }
    }

}

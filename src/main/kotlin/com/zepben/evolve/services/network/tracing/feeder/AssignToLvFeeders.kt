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
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * Convenience class that provides methods for assigning LV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToLvFeeders {

    private val normalTraversal = AssociatedTerminalTrace.newNormalTrace()
    private val currentTraversal = AssociatedTerminalTrace.newCurrentTrace()
    private lateinit var activeLvFeeder: LvFeeder

    fun run(network: NetworkService) {
        val terminalToAuxEquipment = network.sequenceOf<AuxiliaryEquipment>()
            .filter { it.terminal != null }
            .groupBy { it.terminal!!.mRID }

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

        configureStepActions(normalTraversal, terminalToAuxEquipment)
        configureStepActions(currentTraversal, terminalToAuxEquipment)

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

    private fun configureStepActions(traversal: BasicTraversal<Terminal>, terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>) {
        traversal.clearStepActions()
        normalTraversal.addStepAction(processNormal(terminalToAuxEquipment))
        currentTraversal.addStepAction(processCurrent(terminalToAuxEquipment))
    }

    private fun configureStopConditions(traversal: BasicTraversal<Terminal>, lvFeederStartPoints: Set<ConductingEquipment>) {
        traversal.clearStopConditions()
        traversal.addStopCondition(reachedEquipment(lvFeederStartPoints))
        traversal.addStopCondition(reachedHv)
    }

    private val reachedEquipment: (Set<ConductingEquipment>) -> (Terminal) -> Boolean = { { terminal: Terminal -> it.contains(terminal.conductingEquipment) } }

    private val reachedHv: (Terminal) -> Boolean = { terminal ->
        terminal.conductingEquipment?.baseVoltage?.let { it.nominalVoltage >= 1000 } ?: false
    }

    private fun processNormal(terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>): (Terminal, Boolean) -> Unit =
        { terminal, isStopping ->
            process(
                terminal,
                { eq, feeder ->
                    eq.addContainer(feeder)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch -> eq.operatedByProtectionEquipment.forEach { pe -> pe.addContainer(feeder) }
                    }
                },
                { feeder, eq ->
                    feeder.addEquipment(eq)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch -> eq.operatedByProtectionEquipment.forEach { pe -> feeder.addEquipment(pe) }
                    }
                },
                isStopping,
                terminalToAuxEquipment
            )
        }

    private fun processCurrent(terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>): (Terminal, Boolean) -> Unit =
        { terminal, isStopping ->
            process(
                terminal,
                { eq, feeder ->
                    eq.addCurrentContainer(feeder)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch -> eq.operatedByProtectionEquipment.forEach { pe -> pe.addCurrentContainer(feeder) }
                    }
                },
                { feeder, eq ->
                    feeder.addCurrentEquipment(eq)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch -> eq.operatedByProtectionEquipment.forEach { pe -> feeder.addCurrentEquipment(pe) }
                    }
                },
                isStopping,
                terminalToAuxEquipment
            )
        }

    private fun process(
        terminal: Terminal,
        assignLvFeederToEquipment: (Equipment, LvFeeder) -> Unit,
        assignEquipmentToLvFeeder: (LvFeeder, Equipment) -> Unit,
        isStopping: Boolean,
        terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>
    ) {
        if (isStopping && reachedHv(terminal))
            return

        terminalToAuxEquipment[terminal.mRID]?.forEach {
            assignLvFeederToEquipment(it, activeLvFeeder)
            assignEquipmentToLvFeeder(activeLvFeeder, it)
        }

        terminal.conductingEquipment?.let {
            assignLvFeederToEquipment(it, activeLvFeeder)
            assignEquipmentToLvFeeder(activeLvFeeder, it)
        }
    }

}

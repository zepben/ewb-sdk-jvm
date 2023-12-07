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
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext

/**
 * Convenience class that provides methods for assigning HV/MV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [BasicTraversal].
 */
class AssignToFeeders {

    private val normalTraversal = Tracing.connectedTerminalTrace().stopAtNormallyOpen()
    private val currentTraversal = Tracing.connectedTerminalTrace().stopAtCurrentlyOpen()

    private lateinit var activeFeeder: Feeder

    fun run(network: NetworkService) {
        val terminalToAuxEquipment = network.sequenceOf<AuxiliaryEquipment>()
            .filter { it.terminal != null }
            .groupBy { it.terminal!!.mRID }

        val feederStartPoints = network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal }
            .mapNotNull { it.conductingEquipment }
            .toSet()

        configureStepActions(normalTraversal, terminalToAuxEquipment)
        configureStepActions(currentTraversal, terminalToAuxEquipment)

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

    private fun run(traversal: NetworkTrace<Unit>, headTerminal: Terminal) {
        traversal.reset()
        traversal.run(headTerminal, false)
    }

    private fun configureStepActions(traversal: NetworkTrace<*>, terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>) {
        traversal.clearStepActions()
        normalTraversal.addStepAction(processNormal(terminalToAuxEquipment))
        currentTraversal.addStepAction(processCurrent(terminalToAuxEquipment))
    }

    private fun configureStopConditions(traversal: NetworkTrace<Unit>, feederStartPoints: Set<ConductingEquipment>) {
        traversal.clearStopConditions()
        traversal.addStopCondition(reachedEquipment(feederStartPoints))
        traversal.addStopCondition { step, _ -> reachedSubstationTransformer(step.path.toEquipment) }
        traversal.addStopCondition { step, _ -> reachedLv(step.path.toEquipment) }
    }

    private val reachedEquipment: (Set<ConductingEquipment>) -> (NetworkTraceStep<Unit>, StepContext) -> Boolean =
        { { step, _ -> it.contains(step.path.toEquipment) } }

    private val reachedSubstationTransformer: (ConductingEquipment) -> Boolean = { ce ->
        ce is PowerTransformer && ce.substations.isNotEmpty()
    }

    private val reachedLv: (ConductingEquipment) -> Boolean = { ce ->
        ce.baseVoltage?.let { it.nominalVoltage < 1000 } ?: false
    }

    private fun processNormal(
        terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>
    ): (NetworkTraceStep<Unit>, StepContext) -> Unit =
        { step, context ->
            process(
                step.path,
                context,
                { eq, feeder ->
                    feeder.addEquipment(eq)
                    eq.addContainer(feeder)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch ->
                            eq.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                                system.addContainer(feeder)
                            }
                    }
                },
                context.isStopping,
                terminalToAuxEquipment
            )
        }

    private fun processCurrent(
        terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>
    ): (NetworkTraceStep<Unit>, StepContext) -> Unit =
        { step, context ->
            process(
                step.path,
                context,
                { eq, feeder ->
                    feeder.addCurrentEquipment(eq)
                    eq.addCurrentContainer(feeder)
                    // Handle classes extending Equipment
                    when (eq) {
                        is ProtectedSwitch ->
                            eq.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                                system.addCurrentContainer(feeder)
                            }
                    }
                },
                context.isStopping,
                terminalToAuxEquipment
            )
        }

    private fun process(
        stepPath: StepPath,
        stepContext: StepContext,
        associateFeederAndEquipment: (Equipment, Feeder) -> Unit,
        isStopping: Boolean,
        terminalToAuxEquipment: Map<String, Collection<AuxiliaryEquipment>>
    ) {
        if (stepPath.tracedInternally && !stepContext.isStartItem)
            return

        if (isStopping && (reachedLv(stepPath.toEquipment) || reachedSubstationTransformer(stepPath.toEquipment)))
            return

        stepPath.toTerminal?.let { terminal ->
            terminalToAuxEquipment[terminal.mRID]?.forEach {
                associateFeederAndEquipment(it, activeFeeder)
            }
        }

        associateFeederAndEquipment(stepPath.toEquipment, activeFeeder)
    }

}

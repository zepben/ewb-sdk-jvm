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
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext

/**
 * Convenience class that provides methods for assigning HV/MV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [NetworkTrace].
 */
class AssignToFeeders(
    internal val stateOperators: NetworkStateOperators
) {

    /**
     * Assign equipment to feeders in the specified network, given an optional start terminal.
     * * When a start terminal is provided, the trace will assign all feeders associated with the terminals equipment to all connected equipment.
     * * If no start terminal is provided, all feeder head terminals in the network will be used instead, assigning their associated feeder.
     */
    @JvmOverloads
    fun run(network: NetworkService, startTerminal: Terminal? = null) {
        val feederStartPoints = network.feederStartPoints
        val terminalToAuxEquipment = network.auxEquipmentByTerminal

        if (startTerminal == null) {
            network.sequenceOf<Feeder>().forEach {
                run(it.normalHeadTerminal, feederStartPoints, terminalToAuxEquipment, listOf(it))
            }
        } else
            run(startTerminal, feederStartPoints, terminalToAuxEquipment, startTerminal.feeders(stateOperators))
    }

    /**
     * Assign equipment to feeders tracing out from the supplied terminal.
     *
     * @param terminal The [Terminal] to trace from.
     * @param feederStartPoints A set of all [ConductingEquipment] containing a feeder head terminal. Must contain a list of all feeder
     * head equipment that may be traced to from [terminal] to prevent excess tracing onto the sub-transmission network.
     * @param terminalToAuxEquipment A map of all [AuxiliaryEquipment] by their attached [Terminal] that can be traced from [terminal].
     * @param feedersToAssign The list of feeders to assign to all traced equipment.
     */
    fun run(
        terminal: Terminal?,
        feederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        feedersToAssign: List<Feeder>,
    ) {
        // If there is no terminal, or there are no feeders assigned to the terminals equipment, then we have nothing to do.
        if ((terminal == null) || (feedersToAssign.isEmpty()))
            return

        val traversal = createTrace(terminalToAuxEquipment, feederStartPoints, feedersToAssign)
        traversal.run(terminal, canStopOnStartItem = false)
    }

    private fun createTrace(
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        feederStartPoints: Set<ConductingEquipment>,
        feedersToAssign: List<Feeder>,
    ): NetworkTrace<Unit> {
        return Tracing.networkTrace(stateOperators, NetworkTraceActionType.ALL_STEPS)
            .addCondition { stopAtOpen() }
            .addStopCondition { (path), _ -> feederStartPoints.contains(path.toEquipment) }
            .addQueueCondition { (path), _, _, _ -> !reachedSubstationTransformer(path.toEquipment) }
            .addQueueCondition { (path), _, _, _ -> !reachedLv(path.toEquipment) }
            .addStepAction { (path), context -> process(path, context, terminalToAuxEquipment, feedersToAssign) }
    }

    private val reachedSubstationTransformer: (ConductingEquipment) -> Boolean = { ce ->
        ce is PowerTransformer && ce.substations.isNotEmpty()
    }

    private val reachedLv: (ConductingEquipment) -> Boolean = { ce ->
        ce.baseVoltage?.let { it.nominalVoltage < 1000 } ?: false
    }

    private fun process(
        stepPath: NetworkTraceStep.Path,
        stepContext: StepContext,
        terminalToAuxEquipment: Map<Terminal, Collection<AuxiliaryEquipment>>,
        feedersToAssign: List<Feeder>
    ) {
        if (stepPath.tracedInternally && !stepContext.isStartItem)
            return

        terminalToAuxEquipment[stepPath.toTerminal]?.forEach { auxEq ->
            feedersToAssign.forEach { feeder -> stateOperators.associateEquipmentAndContainer(auxEq, feeder) }
        }

        feedersToAssign.forEach { feeder -> stateOperators.associateEquipmentAndContainer(stepPath.toEquipment, feeder) }
        when (stepPath.toEquipment) {
            is ProtectedSwitch ->
                stepPath.toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system }.forEach { system ->
                    feedersToAssign.forEach {
                        stateOperators.associateEquipmentAndContainer(system, it)
                    }
                }
        }
    }

}

/**
 * A map of all [AuxiliaryEquipment] in the [NetworkService] indexed by their terminals.
 */
// TODO [Review]: Where should this be located?
val NetworkService.auxEquipmentByTerminal: Map<Terminal, List<AuxiliaryEquipment>>
    get() = sequenceOf<AuxiliaryEquipment>()
        .filter { it.terminal != null }
        .groupBy { it.terminal!! }

/**
 * A set of all [ConductingEquipment] in the [NetworkService] that are at the top of a feeder.
 */
// TODO [Review]: Where should this be located?
val NetworkService.feederStartPoints: Set<ConductingEquipment>
    get() = sequenceOf<Feeder>()
        .mapNotNull { it.normalHeadTerminal?.conductingEquipment }
        .toSet()

/**
 * Find the feeders assigned to the equipment of a terminal for the appropriate network state.
 */
// TODO [Review]: Where should this be located?
fun Terminal.feeders(stateOperators: NetworkStateOperators): List<Feeder> =
    conductingEquipment?.let { stateOperators.getContainers(it).filterIsInstance<Feeder>() }.orEmpty()

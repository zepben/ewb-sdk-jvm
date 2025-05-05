/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.wires.PowerElectronicsConnection
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.auxEquipmentByTerminal
import com.zepben.evolve.services.network.feederStartPoints
import com.zepben.evolve.services.network.lvFeederStartPoints
import com.zepben.evolve.services.network.tracing.networktrace.*
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import org.slf4j.Logger

/**
 * Convenience class that provides methods for assigning HV/MV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [NetworkTrace].
 */
class AssignToFeeders(
    private val debugLogger: Logger?
) {

    /**
     * Assign equipment to feeders in the specified network, given an optional start terminal.
     *
     * @param network The [NetworkService] to process.
     * @param startTerminal An optional [Terminal] to start from:
     * * When a start terminal is provided, the trace will assign all feeders associated with the terminals equipment to all connected equipment.
     * * If no start terminal is provided, all feeder head terminals in the network will be used instead, assigning their associated feeder.
     */
    @JvmOverloads
    fun run(
        network: NetworkService,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        startTerminal: Terminal? = null
    ): Unit = AssignToFeedersInternal(networkStateOperators, debugLogger)
        .run(network, startTerminal)

    /**
     * Assign equipment to feeders tracing out from the supplied terminal.
     *
     * @param terminal The [Terminal] to trace from.
     * @param feederStartPoints A set of all [ConductingEquipment] containing a feeder head terminal. Must contain a list of all feeder
     * head equipment that may be traced to from [terminal] to prevent excess tracing onto the sub-transmission network.
     * @param lvFeederStartPoints A set of all [ConductingEquipment] containing an LV feeder head terminal. Must contain a list of all LV
     * feeder head equipment that may be traced to from [terminal].
     * @param terminalToAuxEquipment A map of all [AuxiliaryEquipment] by their attached [Terminal] that can be traced from [terminal].
     * @param feedersToAssign The list of feeders to assign to all traced equipment.
     */
    @JvmOverloads
    fun run(
        terminal: Terminal?,
        feederStartPoints: Set<ConductingEquipment>,
        lvFeederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        feedersToAssign: List<Feeder>,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL
    ): Unit = AssignToFeedersInternal(networkStateOperators, debugLogger)
        .run(terminal, feederStartPoints, lvFeederStartPoints, terminalToAuxEquipment, feedersToAssign)

    private class AssignToFeedersInternal(
        private val stateOperators: NetworkStateOperators,
        private val debugLogger: Logger?
    ) {

        fun run(network: NetworkService, startTerminal: Terminal?) {
            val feederStartPoints = network.feederStartPoints
            val lvFeederStartPoints = network.lvFeederStartPoints
            val terminalToAuxEquipment = network.auxEquipmentByTerminal

            if (startTerminal == null) {
                network.sequenceOf<Feeder>().forEach {
                    run(it.normalHeadTerminal, feederStartPoints, lvFeederStartPoints, terminalToAuxEquipment, listOf(it))
                }
            } else
                run(startTerminal, feederStartPoints, lvFeederStartPoints, terminalToAuxEquipment, startTerminal.feeders)
        }

        fun run(
            terminal: Terminal?,
            feederStartPoints: Set<ConductingEquipment>,
            lvFeederStartPoints: Set<ConductingEquipment>,
            terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
            feedersToAssign: List<Feeder>
        ) {
            // If there is no terminal, or there are no feeders to assign, then we have nothing to do.
            if ((terminal == null) || (feedersToAssign.isEmpty()))
                return

            val startCe = terminal.conductingEquipment
            when {
                startCe is Switch && stateOperators.isOpen(startCe) -> feedersToAssign.associateEquipment(startCe)
                else -> {
                    val traversal = createTrace(terminalToAuxEquipment, feederStartPoints, lvFeederStartPoints, feedersToAssign)
                    traversal.run(terminal, canStopOnStartItem = false)
                }
            }
        }

        private fun createTrace(
            terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
            feederStartPoints: Set<ConductingEquipment>,
            lvFeederStartPoints: Set<ConductingEquipment>,
            feedersToAssign: List<Feeder>,
        ): NetworkTrace<Unit> {
            fun reachedSubstationTransformer(ce: ConductingEquipment) = ce is PowerTransformer && ce.substations.isNotEmpty()
            fun reachedLv(ce: ConductingEquipment) = ce.baseVoltage?.let { it.nominalVoltage < 1000 } == true

            return Tracing.networkTrace(stateOperators, NetworkTraceActionType.ALL_STEPS, debugLogger, name = "AssignToFeeders(${stateOperators.description})")
                .addCondition { stopAtOpen() }
                .addStopCondition { (path), _ -> feederStartPoints.contains(path.toEquipment) }
                .addQueueCondition { (path), _, _, _ -> !reachedSubstationTransformer(path.toEquipment) }
                .addQueueCondition { (path), _, _, _ -> !reachedLv(path.toEquipment) }
                .addStepAction { (path), context -> process(path, context, terminalToAuxEquipment, lvFeederStartPoints, feedersToAssign) }
        }

        private fun process(
            stepPath: NetworkTraceStep.Path,
            stepContext: StepContext,
            terminalToAuxEquipment: Map<Terminal, Collection<AuxiliaryEquipment>>,
            lvFeederStartPoints: Set<ConductingEquipment>,
            feedersToAssign: List<Feeder>
        ) {
            if (stepPath.tracedInternally && !stepContext.isStartItem)
                return

            feedersToAssign.associateEquipment(terminalToAuxEquipment[stepPath.toTerminal].orEmpty())
            feedersToAssign.associateEquipment(stepPath.toEquipment)

            when (stepPath.toEquipment) {
                is PowerTransformer -> feedersToAssign.tryEnergizeLvFeeders(stepPath.toEquipment, lvFeederStartPoints)
                is ProtectedSwitch -> feedersToAssign.associateRelaySystems(stepPath.toEquipment)
                is PowerElectronicsConnection -> feedersToAssign.associatePowerElectronicUnits(stepPath.toEquipment)
            }
        }

        private val Terminal.feeders: List<Feeder>
            get() = conductingEquipment.getFilteredContainers<Feeder>(stateOperators).toList()

        private fun Iterable<EquipmentContainer>.associateEquipment(equipment: Iterable<Equipment>) = forEach { feeder ->
            equipment.forEach { stateOperators.associateEquipmentAndContainer(it, feeder) }
        }

        private fun Iterable<EquipmentContainer>.associateEquipment(equipment: Equipment) = forEach { feeder ->
            stateOperators.associateEquipmentAndContainer(equipment, feeder)
        }

        private fun Iterable<EquipmentContainer>.associateRelaySystems(toEquipment: ProtectedSwitch) {
            associateEquipment(toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system })
        }

        private fun Iterable<EquipmentContainer>.associatePowerElectronicUnits(toEquipment: PowerElectronicsConnection) {
            associateEquipment(toEquipment.units)
        }

        private fun Iterable<Feeder>.tryEnergizeLvFeeders(toEquipment: PowerTransformer, lvFeederStartPoints: Set<ConductingEquipment>) {
            //
            // NOTE: This will need to change if we stop assigning site internals to the HV feeder as it will stop before it gets here.
            //

            // Check to see if the change to LV is part of a dist transformer site. If so, we want to energize all LV feeders on any equipment
            // in the site, not just the one on the first LV terminal; otherwise, just energize the LV feeders on this equipment.
            val sites = toEquipment.getFilteredContainers<Site>(stateOperators)
            if (sites.isNotEmpty())
                energizes(sites.findLvFeeders(lvFeederStartPoints, stateOperators))
            else
                energizes(toEquipment.getFilteredContainers<LvFeeder>(stateOperators))
        }

        private fun Iterable<Feeder>.energizes(lvFeeders: Iterable<LvFeeder>) = forEach { feeder ->
            lvFeeders.forEach { stateOperators.associateEnergizingFeeder(feeder, it) }
        }

    }

}

/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.auxEquipmentByTerminal
import com.zepben.evolve.services.network.lvFeederStartPoints
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext

/**
 * Convenience class that provides methods for assigning LV feeders on a [NetworkService].
 * Requires that a Feeder have a normalHeadTerminal with associated ConductingEquipment.
 * This class is backed by a [NetworkTrace].
 */
class AssignToLvFeeders {

    /**
     * Assign equipment to LV feeders in the specified network, given an optional start terminal.
     *
     * @param network The [NetworkService] to process.
     * @param startTerminal An optional [Terminal] to start from:
     * * When a start terminal is provided, the trace will assign all LV feeders associated with the terminals equipment to all connected equipment.
     * * If no start terminal is provided, all LV feeder head terminals in the network will be used instead, assigning their associated feeder.
     *
     * NOTE: When starting from each LV feeder head, each LV feeder will also be associated with its energizing feeder(s).
     */
    @JvmOverloads
    fun run(
        network: NetworkService,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        startTerminal: Terminal? = null
    ): Unit = AssignToLvFeedersInternal(networkStateOperators).run(network, startTerminal)

    /**
     * Assign equipment to LV feeders tracing out from the supplied terminal.
     *
     * @param terminal The [Terminal] to trace from.
     * @param lvFeederStartPoints A set of all [ConductingEquipment] containing an LV feeder head terminal. Must contain a list of all LV
     * feeder head equipment that may be traced to from [terminal].
     * @param terminalToAuxEquipment A map of all [AuxiliaryEquipment] by their attached [Terminal] that can be traced from [terminal].
     * @param lvFeedersToAssign The list of LV feeders to assign to all traced equipment.
     */
    @JvmOverloads
    fun run(
        terminal: Terminal?,
        lvFeederStartPoints: Set<ConductingEquipment>,
        terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
        lvFeedersToAssign: List<LvFeeder>,
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL
    ): Unit = AssignToLvFeedersInternal(networkStateOperators).run(terminal, lvFeederStartPoints, terminalToAuxEquipment, lvFeedersToAssign)

    private class AssignToLvFeedersInternal(
        val stateOperators: NetworkStateOperators
    ) {

        fun run(network: NetworkService, startTerminal: Terminal?) {
            val terminalToAuxEquipment = network.auxEquipmentByTerminal
            val lvFeederStartPoints = network.lvFeederStartPoints

            if (startTerminal == null) {
                network.sequenceOf<LvFeeder>().forEach { lvFeeder ->
                    lvFeeder.normalHeadTerminal?.conductingEquipment?.also { headEquipment ->
                        headEquipment.getFilteredContainers<Feeder>(stateOperators).forEach { feeder ->
                            stateOperators.associateEnergizingFeeder(feeder, lvFeeder)
                        }
                    }

                    // We can run from each LV feeder as we process them, as being associated with their energizing feeders is not a requirement of the trace.
                    run(lvFeeder.normalHeadTerminal, lvFeederStartPoints, terminalToAuxEquipment, listOf(lvFeeder))
                }
            } else {
                run(startTerminal, lvFeederStartPoints, terminalToAuxEquipment, startTerminal.lvFeeders)
            }
        }

        fun run(
            terminal: Terminal?,
            lvFeederStartPoints: Set<ConductingEquipment>,
            terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
            lvFeedersToAssign: List<LvFeeder>
        ) {
            // If there is no terminal, or there are no LV feeders to assign, then we have nothing to do.
            if ((terminal == null) || (lvFeedersToAssign.isEmpty()))
                return

            val traversal = createTrace(terminalToAuxEquipment, lvFeederStartPoints, lvFeedersToAssign)
            traversal.run(terminal, false, canStopOnStartItem = false)
        }

        private fun createTrace(
            terminalToAuxEquipment: Map<Terminal, List<AuxiliaryEquipment>>,
            lvFeederStartPoints: Set<ConductingEquipment>,
            lvFeedersToAssign: List<LvFeeder>,
        ): NetworkTrace<Boolean> {
            fun reachedHv(ce: ConductingEquipment) = ce.baseVoltage?.let { it.nominalVoltage >= 1000 } == true

            return Tracing.networkTrace(stateOperators, NetworkTraceActionType.ALL_STEPS, computeData = { _, _, nextPath ->
                // Store if we found an LV feeder head in the `data` to prevent looking this up multiple times on each iteration.
                lvFeederStartPoints.contains(nextPath.toEquipment)
            })
                .addCondition { stopAtOpen() }
                .addStopCondition { (_, foundLvFeeder), _ -> foundLvFeeder }
                // If we have found an LV feeder head, we want to step on it regardless of if it is HV or not. Sometime people configure their transformers with
                // the base voltage of the HV voltage, which will exclude these otherwise, but we won't keep processing past it as they are also stop conditions
                .addQueueCondition { (path, foundLvFeeder), _, _, _ -> foundLvFeeder || !reachedHv(path.toEquipment) }
                .addStepAction { (path, foundLvFeeder), context ->
                    process(
                        path,
                        foundLvFeeder,
                        context,
                        terminalToAuxEquipment,
                        lvFeederStartPoints,
                        lvFeedersToAssign
                    )
                }
        }

        private fun process(
            stepPath: NetworkTraceStep.Path,
            foundLvFeeder: Boolean,
            stepContext: StepContext,
            terminalToAuxEquipment: Map<Terminal, Collection<AuxiliaryEquipment>>,
            lvFeederStartPoints: Set<ConductingEquipment>,
            lvFeedersToAssign: List<LvFeeder>
        ) {
            if (stepPath.tracedInternally && !stepContext.isStartItem)
                return

            // It might be tempting to check `stepContext.isStopping`, but that would also pick up open points between LV feeders which is not good.
            if (foundLvFeeder) {
                val foundLvFeeders = stepPath.toEquipment.findLvFeeders(lvFeederStartPoints)

                // Energize the LV feeders are a processing by the energizing feeders of what we found.
                lvFeedersToAssign.energizedBy(foundLvFeeders.flatMap { stateOperators.getEnergizingFeeders(it) })

                // Energize the LV feeders we found by the energizing feeders we are processing
                foundLvFeeders.energizedBy(lvFeedersToAssign.flatMap { stateOperators.getEnergizingFeeders(it) })
            }

            lvFeedersToAssign.associateEquipment(terminalToAuxEquipment[stepPath.toTerminal].orEmpty())
            lvFeedersToAssign.associateEquipment(stepPath.toEquipment)

            when (stepPath.toEquipment) {
                is ProtectedSwitch -> lvFeedersToAssign.associateRelaySystems(stepPath.toEquipment)
            }
        }

        private fun ConductingEquipment.findLvFeeders(lvFeederStartPoints: Set<ConductingEquipment>): Iterable<LvFeeder> {
            // Check to see if the LV feeder head is part of a dist transformer site. If so, we want to find all LV feeders on any equipment
            // in the site, not just the LV feeder of the head we found.
            val sites = getFilteredContainers<Site>(stateOperators)
            return if (sites.isNotEmpty())
                sites.findLvFeeders(lvFeederStartPoints, stateOperators)
            else
                getFilteredContainers<LvFeeder>(stateOperators)
        }


        private val Terminal.lvFeeders: List<LvFeeder>
            get() = conductingEquipment.getFilteredContainers<LvFeeder>(stateOperators).toList()

        private fun Iterable<EquipmentContainer>.associateEquipment(equipment: Iterable<Equipment>) = forEach { feeder ->
            equipment.forEach { stateOperators.associateEquipmentAndContainer(it, feeder) }
        }

        private fun Iterable<EquipmentContainer>.associateEquipment(equipment: Equipment) = forEach { feeder ->
            stateOperators.associateEquipmentAndContainer(equipment, feeder)
        }

        private fun Iterable<EquipmentContainer>.associateRelaySystems(toEquipment: ProtectedSwitch) {
            associateEquipment(toEquipment.relayFunctions.flatMap { it.schemes }.mapNotNull { it.system })
        }

        private fun Iterable<LvFeeder>.energizedBy(feeders: Iterable<Feeder>) = forEach { lvFeeder ->
            feeders.forEach { stateOperators.associateEnergizingFeeder(it, lvFeeder) }
        }

    }

}

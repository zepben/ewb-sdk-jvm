/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.connectivity.XyCandidatePhasePaths
import com.zepben.ewb.services.network.tracing.connectivity.XyCandidatePhasePaths.Companion.isAfter
import com.zepben.ewb.services.network.tracing.connectivity.XyCandidatePhasePaths.Companion.isBefore
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import org.slf4j.Logger

class PhaseInferrer(
    private val debugLogger: Logger?
) {

    data class InferredPhase(val conductingEquipment: ConductingEquipment, val suspect: Boolean) {
        fun description(): String = if (suspect) {
            "Inferred missing phases for '${conductingEquipment.name}' [${conductingEquipment.mRID}] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."
        } else {
            "Inferred missing phase for '${conductingEquipment.name}' [${conductingEquipment.mRID}] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."
        }
    }

    @JvmOverloads
    fun run(network: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): Collection<InferredPhase> {
        val tracking = mutableMapOf<ConductingEquipment, Boolean>()

        PhaseInferrerInternal(networkStateOperators, debugLogger).inferMissingPhases(network, tracking)

        return tracking.map { InferredPhase(it.key, it.value) }
    }

    private class PhaseInferrerInternal(
        val stateOperators: NetworkStateOperators,
        private val debugLogger: Logger?
    ) {
        fun inferMissingPhases(network: NetworkService, tracking: MutableMap<ConductingEquipment, Boolean>) {
            do {
                val terminalsMissingPhases = network.listOf<Terminal> { ((it.connectivityNode?.terminals?.size ?: 0) > 1) && hasNonePhase(it) }
                val terminalsMissingXyPhases = terminalsMissingPhases.filter { t -> hasXYPhases(t) }
            } while (
                terminalsMissingPhases.process { setMissingToNominal(it, tracking) } or
                terminalsMissingXyPhases.process { inferXyPhases(it, 1, tracking) } or
                terminalsMissingXyPhases.process { inferXyPhases(it, 4, tracking) }
            )
        }

        private fun hasNonePhase(terminal: Terminal): Boolean =
            stateOperators.phaseStatus(terminal).let { phases ->
                terminal.phases.singlePhases.any { phases[it] == SinglePhaseKind.NONE }
            }

        private fun hasXYPhases(terminal: Terminal): Boolean =
            terminal.phases.singlePhases.contains(SinglePhaseKind.Y) || terminal.phases.singlePhases.contains(SinglePhaseKind.X)

        private fun List<Terminal>.findTerminalAtStartOfMissingPhases(): List<Terminal> =
            missingFromDownToUp().takeUnless { it.isEmpty() }
                ?: missingFromDownToAny().takeUnless { it.isEmpty() }
                ?: missingFromAny()

        private fun List<Terminal>.missingFromDownToUp(): List<Terminal> =
            filter { terminal ->
                hasNonePhase(terminal) &&
                    FeederDirection.UPSTREAM in stateOperators.getDirection(terminal) &&
                    terminal.connectivityNode!!.terminals
                        .asSequence()
                        .filter { it != terminal }
                        .filter { FeederDirection.DOWNSTREAM in stateOperators.getDirection(it) }
                        .any { !hasNonePhase(it) }
            }

        private fun List<Terminal>.missingFromDownToAny(): List<Terminal> =
            filter { terminal ->
                hasNonePhase(terminal) &&
                    terminal.connectivityNode!!.terminals
                        .asSequence()
                        .filter { it != terminal }
                        .filter { FeederDirection.DOWNSTREAM in stateOperators.getDirection(it) }
                        .any { !hasNonePhase(it) }
            }

        private fun List<Terminal>.missingFromAny(): List<Terminal> =
            filter { terminal ->
                hasNonePhase(terminal) &&
                    terminal.connectivityNode!!.terminals
                        .asSequence()
                        .filter { it != terminal }
                        .any { !hasNonePhase(it) }
            }

        private fun List<Terminal>.process(processor: (Terminal) -> Boolean): Boolean {
            var terminalsToProcess = findTerminalAtStartOfMissingPhases()

            var hasProcessed = false
            do {
                var continueProcessing = false

                terminalsToProcess.forEach { continueProcessing = processor(it) || continueProcessing }
                terminalsToProcess = findTerminalAtStartOfMissingPhases()

                hasProcessed = hasProcessed || continueProcessing
            } while (continueProcessing)

            return hasProcessed
        }

        private fun setMissingToNominal(terminal: Terminal, tracking: MutableMap<ConductingEquipment, Boolean>): Boolean {
            val phases = stateOperators.phaseStatus(terminal)

            val phasesToProcess = terminal.phases.singlePhases
                .asSequence()
                .filter { (it != SinglePhaseKind.X) && (it != SinglePhaseKind.Y) }
                .filter { phases[it] === SinglePhaseKind.NONE }
                .toList()

            if (phasesToProcess.isEmpty())
                return false

            phasesToProcess.forEach { phases[it] = it }
            continuePhases(terminal)

            terminal.conductingEquipment?.also { tracking[it] = false }

            return true
        }

        private fun inferXyPhases(terminal: Terminal, maxMissingPhases: Int, tracking: MutableMap<ConductingEquipment, Boolean>): Boolean {
            val none = mutableListOf<SinglePhaseKind>()
            val usedPhases = mutableSetOf<SinglePhaseKind>()

            val conductingEquipment = terminal.conductingEquipment ?: return false

            val phases = stateOperators.phaseStatus(terminal)
            terminal.phases.singlePhases.forEach { nominalPhase ->
                phases[nominalPhase].also {
                    if (it === SinglePhaseKind.NONE)
                        none.add(nominalPhase)
                    else
                        usedPhases.add(it)
                }
            }

            if (none.isEmpty() || (none.size > maxMissingPhases))
                return false

            tracking[conductingEquipment] = true

            var hadChanges = false
            for (nominalPhase in none) {
                val newPhase = if (nominalPhase == SinglePhaseKind.X)
                    XyCandidatePhasePaths.xPriority.firstUnused(usedPhases) { it.isBefore(phases[SinglePhaseKind.Y]) }
                else
                    XyCandidatePhasePaths.yPriority.firstUnused(usedPhases) { it.isAfter(phases[SinglePhaseKind.X]) }

                if (newPhase != SinglePhaseKind.NONE) {
                    phases[nominalPhase] = newPhase
                    usedPhases.add(phases[nominalPhase])
                    hadChanges = true
                }
            }

            continuePhases(terminal)
            return hadChanges
        }

        private fun continuePhases(terminal: Terminal) {
            val setPhasesTrace = SetPhases(debugLogger)
            terminal.otherTerminals().forEach { other ->
                setPhasesTrace.run(terminal, other, terminal.phases.singlePhases, stateOperators)
            }
        }

        private fun List<SinglePhaseKind>.firstUnused(usedPhases: MutableSet<SinglePhaseKind>, validate: (SinglePhaseKind) -> Boolean): SinglePhaseKind =
            firstOrNull { !usedPhases.contains(it) && validate(it) } ?: SinglePhaseKind.NONE
    }

}

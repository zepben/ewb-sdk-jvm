/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.connectivity.XyCandidatePhasePaths
import com.zepben.evolve.services.network.tracing.connectivity.XyCandidatePhasePaths.Companion.isAfter
import com.zepben.evolve.services.network.tracing.connectivity.XyCandidatePhasePaths.Companion.isBefore
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import org.slf4j.LoggerFactory

class PhaseInferrer {

    private val logger = LoggerFactory.getLogger(javaClass)
    private var tracking = mutableMapOf<ConductingEquipment, Boolean>()

    fun run(network: NetworkService) {
        tracking = mutableMapOf()

        inferMissingPhases(network, PhaseSelector.NORMAL_PHASES, DirectionSelector.NORMAL_DIRECTION)
        inferMissingPhases(network, PhaseSelector.CURRENT_PHASES, DirectionSelector.CURRENT_DIRECTION)

        tracking.forEach { (conductingEquipment, hasSuspectInferred) ->
            if (hasSuspectInferred) {
                logger.warn(
                    "*** Action Required *** Inferred missing phases for '{}' [{}] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.",
                    conductingEquipment.name,
                    conductingEquipment.mRID
                )
            } else {
                logger.warn(
                    "*** Action Required *** Inferred missing phase for '{}' [{}] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.",
                    conductingEquipment.name,
                    conductingEquipment.mRID
                )
            }
        }
    }

    private fun inferMissingPhases(network: NetworkService, phaseSelector: PhaseSelector, directionSelector: DirectionSelector) {
        do {
            val terminalsMissingPhases = network.listOf<Terminal> { ((it.connectivityNode?.terminals?.size ?: 0) > 1) && hasNonePhase(it, phaseSelector) }
            val terminalsMissingXyPhases = terminalsMissingPhases.filter { t -> hasXYPhases(t) }
        } while (
            terminalsMissingPhases.process(phaseSelector, directionSelector) { setMissingToNominal(it, phaseSelector) } or
            terminalsMissingXyPhases.process(phaseSelector, directionSelector) { inferXyPhases(it, phaseSelector, 1) } or
            terminalsMissingXyPhases.process(phaseSelector, directionSelector) { inferXyPhases(it, phaseSelector, 4) }
        )
    }

    private fun hasNonePhase(terminal: Terminal, phaseSelector: PhaseSelector): Boolean =
        phaseSelector.phases(terminal).let { phases ->
            terminal.phases.singlePhases.any { phases[it] == SinglePhaseKind.NONE }
        }

    private fun hasXYPhases(terminal: Terminal): Boolean =
        terminal.phases.singlePhases.contains(SinglePhaseKind.Y) || terminal.phases.singlePhases.contains(SinglePhaseKind.X)

    private fun findTerminalAtStartOfMissingPhases(
        terminals: List<Terminal>,
        phaseSelector: PhaseSelector,
        directionSelector: DirectionSelector
    ): List<Terminal> =
        terminals.missingFromDownToUp(phaseSelector, directionSelector).takeUnless { it.isEmpty() }
            ?: terminals.missingFromDownToAny(phaseSelector, directionSelector).takeUnless { it.isEmpty() }
            ?: terminals.missingFromAny(phaseSelector)

    private fun List<Terminal>.missingFromDownToUp(phaseSelector: PhaseSelector, directionSelector: DirectionSelector): List<Terminal> =
        filter { terminal ->
            hasNonePhase(terminal, phaseSelector) &&
                FeederDirection.UPSTREAM in directionSelector.select(terminal).value &&
                terminal.connectivityNode!!.terminals
                    .asSequence()
                    .filter { it != terminal }
                    .filter { FeederDirection.DOWNSTREAM in directionSelector.select(it).value }
                    .any { !hasNonePhase(it, phaseSelector) }
        }

    private fun List<Terminal>.missingFromDownToAny(phaseSelector: PhaseSelector, directionSelector: DirectionSelector): List<Terminal> =
        filter { terminal ->
            hasNonePhase(terminal, phaseSelector) &&
                terminal.connectivityNode!!.terminals
                    .asSequence()
                    .filter { it != terminal }
                    .filter { FeederDirection.DOWNSTREAM in directionSelector.select(it).value }
                    .any { !hasNonePhase(it, phaseSelector) }
        }

    private fun List<Terminal>.missingFromAny(phaseSelector: PhaseSelector): List<Terminal> =
        filter { terminal ->
            hasNonePhase(terminal, phaseSelector) &&
                terminal.connectivityNode!!.terminals
                    .asSequence()
                    .filter { it != terminal }
                    .any { !hasNonePhase(it, phaseSelector) }
        }

    private fun List<Terminal>.process(phaseSelector: PhaseSelector, directionSelector: DirectionSelector, processor: (Terminal) -> Boolean): Boolean {
        var terminalsToProcess = findTerminalAtStartOfMissingPhases(this, phaseSelector, directionSelector)

        var hasProcessed = false
        do {
            var continueProcessing = false

            terminalsToProcess.forEach { continueProcessing = processor(it) || continueProcessing }
            terminalsToProcess = findTerminalAtStartOfMissingPhases(this, phaseSelector, directionSelector)

            hasProcessed = hasProcessed || continueProcessing
        } while (continueProcessing)

        return hasProcessed
    }

    private fun setMissingToNominal(terminal: Terminal, phaseSelector: PhaseSelector): Boolean {
        val phases = phaseSelector.phases(terminal)

        val phasesToProcess = terminal.phases.singlePhases
            .asSequence()
            .filter { (it != SinglePhaseKind.X) && (it != SinglePhaseKind.Y) }
            .filter { phases[it] === SinglePhaseKind.NONE }
            .toList()

        if (phasesToProcess.isEmpty())
            return false

        phasesToProcess.forEach { phases[it] = it }
        continuePhases(terminal, phaseSelector)

        terminal.conductingEquipment?.also { tracking[it] = false }

        return true
    }

    private fun inferXyPhases(terminal: Terminal, phaseSelector: PhaseSelector, maxMissingPhases: Int): Boolean {
        val none = mutableListOf<SinglePhaseKind>()
        val usedPhases = mutableSetOf<SinglePhaseKind>()

        val conductingEquipment = terminal.conductingEquipment ?: return false

        val phases = phaseSelector.phases(terminal)
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

        continuePhases(terminal, phaseSelector)
        return hadChanges
    }

    private fun continuePhases(terminal: Terminal, phaseSelector: PhaseSelector) {
        terminal.conductingEquipment?.also { ce ->
            ce.terminals
                .asSequence()
                .filter { it != terminal }
                .forEach { other ->
                    Tracing.setPhases().apply {
                        spreadPhases(terminal, other, phaseSelector = phaseSelector)
                        run(other, phaseSelector)
                    }
                }
        }
    }

    private fun List<SinglePhaseKind>.firstUnused(usedPhases: MutableSet<SinglePhaseKind>, validate: (SinglePhaseKind) -> Boolean): SinglePhaseKind =
        firstOrNull { !usedPhases.contains(it) && validate(it) } ?: SinglePhaseKind.NONE

}

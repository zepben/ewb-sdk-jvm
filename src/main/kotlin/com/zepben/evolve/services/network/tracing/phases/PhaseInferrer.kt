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
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import org.slf4j.LoggerFactory

class PhaseInferrer {

    private var tracking = mutableMapOf<ConductingEquipment, Boolean>()

    fun run(network: NetworkService) {
        tracking = mutableMapOf()

        inferMissingPhases(network, PhaseSelector.NORMAL_PHASES)
        inferMissingPhases(network, PhaseSelector.CURRENT_PHASES)

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

    private fun inferMissingPhases(network: NetworkService, phaseSelector: PhaseSelector) {
        val breakers = network.listOf<Breaker>()
        var terminals = findTerminalAtStartOfMissingPhases(network.listOf(Terminal::class.java), phaseSelector)

        var didUpdate: Boolean
        do {
            didUpdate = setMissingToNominal(breakers, terminals, phaseSelector)
            terminals = findTerminalAtStartOfMissingPhases(network.listOf(Terminal::class.java), phaseSelector)
        } while (didUpdate)

        terminals = terminals.filter { t -> hasXYPhases(t) }
        var previousCount = 0
        while (previousCount != terminals.size) {
            previousCount = terminals.size
            terminals.forEach { inferPhases(breakers, it, phaseSelector, 1) }
            terminals = findTerminalAtStartOfMissingPhases(terminals, phaseSelector)
        }

        terminals.forEach { inferPhases(breakers, it, phaseSelector, 4) }
    }

    private fun hasXYPhases(terminal: Terminal): Boolean {
        return terminal.phases.singlePhases().contains(SinglePhaseKind.Y) || terminal.phases.singlePhases().contains(SinglePhaseKind.X)
    }

    private fun findTerminalAtStartOfMissingPhases(terminals: List<Terminal>, phaseSelector: PhaseSelector): List<Terminal> {
        return terminals
            .filter { terminal ->
                hasDirectionOf(FeederDirection.NONE, terminal, phaseSelector) &&
                    terminal.connectivityNode?.terminals
                        ?.filter { other -> other != terminal }
                        ?.filter { other -> !other.phases.singlePhases().containsAll(terminal.phases.singlePhases()) }
                        ?.any { other -> hasDirectionOf(FeederDirection.DOWNSTREAM, other, phaseSelector) }
                    ?: false
            }
    }

    private fun hasDirectionOf(expectedDirection: FeederDirection, terminal: Terminal, phaseSelector: PhaseSelector): Boolean {
        return terminal.phases.singlePhases()
            .any {
                phaseSelector.status(terminal, it).direction.has(expectedDirection)
            }
    }

    private fun setMissingToNominal(breakers: List<Breaker>, terminals: List<Terminal>, phaseSelector: PhaseSelector): Boolean {
        val terminalsToTrace = mutableSetOf<Terminal>()
        terminals.forEach { terminal ->
            terminal.phases.singlePhases().forEach { nominalPhase ->
                val status = phaseSelector.status(terminal, nominalPhase)
                if (status.direction === FeederDirection.NONE) {
                    if (nominalPhase != SinglePhaseKind.X && nominalPhase != SinglePhaseKind.Y) {
                        status.add(nominalPhase, FeederDirection.UPSTREAM)
                        terminalsToTrace.add(terminal)
                    }
                }
            }
        }

        terminalsToTrace.forEach { terminal ->
            terminal.conductingEquipment?.let {
                Tracing.setPhases().run(it, breakers)
                tracking.putIfAbsent(it, false)
            }
        }

        return terminalsToTrace.isNotEmpty()
    }

    private fun inferPhases(breakers: Collection<Breaker>, terminal: Terminal, phaseSelector: PhaseSelector, maxMissingPhases: Int) {
        val none = mutableListOf<PhaseStatus>()
        val usedPhases = mutableSetOf<SinglePhaseKind>()

        val conductingEquipment = terminal.conductingEquipment ?: return

        terminal.phases.singlePhases().forEach {
            val status = phaseSelector.status(terminal, it)
            if (status.direction === FeederDirection.NONE)
                none.add(status)
            else
                usedPhases.add(status.phase)
        }

        if (none.isEmpty() || (none.size >= maxMissingPhases))
            return

        tracking[conductingEquipment] = true

        for (status in none) {
            if (!usedPhases.contains(SinglePhaseKind.A))
                status.add(SinglePhaseKind.A, FeederDirection.UPSTREAM)
            else if (!usedPhases.contains(SinglePhaseKind.B))
                status.add(SinglePhaseKind.B, FeederDirection.UPSTREAM)
            else if (!usedPhases.contains(SinglePhaseKind.C))
                status.add(SinglePhaseKind.C, FeederDirection.UPSTREAM)
            else
                status.add(SinglePhaseKind.N, FeederDirection.UPSTREAM)

            usedPhases.add(status.phase)
        }

        Tracing.setPhases().run(conductingEquipment, breakers)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PhaseInferrer::class.java)
    }

}

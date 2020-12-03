/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkService.Companion.connectedTerminals
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

        terminals = setMissingToNominal(breakers, terminals, phaseSelector)

        var previousCount = 0
        while (previousCount != terminals.size) {
            previousCount = terminals.size
            terminals.forEach { inferPhases(breakers, it, phaseSelector, 1) }
            terminals = findTerminalAtStartOfMissingPhases(terminals, phaseSelector)
        }

        terminals.forEach { inferPhases(breakers, it, phaseSelector, 4) }
    }

    private fun findTerminalAtStartOfMissingPhases(terminals: Collection<Terminal>, phaseSelector: PhaseSelector): List<Terminal> {
        return terminals
            .asSequence()
            .filter { isStartOfMissingPhases(it, phaseSelector) }
            .toList()
    }

    private fun isStartOfMissingPhases(terminal: Terminal, phaseSelector: PhaseSelector): Boolean {
        var numNone = 0
        var numIn = 0

        for (phase in terminal.phases.singlePhases()) {
            val direction = phaseSelector.status(terminal, phase).direction()
            if (direction === PhaseDirection.NONE) ++numNone else if (direction.has(PhaseDirection.IN)) ++numIn
        }

        return (numNone > 0)
            && (numNone < terminal.phases.singlePhases().size)
            && (numIn > 0)
            && hasMorePhasesThanConnected(terminal)
    }

    private fun hasMorePhasesThanConnected(terminal: Terminal): Boolean = connectedTerminals(terminal)
        .any { it.toTerminal.phases.singlePhases().size < terminal.phases.singlePhases().size }

    private fun setMissingToNominal(breakers: List<Breaker>, terminals: List<Terminal>, phaseSelector: PhaseSelector): MutableList<Terminal> {
        val xyTerminals = mutableListOf<Terminal>()
        terminals.forEach { terminal ->
            var runSetPhases = false
            var hasXY = false
            terminal.phases.singlePhases().forEach { nominalPhase ->
                val status = phaseSelector.status(terminal, nominalPhase)
                if (status.direction() === PhaseDirection.NONE) {
                    if (nominalPhase != SinglePhaseKind.X || nominalPhase != SinglePhaseKind.Y) {
                        status.add(nominalPhase, PhaseDirection.IN)
                        runSetPhases = true
                    } else
                        hasXY = true
                }
            }

            if (runSetPhases) {
                terminal.conductingEquipment?.let {
                    Tracing.setPhases().run(it, breakers)
                    tracking.putIfAbsent(it, false)
                }
            }

            if (hasXY)
                xyTerminals.add(terminal)
        }

        return xyTerminals
    }

    private fun inferPhases(breakers: Collection<Breaker>, terminal: Terminal, phaseSelector: PhaseSelector, maxMissingPhases: Int) {
        val none = mutableListOf<PhaseStatus>()
        val usedPhases = mutableSetOf<SinglePhaseKind>()

        val conductingEquipment = terminal.conductingEquipment ?: return

        terminal.phases.singlePhases().forEach {
            val status = phaseSelector.status(terminal, it)
            if (status.direction() === PhaseDirection.NONE)
                none.add(status)
            else
                usedPhases.add(status.phase())
        }

        if (none.isEmpty() || (none.size >= maxMissingPhases))
            return

        tracking[conductingEquipment] = true

        for (status in none) {
            if (!usedPhases.contains(SinglePhaseKind.A))
                status.add(SinglePhaseKind.A, PhaseDirection.IN)
            else if (!usedPhases.contains(SinglePhaseKind.B))
                status.add(SinglePhaseKind.B, PhaseDirection.IN)
            else if (!usedPhases.contains(SinglePhaseKind.C))
                status.add(SinglePhaseKind.C, PhaseDirection.IN)
            else
                status.add(SinglePhaseKind.N, PhaseDirection.IN)

            usedPhases.add(status.phase())
        }

        Tracing.setPhases().run(conductingEquipment, breakers)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PhaseInferrer::class.java)
    }

}

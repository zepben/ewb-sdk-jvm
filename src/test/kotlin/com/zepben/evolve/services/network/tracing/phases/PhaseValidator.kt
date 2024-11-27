/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

object PhaseValidator {

    fun validatePhases(network: NetworkService, id: String, expectedPhases: PhaseCode, vararg otherExpectedPhases: PhaseCode) {
        validatePhases(network, id, expectedPhases.singlePhases, *otherExpectedPhases.map { it.singlePhases }.toTypedArray())
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhases: List<SinglePhaseKind>, vararg otherExpectedPhases: List<SinglePhaseKind>) {
        when (val io: IdentifiedObject = network[id]!!) {
            is Terminal -> validatePhases(io, expectedPhases, otherExpectedPhases.firstOrNull() ?: expectedPhases)
            is ConductingEquipment -> {
                val checkPhases = if (io.numTerminals() > 1 && otherExpectedPhases.isEmpty()) {
                    listOf(expectedPhases, expectedPhases)
                } else
                    listOf(expectedPhases, *otherExpectedPhases)

                assertThat(io.numTerminals(), equalTo(checkPhases.size))

                checkPhases.forEachIndexed { index, phases ->
                    validatePhases(io.getTerminal(index + 1), phases)
                }
            }

            else -> throw IllegalArgumentException()
        }
    }

    fun validatePhases(terminal: Terminal?, expectedPhases: List<SinglePhaseKind>) {
        validatePhases(terminal, expectedPhases, expectedPhases)
    }

    fun validatePhases(terminal: Terminal?, expectedPhasesNormal: PhaseCode, expectedPhasesCurrent: PhaseCode) {
        validatePhases(terminal, expectedPhasesNormal.singlePhases, expectedPhasesCurrent.singlePhases)
    }

    fun validatePhases(terminal: Terminal?, expectedPhasesNormal: List<SinglePhaseKind>, expectedPhasesCurrent: List<SinglePhaseKind>) {
        if (terminal == null)
            return

        doPhaseValidation(terminal, terminal.normalPhases, expectedPhasesNormal)
        doPhaseValidation(terminal, terminal.currentPhases, expectedPhasesCurrent)
    }

    private fun doPhaseValidation(terminal: Terminal, phaseStatus: PhaseStatus, expectedPhases: List<SinglePhaseKind>) {
        if ((expectedPhases.size == 1) && (expectedPhases[0] == SinglePhaseKind.NONE)) {
            terminal.phases.singlePhases.forEach { nominalPhase ->
                assertThat("nominal phase $nominalPhase", phaseStatus[nominalPhase], equalTo(SinglePhaseKind.NONE))
            }
        } else {
            assertThat(terminal.phases.numPhases(), equalTo(expectedPhases.size))

            terminal.phases.singlePhases.forEachIndexed { index, nominalPhase ->
                assertThat("nominal phase $nominalPhase", phaseStatus[nominalPhase], equalTo(expectedPhases[index]))
            }
        }
    }

}

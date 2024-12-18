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
import org.hamcrest.Matchers.nullValue

object PhaseValidator {

    fun validatePhases(network: NetworkService, id: String, phaseCode: PhaseCode) {
        validatePhases(network, id, phaseCode.singlePhases)
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhases: List<SinglePhaseKind>) {
        validatePhases(network, id, expectedPhases.toList(), expectedPhases.toList())
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhases1: PhaseCode, expectedPhases2: PhaseCode) {
        validatePhases(network, id, expectedPhases1.singlePhases, expectedPhases2.singlePhases)
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhases1: List<SinglePhaseKind>, expectedPhases2: List<SinglePhaseKind>?) {
        when (val io: IdentifiedObject = network[id]!!) {
            is Terminal -> validatePhases(io, expectedPhases1, expectedPhases2 ?: expectedPhases1)
            is ConductingEquipment -> {
                validatePhases(io.getTerminal(1), expectedPhases1)

                if (expectedPhases2 != null)
                    validatePhases(io.getTerminal(2), expectedPhases2)
                else
                    assertThat(io.getTerminal(2), nullValue())
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

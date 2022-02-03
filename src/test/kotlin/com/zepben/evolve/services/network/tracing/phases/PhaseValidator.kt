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
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

object PhaseValidator {

    fun validatePhases(network: NetworkService, id: String, phaseCode: PhaseCode) {
        validatePhases(network, id, *phaseCode.singlePhases().toTypedArray())
    }

    fun validatePhases(network: NetworkService, id: String, vararg expectedPhases: SinglePhaseKind) {
        validatePhases(network, id, expectedPhases.toList(), expectedPhases.toList())
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhasesT1: PhaseCode, expectedPhasesT2: PhaseCode) {
        validatePhases(network, id, expectedPhasesT1.singlePhases(), expectedPhasesT2.singlePhases())
    }

    fun validatePhases(network: NetworkService, id: String, expectedPhasesT1: List<SinglePhaseKind>, expectedPhasesT2: List<SinglePhaseKind>) {
        val asset = network.get<ConductingEquipment>(id)!!
        validatePhases(asset.getTerminal(1), expectedPhasesT1)
        validatePhases(asset.getTerminal(2), expectedPhasesT2)
    }

    fun validatePhases(terminal: Terminal?, expectedPhases: List<SinglePhaseKind>) {
        if (terminal == null)
            return

        if ((expectedPhases.size == 1) && (expectedPhases[0] == SinglePhaseKind.NONE)) {
            terminal.phases.singlePhases().forEach {
                assertThat(terminal.normalPhases(it).phase, equalTo(SinglePhaseKind.NONE))
            }
        } else {
            assertThat(terminal.phases.numPhases(), equalTo(expectedPhases.size))

            for (index in expectedPhases.indices) {
                val nominalPhase = terminal.phases.singlePhases()[index]
                assertThat(terminal.normalPhases(nominalPhase).phase, equalTo(expectedPhases[index]))
            }
        }
    }

    fun validatePhaseDirections(
        network: NetworkService,
        id: String,
        expectedDirectionT1: List<FeederDirection>,
        expectedDirectionT2: List<FeederDirection> = listOf()
    ) {
        val asset = network.get<ConductingEquipment>(id)!!
        val t1 = if (expectedDirectionT1.isNotEmpty()) asset.terminals[0] else null
        val t2 = if (expectedDirectionT2.isNotEmpty()) asset.terminals[1] else null

        if (t1 != null) {
            for (index in expectedDirectionT1.indices) {
                val nominalPhase = t1.phases.singlePhases()[index]
                assertThat(t1.normalPhases(nominalPhase).direction, equalTo(expectedDirectionT1[index]))
            }
        }

        if (t2 != null) {
            for (index in expectedDirectionT2.indices) {
                val nominalPhase = t2.phases.singlePhases()[index]
                assertThat(t2.normalPhases(nominalPhase).direction, equalTo(expectedDirectionT2[index]))
            }
        }

    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.PhaseDirection
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TerminalTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Terminal().mRID, not(equalTo("")))
        assertThat(Terminal(mRID = "id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val terminal = Terminal()
        val conductingEquipment = object : ConductingEquipment() {}

        assertThat(terminal.conductingEquipment, nullValue())
        assertThat(terminal.phases, equalTo(PhaseCode.ABC))
        assertThat(terminal.tracedPhases, notNullValue())

        terminal.apply {
            this.conductingEquipment = conductingEquipment
            phases = PhaseCode.AB
        }

        assertThat(terminal.conductingEquipment, equalTo(conductingEquipment))
        assertThat(terminal.phases, equalTo(PhaseCode.AB))
    }

    @Test
    internal fun connectivity() {
        val terminal = Terminal()
        val connectivityNode = ConnectivityNode()

        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.connectivityNodeId(), nullValue())
        assertThat(terminal.isConnected(), equalTo(false))

        terminal.connect(connectivityNode)

        assertThat(terminal.connectivityNode, equalTo(connectivityNode))
        assertThat(terminal.connectivityNodeId(), equalTo(connectivityNode.mRID))
        assertThat(terminal.isConnected(), equalTo(true))

        terminal.disconnect()

        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.connectivityNodeId(), nullValue())
        assertThat(terminal.isConnected(), equalTo(false))
    }

    @Test
    internal fun tracedPhases() {
        val terminal = Terminal()

        validatePhases(terminal, SinglePhaseKind.NONE, PhaseDirection.NONE, SinglePhaseKind.NONE, PhaseDirection.NONE)

        terminal.tracedPhases.setNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.A)
        validatePhases(terminal, SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.NONE, PhaseDirection.NONE)

        terminal.tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A)
        validatePhases(terminal, SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.A, PhaseDirection.IN)
    }

    private fun validatePhases(
        terminal: Terminal,
        normalPhase: SinglePhaseKind,
        normalDirection: PhaseDirection,
        currentPhase: SinglePhaseKind,
        currentDirection: PhaseDirection
    ) {
        assertThat(terminal.normalPhases(SinglePhaseKind.A).phase(), equalTo(normalPhase))
        assertThat(terminal.normalPhases(SinglePhaseKind.A).direction(), equalTo(normalDirection))
        assertThat(terminal.currentPhases(SinglePhaseKind.A).phase(), equalTo(currentPhase))
        assertThat(terminal.currentPhases(SinglePhaseKind.A).direction(), equalTo(currentDirection))
    }
}

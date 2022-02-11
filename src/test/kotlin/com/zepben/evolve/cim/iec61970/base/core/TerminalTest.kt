/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.testutils.junit.SystemLogExtension
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

        assertThat(terminal.conductingEquipment, nullValue())
        assertThat(terminal.phases, equalTo(PhaseCode.ABC))
        assertThat(terminal.sequenceNumber, equalTo(0))
        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.tracedPhases.phaseStatusInternal, equalTo(0u))
        assertThat(terminal.normalFeederDirection, equalTo(FeederDirection.NONE))
        assertThat(terminal.currentFeederDirection, equalTo(FeederDirection.NONE))

        terminal.fillFields(NetworkService())

        assertThat(terminal.conductingEquipment, instanceOf(Junction::class.java))
        assertThat(terminal.phases, equalTo(PhaseCode.X))
        assertThat(terminal.sequenceNumber, equalTo(1))
        assertThat(terminal.connectivityNode, notNullValue())
        assertThat(terminal.tracedPhases.phaseStatusInternal, equalTo(2u))
        assertThat(terminal.normalFeederDirection, equalTo(FeederDirection.UPSTREAM))
        assertThat(terminal.currentFeederDirection, equalTo(FeederDirection.DOWNSTREAM))
    }

    @Test
    internal fun connectivity() {
        val terminal = Terminal()
        val connectivityNode = ConnectivityNode()

        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.connectivityNodeId, nullValue())
        assertThat(terminal.isConnected, equalTo(false))

        terminal.connect(connectivityNode)

        assertThat(terminal.connectivityNode, equalTo(connectivityNode))
        assertThat(terminal.connectivityNodeId, equalTo(connectivityNode.mRID))
        assertThat(terminal.isConnected, equalTo(true))

        terminal.disconnect()

        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.connectivityNodeId, nullValue())
        assertThat(terminal.isConnected, equalTo(false))
    }

    @Test
    internal fun tracedPhases() {
        val terminal = Terminal()

        validatePhases(terminal, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        terminal.tracedPhases.setNormal(SinglePhaseKind.A, SinglePhaseKind.B)
        validatePhases(terminal, SinglePhaseKind.B, SinglePhaseKind.NONE)

        terminal.tracedPhases.setCurrent(SinglePhaseKind.A, SinglePhaseKind.A)
        validatePhases(terminal, SinglePhaseKind.B, SinglePhaseKind.A)
    }

    @Test
    internal fun tracedPhasesOperators() {
        val terminal = Terminal()

        validatePhases(terminal, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        terminal.tracedPhases.normal[SinglePhaseKind.A] = SinglePhaseKind.B
        validatePhases(terminal, SinglePhaseKind.B, SinglePhaseKind.NONE)

        terminal.tracedPhases.current[SinglePhaseKind.A] = SinglePhaseKind.A
        validatePhases(terminal, SinglePhaseKind.B, SinglePhaseKind.A)
    }

    private fun validatePhases(
        terminal: Terminal,
        normalPhase: SinglePhaseKind,
        currentPhase: SinglePhaseKind,
    ) {
        assertThat(terminal.normalPhases[SinglePhaseKind.A], equalTo(normalPhase))
        assertThat(terminal.tracedPhases.normal[SinglePhaseKind.A], equalTo(normalPhase))
        assertThat(terminal.tracedPhases.normal(SinglePhaseKind.A), equalTo(normalPhase))

        assertThat(terminal.currentPhases[SinglePhaseKind.A], equalTo(currentPhase))
        assertThat(terminal.tracedPhases.current[SinglePhaseKind.A], equalTo(currentPhase))
        assertThat(terminal.tracedPhases.current(SinglePhaseKind.A), equalTo(currentPhase))
    }

}

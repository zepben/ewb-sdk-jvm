/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TerminalTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Terminal(mRID = "id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val terminal = Terminal(generateId())

        assertThat(terminal.conductingEquipment, nullValue())
        assertThat(terminal.phases, equalTo(PhaseCode.ABC))
        assertThat(terminal.sequenceNumber, equalTo(0))
        assertThat(terminal.connectivityNode, nullValue())
        assertThat(terminal.normalPhases.phaseStatusInternal, equalTo(0u))
        assertThat(terminal.currentPhases.phaseStatusInternal, equalTo(0u))
        assertThat(terminal.normalFeederDirection, equalTo(FeederDirection.NONE))
        assertThat(terminal.currentFeederDirection, equalTo(FeederDirection.NONE))

        terminal.fillFields(NetworkService())

        assertThat(terminal.conductingEquipment, instanceOf(Junction::class.java))
        assertThat(terminal.phases, equalTo(PhaseCode.X))
        assertThat(terminal.sequenceNumber, equalTo(1))
        assertThat(terminal.connectivityNode, notNullValue())
        assertThat(terminal.normalPhases.phaseStatusInternal, equalTo(1u))
        assertThat(terminal.currentPhases.phaseStatusInternal, equalTo(2u))
        assertThat(terminal.normalFeederDirection, equalTo(FeederDirection.UPSTREAM))
        assertThat(terminal.currentFeederDirection, equalTo(FeederDirection.DOWNSTREAM))
    }

    @Test
    internal fun connectivity() {
        val terminal = Terminal(generateId())
        val connectivityNode = ConnectivityNode(generateId())

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
    internal fun connectedTerminals() {
        val terminal1 = Terminal(generateId())
        val terminal2 = Terminal(generateId())
        val terminal3 = Terminal(generateId())
        val networkService = NetworkService()

        assertThat(terminal1.connectedTerminals().toList(), empty())

        networkService.connect(terminal1, "cn1")
        assertThat(terminal1.connectedTerminals().toList(), empty())

        networkService.connect(terminal2, "cn1")
        assertThat(terminal1.connectedTerminals().toList(), containsInAnyOrder(terminal2))

        networkService.connect(terminal3, "cn1")
        assertThat(terminal1.connectedTerminals().toList(), containsInAnyOrder(terminal2, terminal3))
    }

    @Test
    internal fun otherTerminals() {
        val terminal1 = Terminal(generateId())
        val terminal2 = Terminal(generateId())
        val terminal3 = Terminal(generateId())
        val ce = Junction(generateId())

        assertThat(terminal1.otherTerminals().toList(), empty())

        ce.addTerminal(terminal1)
        assertThat(terminal1.otherTerminals().toList(), empty())

        ce.addTerminal(terminal2)
        assertThat(terminal1.otherTerminals().toList(), containsInAnyOrder(terminal2))

        ce.addTerminal(terminal3)
        assertThat(terminal1.otherTerminals().toList(), containsInAnyOrder(terminal2, terminal3))
    }

    @Test
    internal fun normalAndCurrentPhasesAreDifferentStatuses() {
        val terminal = Terminal(generateId())
        assertThat(terminal.normalPhases, not(sameInstance(terminal.currentPhases)))
    }

}

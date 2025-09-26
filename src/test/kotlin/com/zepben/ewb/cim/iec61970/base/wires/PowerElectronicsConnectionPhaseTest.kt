/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsConnectionPhaseTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerElectronicsConnectionPhase().mRID, not(equalTo("")))
        assertThat(PowerElectronicsConnectionPhase("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsConnectionsPhase = PowerElectronicsConnectionPhase()
        val powerElectronicsConnection = PowerElectronicsConnection()

        assertThat(powerElectronicsConnectionsPhase.powerElectronicsConnection, nullValue())
        assertThat(powerElectronicsConnectionsPhase.p, nullValue())
        assertThat(powerElectronicsConnectionsPhase.phase, equalTo(SinglePhaseKind.X))
        assertThat(powerElectronicsConnectionsPhase.q, nullValue())

        powerElectronicsConnectionsPhase.apply {
            this.powerElectronicsConnection = powerElectronicsConnection
            p = 1.0
            phase = SinglePhaseKind.B
            q = 2.0
        }

        assertThat(powerElectronicsConnectionsPhase.powerElectronicsConnection, equalTo(powerElectronicsConnection))
        assertThat(powerElectronicsConnectionsPhase.p, equalTo(1.0))
        assertThat(powerElectronicsConnectionsPhase.phase, equalTo(SinglePhaseKind.B))
        assertThat(powerElectronicsConnectionsPhase.q, equalTo(2.0))
    }

}

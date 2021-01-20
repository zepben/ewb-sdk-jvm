/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsConnectionPhaseTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(PowerElectronicsConnectionPhase().mRID, Matchers.not(Matchers.equalTo("")))
        MatcherAssert.assertThat(PowerElectronicsConnectionPhase("id").mRID, Matchers.equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsConnectionsPhase = PowerElectronicsConnectionPhase()
        val powerElectronicsConnection = PowerElectronicsConnection()

        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.powerElectronicsConnection, Matchers.nullValue())
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.p, Matchers.equalTo(0.0))
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.phase, Matchers.equalTo(SinglePhaseKind.X))
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.q, Matchers.equalTo(0.0))

        powerElectronicsConnectionsPhase.apply {
            this.powerElectronicsConnection = powerElectronicsConnection
            p = 1.0
            phase = SinglePhaseKind.B
            q = 2.0
        }

        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.powerElectronicsConnection, Matchers.equalTo(powerElectronicsConnection))
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.p, Matchers.equalTo(1.0))
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.phase, Matchers.equalTo(SinglePhaseKind.B))
        MatcherAssert.assertThat(powerElectronicsConnectionsPhase.q, Matchers.equalTo(2.0))
    }
}
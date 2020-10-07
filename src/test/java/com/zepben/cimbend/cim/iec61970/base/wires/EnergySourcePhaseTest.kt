/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EnergySourcePhaseTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EnergySourcePhase().mRID, not(equalTo("")))
        assertThat(EnergySourcePhase("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val energySourcePhase = EnergySourcePhase()
        val energySource = EnergySource()

        assertThat(energySourcePhase.energySource, nullValue())
        assertThat(energySourcePhase.phase, equalTo(SinglePhaseKind.X))

        energySourcePhase.apply {
            this.energySource = energySource
            phase = SinglePhaseKind.B
        }

        assertThat(energySourcePhase.energySource, equalTo(energySource))
        assertThat(energySourcePhase.phase, equalTo(SinglePhaseKind.B))
    }
}

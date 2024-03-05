/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EnergyConsumerPhaseTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EnergyConsumerPhase().mRID, not(equalTo("")))
        assertThat(EnergyConsumerPhase("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val energyConsumerPhase = EnergyConsumerPhase()
        val energyConsumer = EnergyConsumer()

        assertThat(energyConsumerPhase.energyConsumer, nullValue())
        assertThat(energyConsumerPhase.phase, equalTo(SinglePhaseKind.X))
        assertThat(energyConsumerPhase.p, nullValue())
        assertThat(energyConsumerPhase.pFixed, nullValue())
        assertThat(energyConsumerPhase.q, nullValue())
        assertThat(energyConsumerPhase.qFixed, nullValue())

        energyConsumerPhase.apply {
            this.energyConsumer = energyConsumer
            phase = SinglePhaseKind.A
            p = 1.2
            pFixed = 3.4
            q = 5.6
            qFixed = 7.8
        }

        assertThat(energyConsumerPhase.energyConsumer, equalTo(energyConsumer))
        assertThat(energyConsumerPhase.phase, equalTo(SinglePhaseKind.A))
        assertThat(energyConsumerPhase.p, equalTo(1.2))
        assertThat(energyConsumerPhase.pFixed, equalTo(3.4))
        assertThat(energyConsumerPhase.q, equalTo(5.6))
        assertThat(energyConsumerPhase.qFixed, equalTo(7.8))
    }
}

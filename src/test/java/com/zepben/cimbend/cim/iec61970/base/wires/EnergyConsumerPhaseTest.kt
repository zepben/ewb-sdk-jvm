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
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.test.util.junit.SystemLogExtension
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
        assertThat(energyConsumerPhase.p, equalTo(0.0))
        assertThat(energyConsumerPhase.pFixed, equalTo(0.0))
        assertThat(energyConsumerPhase.q, equalTo(0.0))
        assertThat(energyConsumerPhase.qFixed, equalTo(0.0))

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

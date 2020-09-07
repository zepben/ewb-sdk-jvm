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

import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EnergyConsumerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(EnergyConsumer().mRID, not(equalTo("")))
        assertThat(EnergyConsumer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val energyConsumer = EnergyConsumer()

        assertThat(energyConsumer.customerCount, equalTo(0))
        assertThat(energyConsumer.grounded, equalTo(false))
        assertThat(energyConsumer.p, equalTo(0.0))
        assertThat(energyConsumer.pFixed, equalTo(0.0))
        assertThat(energyConsumer.phaseConnection, equalTo(PhaseShuntConnectionKind.D))
        assertThat(energyConsumer.q, equalTo(0.0))
        assertThat(energyConsumer.qFixed, equalTo(0.0))

        energyConsumer.customerCount = 1
        energyConsumer.grounded = true
        energyConsumer.p = 2.3
        energyConsumer.pFixed = 4.5
        energyConsumer.phaseConnection = PhaseShuntConnectionKind.Yn
        energyConsumer.q = 6.7
        energyConsumer.qFixed = 8.9

        assertThat(energyConsumer.customerCount, equalTo(1))
        assertThat(energyConsumer.grounded, equalTo(true))
        assertThat(energyConsumer.p, equalTo(2.3))
        assertThat(energyConsumer.pFixed, equalTo(4.5))
        assertThat(energyConsumer.phaseConnection, equalTo(PhaseShuntConnectionKind.Yn))
        assertThat(energyConsumer.q, equalTo(6.7))
        assertThat(energyConsumer.qFixed, equalTo(8.9))
    }

    @Test
    internal fun energyConsumerPhases() {
        PrivateCollectionValidator.validate(
            { EnergyConsumer() },
            { id, _ -> EnergyConsumerPhase(id) },
            EnergyConsumer::numPhases,
            EnergyConsumer::getPhase,
            EnergyConsumer::phases,
            EnergyConsumer::addPhase,
            EnergyConsumer::removePhase,
            EnergyConsumer::clearPhases
        )
    }
}

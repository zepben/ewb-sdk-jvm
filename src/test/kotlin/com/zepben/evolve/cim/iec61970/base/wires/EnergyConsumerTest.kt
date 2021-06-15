/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

        assertThat(energyConsumer.customerCount, nullValue())
        assertThat(energyConsumer.grounded, equalTo(false))
        assertThat(energyConsumer.p, nullValue())
        assertThat(energyConsumer.pFixed, nullValue())
        assertThat(energyConsumer.phaseConnection, equalTo(PhaseShuntConnectionKind.D))
        assertThat(energyConsumer.q, nullValue())
        assertThat(energyConsumer.qFixed, nullValue())

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

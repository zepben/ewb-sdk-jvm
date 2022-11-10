/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsConnectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerElectronicsConnection().mRID, not(equalTo("")))
        assertThat(PowerElectronicsConnection("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsConnection = PowerElectronicsConnection()

        assertThat(powerElectronicsConnection.maxIFault, nullValue())
        assertThat(powerElectronicsConnection.maxQ, nullValue())
        assertThat(powerElectronicsConnection.minQ, nullValue())
        assertThat(powerElectronicsConnection.p, nullValue())
        assertThat(powerElectronicsConnection.q, nullValue())
        assertThat(powerElectronicsConnection.ratedS, nullValue())
        assertThat(powerElectronicsConnection.ratedU, nullValue())

        powerElectronicsConnection.maxIFault = 1
        powerElectronicsConnection.maxQ = 2.0
        powerElectronicsConnection.minQ = 3.0
        powerElectronicsConnection.p = 4.0
        powerElectronicsConnection.q = 5.0
        powerElectronicsConnection.ratedS = 6
        powerElectronicsConnection.ratedU = 7

        assertThat(powerElectronicsConnection.maxIFault, equalTo(1))
        assertThat(powerElectronicsConnection.maxQ, equalTo(2.0))
        assertThat(powerElectronicsConnection.minQ, equalTo(3.0))
        assertThat(powerElectronicsConnection.p, equalTo(4.0))
        assertThat(powerElectronicsConnection.q, equalTo(5.0))
        assertThat(powerElectronicsConnection.ratedS, equalTo(6))
        assertThat(powerElectronicsConnection.ratedU, equalTo(7))
    }

    @Test
    internal fun assignsPowerElectronicsConnectionToPowerElectronicsConnectionPhaseIfMissing() {
        val powerElectronicsConnection = PowerElectronicsConnection()
        val phase = PowerElectronicsConnectionPhase()

        powerElectronicsConnection.addPhase(phase)
        assertThat(phase.powerElectronicsConnection, equalTo(powerElectronicsConnection))
    }

    @Test
    internal fun rejectsPowerElectronicsConnectionPhaseWithWrongPowerElectronicsConnection() {
        val powerElectronicsConnection1 = PowerElectronicsConnection()
        val powerElectronicsConnection2 = PowerElectronicsConnection()
        val phase = PowerElectronicsConnectionPhase().apply { powerElectronicsConnection = powerElectronicsConnection2 }

        ExpectException.expect { powerElectronicsConnection1.addPhase(phase) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${phase.typeNameAndMRID()} `powerElectronicsConnection` property references ${powerElectronicsConnection2.typeNameAndMRID()}, expected ${powerElectronicsConnection1.typeNameAndMRID()}.")
    }

    @Test
    internal fun powerElectronicsConnectionUnits() {
        PrivateCollectionValidator.validate(
            { PowerElectronicsConnection() },
            { id, _ -> object : PowerElectronicsUnit(id) {} },
            PowerElectronicsConnection::numUnits,
            PowerElectronicsConnection::getUnit,
            PowerElectronicsConnection::units,
            PowerElectronicsConnection::addUnit,
            PowerElectronicsConnection::removeUnit,
            PowerElectronicsConnection::clearUnits
        )
    }

    @Test
    internal fun powerElectronicsConnectionPhases() {
        PrivateCollectionValidator.validate(
            { PowerElectronicsConnection() },
            { id, _ -> PowerElectronicsConnectionPhase(id) },
            PowerElectronicsConnection::numPhases,
            PowerElectronicsConnection::getPhase,
            PowerElectronicsConnection::phases,
            PowerElectronicsConnection::addPhase,
            PowerElectronicsConnection::removePhase,
            PowerElectronicsConnection::clearPhases
        )
    }
}

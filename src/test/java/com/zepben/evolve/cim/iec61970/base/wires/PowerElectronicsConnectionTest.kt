/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsConnectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(PowerElectronicsConnection().mRID, Matchers.not(Matchers.equalTo("")))
        MatcherAssert.assertThat(PowerElectronicsConnection("id").mRID, Matchers.equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsConnection = PowerElectronicsConnection()

        MatcherAssert.assertThat(powerElectronicsConnection.maxIFault, Matchers.equalTo(0))
        MatcherAssert.assertThat(powerElectronicsConnection.maxQ, Matchers.equalTo(0.0))
        MatcherAssert.assertThat(powerElectronicsConnection.minQ, Matchers.equalTo(0.0))
        MatcherAssert.assertThat(powerElectronicsConnection.p, Matchers.equalTo(0.0))
        MatcherAssert.assertThat(powerElectronicsConnection.q, Matchers.equalTo(0.0))
        MatcherAssert.assertThat(powerElectronicsConnection.ratedS, Matchers.equalTo(0))
        MatcherAssert.assertThat(powerElectronicsConnection.ratedU, Matchers.equalTo(0))

        powerElectronicsConnection.maxIFault = 1
        powerElectronicsConnection.maxQ = 2.0
        powerElectronicsConnection.minQ = 3.0
        powerElectronicsConnection.p = 4.0
        powerElectronicsConnection.q = 5.0
        powerElectronicsConnection.ratedS = 6
        powerElectronicsConnection.ratedU = 7

        MatcherAssert.assertThat(powerElectronicsConnection.maxIFault, Matchers.equalTo(1))
        MatcherAssert.assertThat(powerElectronicsConnection.maxQ, Matchers.equalTo(2.0))
        MatcherAssert.assertThat(powerElectronicsConnection.minQ, Matchers.equalTo(3.0))
        MatcherAssert.assertThat(powerElectronicsConnection.p, Matchers.equalTo(4.0))
        MatcherAssert.assertThat(powerElectronicsConnection.q, Matchers.equalTo(5.0))
        MatcherAssert.assertThat(powerElectronicsConnection.ratedS, Matchers.equalTo(6))
        MatcherAssert.assertThat(powerElectronicsConnection.ratedU, Matchers.equalTo(7))
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
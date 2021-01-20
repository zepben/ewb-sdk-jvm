/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

import com.zepben.evolve.cim.iec61970.base.wires.PowerElectronicsConnection
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsUnitTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(object : PowerElectronicsUnit() {}.mRID, Matchers.not(Matchers.equalTo("")))
        MatcherAssert.assertThat(object : PowerElectronicsUnit("id") {}.mRID, Matchers.equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsUnit = object : PowerElectronicsUnit() {}
        val powerElectronicsConnection = PowerElectronicsConnection()

        MatcherAssert.assertThat(powerElectronicsUnit.powerElectronicsConnection, Matchers.nullValue())
        MatcherAssert.assertThat(powerElectronicsUnit.maxP, Matchers.equalTo(0))
        MatcherAssert.assertThat(powerElectronicsUnit.minP, Matchers.equalTo(0))

        powerElectronicsUnit.apply {
            this.powerElectronicsConnection = powerElectronicsConnection
            maxP = 1
            minP = 2
        }

        MatcherAssert.assertThat(powerElectronicsUnit.powerElectronicsConnection, Matchers.equalTo(powerElectronicsConnection))
        MatcherAssert.assertThat(powerElectronicsUnit.maxP, Matchers.equalTo(1))
        MatcherAssert.assertThat(powerElectronicsUnit.minP, Matchers.equalTo(2))
    }

    @Test
    internal fun throwsOnReassignment() {
        val powerElectronicsUnit = object : PowerElectronicsUnit() {}
        val powerElectronicsConnection1 = PowerElectronicsConnection()
        val powerElectronicsConnection2 = PowerElectronicsConnection()

        powerElectronicsUnit.apply { this.powerElectronicsConnection = powerElectronicsConnection1 }
        ExpectException.expect { powerElectronicsUnit.powerElectronicsConnection = powerElectronicsConnection2 }
            .toThrow(IllegalStateException::class.java)
    }
}
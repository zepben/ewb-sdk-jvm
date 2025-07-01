/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

import com.zepben.ewb.cim.iec61970.base.wires.PowerElectronicsConnection
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerElectronicsUnitTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : PowerElectronicsUnit() {}.mRID, not(equalTo("")))
        assertThat(object : PowerElectronicsUnit("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerElectronicsUnit = object : PowerElectronicsUnit() {}
        val powerElectronicsConnection = PowerElectronicsConnection()

        assertThat(powerElectronicsUnit.powerElectronicsConnection, nullValue())
        assertThat(powerElectronicsUnit.maxP, nullValue())
        assertThat(powerElectronicsUnit.minP, nullValue())

        powerElectronicsUnit.apply {
            this.powerElectronicsConnection = powerElectronicsConnection
            maxP = 1
            minP = 2
        }

        assertThat(powerElectronicsUnit.powerElectronicsConnection, equalTo(powerElectronicsConnection))
        assertThat(powerElectronicsUnit.maxP, equalTo(1))
        assertThat(powerElectronicsUnit.minP, equalTo(2))
    }

    @Test
    internal fun throwsOnReassignment() {
        val powerElectronicsUnit = object : PowerElectronicsUnit() {}
        val powerElectronicsConnection1 = PowerElectronicsConnection()
        val powerElectronicsConnection2 = PowerElectronicsConnection()

        powerElectronicsUnit.apply { this.powerElectronicsConnection = powerElectronicsConnection1 }
        ExpectException.expect { powerElectronicsUnit.powerElectronicsConnection = powerElectronicsConnection2 }
            .toThrow<IllegalStateException>()
    }
}

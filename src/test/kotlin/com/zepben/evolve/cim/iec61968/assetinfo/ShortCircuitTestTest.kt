/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ShortCircuitTestTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(ShortCircuitTest().mRID, not(equalTo("")))
        assertThat(ShortCircuitTest("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val shortCircuitTest = ShortCircuitTest()

        assertThat(shortCircuitTest.current, nullValue())
        assertThat(shortCircuitTest.energisedEndStep, nullValue())
        assertThat(shortCircuitTest.groundedEndStep, nullValue())
        assertThat(shortCircuitTest.leakageImpedance, nullValue())
        assertThat(shortCircuitTest.leakageImpedanceZero, nullValue())
        assertThat(shortCircuitTest.loss, nullValue())
        assertThat(shortCircuitTest.lossZero, nullValue())
        assertThat(shortCircuitTest.power, nullValue())
        assertThat(shortCircuitTest.voltage, nullValue())
        assertThat(shortCircuitTest.voltageOhmicPart, nullValue())

        shortCircuitTest.fillFields(NetworkService(), true)

        assertThat(shortCircuitTest.current, equalTo(1.1))
        assertThat(shortCircuitTest.energisedEndStep, equalTo(2))
        assertThat(shortCircuitTest.groundedEndStep, equalTo(3))
        assertThat(shortCircuitTest.leakageImpedance, equalTo(4.4))
        assertThat(shortCircuitTest.leakageImpedanceZero, equalTo(5.5))
        assertThat(shortCircuitTest.loss, equalTo(6))
        assertThat(shortCircuitTest.lossZero, equalTo(7))
        assertThat(shortCircuitTest.power, equalTo(8))
        assertThat(shortCircuitTest.voltage, equalTo(9.9))
        assertThat(shortCircuitTest.voltageOhmicPart, equalTo(10.01))
    }

}
    

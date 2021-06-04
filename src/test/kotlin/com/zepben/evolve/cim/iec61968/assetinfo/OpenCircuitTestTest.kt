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
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class OpenCircuitTestTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(OpenCircuitTest().mRID, not(equalTo("")))
        assertThat(OpenCircuitTest("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerTest = OpenCircuitTest()

        assertThat(transformerTest.energisedEndStep, equalTo(0))
        assertThat(transformerTest.energisedEndVoltage, equalTo(0))
        assertThat(transformerTest.openEndStep, equalTo(0))
        assertThat(transformerTest.openEndVoltage, equalTo(0))
        assertThat(transformerTest.phaseShift, equalTo(0.0))

        transformerTest.fillFields(NetworkService(), true)

        assertThat(transformerTest.energisedEndStep, equalTo(1))
        assertThat(transformerTest.energisedEndVoltage, equalTo(2))
        assertThat(transformerTest.openEndStep, equalTo(3))
        assertThat(transformerTest.openEndVoltage, equalTo(4))
        assertThat(transformerTest.phaseShift, equalTo(5.5))
    }

}
    

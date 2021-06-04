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

internal class NoLoadTestTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(NoLoadTest().mRID, not(equalTo("")))
        assertThat(NoLoadTest("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val noLoadTest = NoLoadTest()

        assertThat(noLoadTest.energisedEndVoltage, equalTo(0))
        assertThat(noLoadTest.excitingCurrent, equalTo(0.0))
        assertThat(noLoadTest.excitingCurrentZero, equalTo(0.0))
        assertThat(noLoadTest.loss, equalTo(0))
        assertThat(noLoadTest.lossZero, equalTo(0))

        noLoadTest.fillFields(NetworkService(), true)

        assertThat(noLoadTest.energisedEndVoltage, equalTo(1))
        assertThat(noLoadTest.excitingCurrent, equalTo(2.2))
        assertThat(noLoadTest.excitingCurrentZero, equalTo(3.3))
        assertThat(noLoadTest.loss, equalTo(4))
        assertThat(noLoadTest.lossZero, equalTo(5))
    }

}
    

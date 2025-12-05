/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RotatingMachineTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : RotatingMachine("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val rotatingMachine = object : RotatingMachine(generateId()) {}

        assertThat(rotatingMachine.ratedPowerFactor, equalTo(null))
        assertThat(rotatingMachine.ratedS, equalTo(null))
        assertThat(rotatingMachine.ratedU, equalTo(null))
        assertThat(rotatingMachine.p, equalTo(null))
        assertThat(rotatingMachine.q, equalTo(null))

        rotatingMachine.fillFields(NetworkService())

        assertThat(rotatingMachine.ratedPowerFactor, equalTo(1.1))
        assertThat(rotatingMachine.ratedS, equalTo(2.2))
        assertThat(rotatingMachine.ratedU, equalTo(3))
        assertThat(rotatingMachine.p, equalTo(4.4))
        assertThat(rotatingMachine.q, equalTo(5.5))
    }

}

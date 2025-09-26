/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class StaticVarCompensatorTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(StaticVarCompensator().mRID, not(equalTo("")))
        assertThat(StaticVarCompensator("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val batteryControl = StaticVarCompensator()

        assertThat(batteryControl.capacitiveRating, nullValue())
        assertThat(batteryControl.inductiveRating, nullValue())
        assertThat(batteryControl.q, nullValue())
        assertThat(batteryControl.svcControlMode, equalTo(SVCControlMode.UNKNOWN))
        assertThat(batteryControl.voltageSetPoint, nullValue())

        batteryControl.fillFields(NetworkService())

        assertThat(batteryControl.capacitiveRating, equalTo(1.0))
        assertThat(batteryControl.inductiveRating, equalTo(2.0))
        assertThat(batteryControl.q, equalTo(3.0))
        assertThat(batteryControl.svcControlMode, equalTo(SVCControlMode.reactivePower))
        assertThat(batteryControl.voltageSetPoint, equalTo(4))
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.wires

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BatteryControlTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(BatteryControl().mRID, not(equalTo("")))
        assertThat(BatteryControl("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val batteryControl = BatteryControl()

        assertThat(batteryControl.chargingRate, nullValue())
        assertThat(batteryControl.dischargingRate, nullValue())
        assertThat(batteryControl.reservePercent, nullValue())
        assertThat(batteryControl.controlMode, equalTo(BatteryControlMode.UNKNOWN))

        batteryControl.fillFields(NetworkService())

        assertThat(batteryControl.chargingRate, equalTo(1.0))
        assertThat(batteryControl.dischargingRate, equalTo(2.0))
        assertThat(batteryControl.reservePercent, equalTo(3.0))
        assertThat(batteryControl.controlMode, equalTo(BatteryControlMode.time))
    }

}

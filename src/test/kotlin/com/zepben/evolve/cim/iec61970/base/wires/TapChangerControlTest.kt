/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TapChangerControlTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TapChangerControl().mRID, not(equalTo("")))
        assertThat(TapChangerControl("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val tapChangerControl = TapChangerControl()

        assertThat(tapChangerControl.limitVoltage, nullValue())
        assertThat(tapChangerControl.lineDropCompensation, nullValue())
        assertThat(tapChangerControl.lineDropR, nullValue())
        assertThat(tapChangerControl.lineDropX, nullValue())
        assertThat(tapChangerControl.reverseLineDropR, nullValue())
        assertThat(tapChangerControl.reverseLineDropX, nullValue())
        assertThat(tapChangerControl.forwardLDCBlocking, nullValue())
        assertThat(tapChangerControl.timeDelay, nullValue())
        assertThat(tapChangerControl.coGenerationEnabled, nullValue())

        tapChangerControl.fillFields(NetworkService())

        assertThat(tapChangerControl.limitVoltage, equalTo(1000))
        assertThat(tapChangerControl.lineDropCompensation, equalTo(true))
        assertThat(tapChangerControl.lineDropR, equalTo(10.0))
        assertThat(tapChangerControl.lineDropX, equalTo(4.0))
        assertThat(tapChangerControl.reverseLineDropR, equalTo(1.0))
        assertThat(tapChangerControl.reverseLineDropX, equalTo(1.0))
        assertThat(tapChangerControl.forwardLDCBlocking, equalTo(true))
        assertThat(tapChangerControl.timeDelay, equalTo(5.3))
        assertThat(tapChangerControl.coGenerationEnabled, equalTo(false))
    }

}

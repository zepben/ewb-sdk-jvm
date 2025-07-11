/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.scada

import com.zepben.ewb.cim.iec61970.base.meas.Control
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RemoteControlTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(RemoteControl().mRID, not(equalTo("")))
        assertThat(RemoteControl("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val remoteControl = RemoteControl()
        val control = Control()

        assertThat(remoteControl.control, nullValue())

        remoteControl.control = control

        assertThat(remoteControl.control, equalTo(control))
    }
}

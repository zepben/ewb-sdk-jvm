/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.meas

import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ControlTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Control().mRID, not(equalTo("")))
        assertThat(Control("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val control = Control()
        val remoteControl = RemoteControl()

        assertThat(control.powerSystemResourceMRID, nullValue())
        assertThat(control.remoteControl, nullValue())

        control.powerSystemResourceMRID = "powerSystemResourceMRID"
        control.remoteControl = remoteControl

        assertThat(control.powerSystemResourceMRID, equalTo("powerSystemResourceMRID"))
        assertThat(control.remoteControl, equalTo(remoteControl))
    }
}

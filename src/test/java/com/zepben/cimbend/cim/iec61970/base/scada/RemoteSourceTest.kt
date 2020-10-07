/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.scada

import com.zepben.cimbend.cim.iec61970.base.meas.Measurement
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RemoteSourceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(RemoteSource().mRID, not(equalTo("")))
        assertThat(RemoteSource("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val remoteSource = RemoteSource()
        val measurement = object : Measurement() {}

        assertThat(remoteSource.measurement, nullValue())

        remoteSource.measurement = measurement

        assertThat(remoteSource.measurement, equalTo(measurement))
    }
}

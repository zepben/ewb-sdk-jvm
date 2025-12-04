/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.scada

import com.zepben.ewb.cim.iec61970.base.meas.Measurement
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RemoteSourceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(RemoteSource("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val remoteSource = RemoteSource(generateId())
        val measurement = object : Measurement(generateId()) {}

        assertThat(remoteSource.measurement, nullValue())

        remoteSource.measurement = measurement

        assertThat(remoteSource.measurement, equalTo(measurement))
    }

}

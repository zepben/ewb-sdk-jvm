/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.meas

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class MeasurementValueTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    @Throws(IllegalStateException::class)
    internal fun accessorCoverage() {
        val mv = object : MeasurementValue() {}
        val timeStamp = Instant.now()

        MatcherAssert.assertThat(mv.timeStamp, Matchers.nullValue())

        mv.timeStamp = timeStamp
        MatcherAssert.assertThat(mv.timeStamp, Matchers.`is`(timeStamp))
    }
}
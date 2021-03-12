/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.meas

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AccumulatorValueTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun accessorCoverage() {
        val mv = AccumulatorValue()
        val measValue = 23u
        val measMRID = "measurement-mrid"

        MatcherAssert.assertThat(mv.value, Matchers.`is`(0u))
        MatcherAssert.assertThat(mv.accumulatorMRID, Matchers.nullValue())

        mv.apply {
            value = measValue
            accumulatorMRID = measMRID
        }
        MatcherAssert.assertThat(mv.value, Matchers.`is`(measValue))
        MatcherAssert.assertThat(mv.accumulatorMRID, Matchers.`is`(measMRID))
    }
}

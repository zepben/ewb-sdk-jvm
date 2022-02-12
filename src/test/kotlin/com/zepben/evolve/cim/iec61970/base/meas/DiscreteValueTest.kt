/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.meas

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiscreteValueTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun accessorCoverage() {
        val mv = DiscreteValue()
        val measValue = 23
        val measMRID = "measurement-mrid"

        assertThat(mv.value, equalTo(0))
        assertThat(mv.discreteMRID, nullValue())

        mv.apply {
            value = measValue
            discreteMRID = measMRID
        }
        assertThat(mv.value, equalTo(measValue))
        assertThat(mv.discreteMRID, equalTo(measMRID))
    }
}

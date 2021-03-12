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

internal class AnalogTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(Analog().mRID, Matchers.not(Matchers.equalTo("")))
        MatcherAssert.assertThat(Analog("id").mRID, Matchers.equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val measurement = Analog()
        MatcherAssert.assertThat(measurement.positiveFlowIn, Matchers.`is`(false))

        measurement.positiveFlowIn = true

        MatcherAssert.assertThat(measurement.positiveFlowIn, Matchers.`is`(true))

    }
}

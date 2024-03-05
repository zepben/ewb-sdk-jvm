/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RatioTapChangerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(RatioTapChanger().mRID, not(equalTo("")))
        assertThat(RatioTapChanger("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val ratioTapChanger = RatioTapChanger()
        val transformerEnd = object : TransformerEnd() {}

        assertThat(ratioTapChanger.transformerEnd, nullValue())
        assertThat(ratioTapChanger.stepVoltageIncrement, nullValue())

        ratioTapChanger.transformerEnd = transformerEnd
        ratioTapChanger.stepVoltageIncrement = 1.23

        assertThat(ratioTapChanger.transformerEnd, equalTo(transformerEnd))
        assertThat(ratioTapChanger.stepVoltageIncrement, equalTo(1.23))
    }
}

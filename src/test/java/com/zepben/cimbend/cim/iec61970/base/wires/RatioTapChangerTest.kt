/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.test.util.junit.SystemLogExtension
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
        assertThat(ratioTapChanger.stepVoltageIncrement, equalTo(0.0))

        ratioTapChanger.transformerEnd = transformerEnd
        ratioTapChanger.stepVoltageIncrement = 1.23

        assertThat(ratioTapChanger.transformerEnd, equalTo(transformerEnd))
        assertThat(ratioTapChanger.stepVoltageIncrement, equalTo(1.23))
    }
}

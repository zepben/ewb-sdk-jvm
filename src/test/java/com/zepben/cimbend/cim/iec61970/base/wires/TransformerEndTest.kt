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

import com.zepben.cimbend.cim.iec61970.base.core.BaseVoltage
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TransformerEndTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : TransformerEnd() {}.mRID, not(equalTo("")))
        assertThat(object : TransformerEnd("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerEnd = object : TransformerEnd() {}
        val baseVoltage = BaseVoltage()
        val ratioTapChanger = RatioTapChanger()
        val terminal = Terminal()

        assertThat(transformerEnd.grounded, equalTo(false))
        assertThat(transformerEnd.rGround, equalTo(0.0))
        assertThat(transformerEnd.xGround, equalTo(0.0))
        assertThat(transformerEnd.baseVoltage, equalTo(null))
        assertThat(transformerEnd.ratioTapChanger, equalTo(null))
        assertThat(transformerEnd.terminal, equalTo(null))

        transformerEnd.grounded = true
        transformerEnd.rGround = 1.2
        transformerEnd.xGround = 3.4
        transformerEnd.baseVoltage = baseVoltage
        transformerEnd.ratioTapChanger = ratioTapChanger
        transformerEnd.terminal = terminal

        assertThat(transformerEnd.grounded, equalTo(true))
        assertThat(transformerEnd.rGround, equalTo(1.2))
        assertThat(transformerEnd.xGround, equalTo(3.4))
        assertThat(transformerEnd.baseVoltage, equalTo(baseVoltage))
        assertThat(transformerEnd.ratioTapChanger, equalTo(ratioTapChanger))
        assertThat(transformerEnd.terminal, equalTo(terminal))
    }

}

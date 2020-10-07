/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61970.base.core.BaseVoltage
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.testutils.junit.SystemLogExtension
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

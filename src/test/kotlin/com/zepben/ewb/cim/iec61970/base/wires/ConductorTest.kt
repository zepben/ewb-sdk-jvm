/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.CableInfo
import com.zepben.ewb.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConductorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Conductor() {}.mRID, not(equalTo("")))
        assertThat(object : Conductor("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val conductor = object : Conductor() {}
        val wireInfo = object : WireInfo() {}

        assertThat(conductor.assetInfo, nullValue())
        assertThat(conductor.length, nullValue())
        assertThat(conductor.designTemperature, nullValue())
        assertThat(conductor.designRating, nullValue())

        conductor.assetInfo = wireInfo
        conductor.length = 12.3
        conductor.designTemperature = 45
        conductor.designRating = 67.8

        assertThat(conductor.assetInfo, equalTo(wireInfo))
        assertThat(conductor.length, equalTo(12.3))
        assertThat(conductor.designTemperature, equalTo(45))
        assertThat(conductor.designRating, equalTo(67.8))
    }

    @Test
    internal fun validatesLength() {
        val conductor = object : Conductor() {}
        conductor.length = 1.0
        conductor.length = 0.0
        conductor.length = Double.NaN
        expect { conductor.length = -1.0 }
            .toThrow<IllegalArgumentException>()
            .withMessage("Conductor length cannot be negative.")
    }

    @Test
    internal fun undergroundVsOverhead() {
        val ug = CableInfo()
        val oh = OverheadWireInfo()
        val conductor = object : Conductor() {}

        assertThat(conductor.isUnderground, equalTo(false))

        conductor.assetInfo = ug
        assertThat(conductor.isUnderground, equalTo(true))

        conductor.assetInfo = oh
        assertThat(conductor.isUnderground, equalTo(false))
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Assert
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

        assertThat(conductor.assetInfo, Matchers.nullValue())
        assertThat(conductor.length, equalTo(0.0))

        conductor.assetInfo = wireInfo
        conductor.length = 12.3

        assertThat(conductor.assetInfo, equalTo(wireInfo))
        assertThat(conductor.length, equalTo(12.3))
    }

    @Test
    internal fun validatesLength() {
        val conductor = object : Conductor() {}
        conductor.length = 1.0
        conductor.length = 0.0
        conductor.length = Double.NaN
        expect { conductor.length = -1.0 }
            .toThrow(IllegalArgumentException::class.java)
            .withMessage("Conductor length cannot be negative.")
    }

    @Test
    internal fun undergroundVsOverhead() {
        val ug = CableInfo()
        val oh = OverheadWireInfo()
        val conductor = object : Conductor() {}

        Assert.assertThat(conductor.isUnderground, equalTo(false))

        conductor.assetInfo = ug
        Assert.assertThat(conductor.isUnderground, equalTo(true))

        conductor.assetInfo = oh
        Assert.assertThat(conductor.isUnderground, equalTo(false))
    }
}

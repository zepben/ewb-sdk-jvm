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

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.WireInfo
import com.zepben.test.util.ExpectException.expect
import com.zepben.test.util.junit.SystemLogExtension
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

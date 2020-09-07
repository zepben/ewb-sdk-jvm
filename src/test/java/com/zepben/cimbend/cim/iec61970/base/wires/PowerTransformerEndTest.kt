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

internal class PowerTransformerEndTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerTransformerEnd().mRID, not(equalTo("")))
        assertThat(PowerTransformerEnd("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerTransformerEnd = PowerTransformerEnd()
        val powerTransformer = PowerTransformer()

        assertThat(powerTransformerEnd.powerTransformer, nullValue())
        assertThat(powerTransformerEnd.b, equalTo(0.0))
        assertThat(powerTransformerEnd.b0, equalTo(0.0))
        assertThat(powerTransformerEnd.connectionKind, equalTo(WindingConnection.UNKNOWN_WINDING))
        assertThat(powerTransformerEnd.g, equalTo(0.0))
        assertThat(powerTransformerEnd.g0, equalTo(0.0))
        assertThat(powerTransformerEnd.phaseAngleClock, equalTo(0))
        assertThat(powerTransformerEnd.r, equalTo(0.0))
        assertThat(powerTransformerEnd.r0, equalTo(0.0))
        assertThat(powerTransformerEnd.ratedS, equalTo(0))
        assertThat(powerTransformerEnd.ratedU, equalTo(0))
        assertThat(powerTransformerEnd.x, equalTo(0.0))
        assertThat(powerTransformerEnd.x0, equalTo(0.0))

        powerTransformerEnd.apply {
            this.powerTransformer = powerTransformer
            b = 1.0
            b0 = 2.0
            connectionKind = WindingConnection.Zn
            g = 3.0
            g0 = 4.0
            phaseAngleClock = 5
            r = 6.0
            r0 = 7.0
            ratedS = 8
            ratedU = 9
            x = 10.0
            x0 = 11.0
        }

        assertThat(powerTransformerEnd.powerTransformer, equalTo(powerTransformer))
        assertThat(powerTransformerEnd.b, equalTo(1.0))
        assertThat(powerTransformerEnd.b0, equalTo(2.0))
        assertThat(powerTransformerEnd.connectionKind, equalTo(WindingConnection.Zn))
        assertThat(powerTransformerEnd.g, equalTo(3.0))
        assertThat(powerTransformerEnd.g0, equalTo(4.0))
        assertThat(powerTransformerEnd.phaseAngleClock, equalTo(5))
        assertThat(powerTransformerEnd.r, equalTo(6.0))
        assertThat(powerTransformerEnd.r0, equalTo(7.0))
        assertThat(powerTransformerEnd.ratedS, equalTo(8))
        assertThat(powerTransformerEnd.ratedU, equalTo(9))
        assertThat(powerTransformerEnd.x, equalTo(10.0))
        assertThat(powerTransformerEnd.x0, equalTo(11.0))
    }
}

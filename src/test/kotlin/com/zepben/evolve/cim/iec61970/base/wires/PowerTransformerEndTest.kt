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

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.common

import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PositionPointTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PositionPoint(0.0, 0.0), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val (xPosition, yPosition) = PositionPoint(34.5, 67.8)

        assertThat(xPosition, equalTo(34.5))
        assertThat(yPosition, equalTo(67.8))
    }

    @Test
    internal fun equals() {
        val point1 = PositionPoint(0.0, 0.0)
        val point2 = PositionPoint(0.0, 1.0)
        val point3 = PositionPoint(1.0, 0.0)
        val point1Dup = PositionPoint(0.0, 0.0)
        val point2Dup = PositionPoint(0.0, 1.0)
        val point3Dup = PositionPoint(1.0, 0.0)

        assertThat(point1, equalTo(point1Dup))
        assertThat(point2, equalTo(point2Dup))
        assertThat(point3, equalTo(point3Dup))
        assertThat(point1, not(equalTo(point2)))
        assertThat(point1, not(equalTo(point3)))
        assertThat(point2, not(equalTo(point3)))
    }

    @Test
    internal fun detectsInvalid() {
        validateErrors(-181.0, 0.0, "Longitude is out of range. Expected -180 to 180")
        validateErrors(181.0, 0.0, "Longitude is out of range. Expected -180 to 180")
        validateErrors(0.0, -91.0, "Latitude is out of range. Expected -90 to 90")
        validateErrors(0.0, 91.0, "Latitude is out of range. Expected -90 to 90")
    }

    private fun validateErrors(longitude: Double, latitude: Double, error: String) {
        expect { PositionPoint(longitude, latitude) }
            .toThrow(IllegalArgumentException::class.java)
            .exception().also {
                assertThat(it.message, containsString(error))
            }
    }
}
    

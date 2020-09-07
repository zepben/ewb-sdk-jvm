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
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.test.util.ExpectException.expect
import com.zepben.test.util.junit.SystemLogExtension
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
    

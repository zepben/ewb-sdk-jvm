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
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class StreetlightTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Streetlight().mRID, not(equalTo("")))
        assertThat(Streetlight("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val streetlight = Streetlight()
        val pole = Pole("p")

        assertThat(streetlight.pole, Matchers.nullValue())
        assertThat(streetlight.lampKind, equalTo(StreetlightLampKind.UNKNOWN))
        assertThat(streetlight.lightRating, equalTo(0))

        streetlight.apply {
            lampKind = StreetlightLampKind.HIGH_PRESSURE_SODIUM
            lightRating = 1
            this.pole = pole
        }

        assertThat(streetlight.pole, equalTo(pole))
        assertThat(streetlight.lampKind, equalTo(StreetlightLampKind.HIGH_PRESSURE_SODIUM))
        assertThat(streetlight.lightRating, equalTo(1))
    }
}

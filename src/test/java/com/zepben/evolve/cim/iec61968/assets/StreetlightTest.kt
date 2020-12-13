/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.assets

import com.zepben.testutils.junit.SystemLogExtension
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

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assets

import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.StreetlightLampKind
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class StreetlightTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Streetlight("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val streetlight = Streetlight(generateId())
        val pole = Pole("p")

        assertThat(streetlight.pole, nullValue())
        assertThat(streetlight.lampKind, equalTo(StreetlightLampKind.UNKNOWN))
        assertThat(streetlight.lightRating, nullValue())

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

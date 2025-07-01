/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class SeriesCompensatorTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(SeriesCompensator().mRID, not(equalTo("")))
        assertThat(SeriesCompensator("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val seriesCompensator = SeriesCompensator()

        assertThat(seriesCompensator.r, nullValue())
        assertThat(seriesCompensator.r0, nullValue())
        assertThat(seriesCompensator.x, nullValue())
        assertThat(seriesCompensator.x0, nullValue())
        assertThat(seriesCompensator.varistorRatedCurrent, nullValue())
        assertThat(seriesCompensator.varistorVoltageThreshold, nullValue())

        seriesCompensator.fillFields(NetworkService())

        assertThat(seriesCompensator.r, equalTo(1.1))
        assertThat(seriesCompensator.r0, equalTo(2.2))
        assertThat(seriesCompensator.x, equalTo(3.3))
        assertThat(seriesCompensator.x0, equalTo(4.4))
        assertThat(seriesCompensator.varistorRatedCurrent, equalTo(5))
        assertThat(seriesCompensator.varistorVoltageThreshold, equalTo(6))
    }

    @Test
    internal fun varistorPresent() {
        val seriesCompensator1 = SeriesCompensator()
        val seriesCompensator2 = SeriesCompensator().apply { varistorRatedCurrent = 1 }
        val seriesCompensator3 = SeriesCompensator().apply { varistorVoltageThreshold = 1 }

        assertThat(seriesCompensator1.varistorPresent, equalTo(false))
        assertThat(seriesCompensator2.varistorPresent, equalTo(true))
        assertThat(seriesCompensator3.varistorPresent, equalTo(true))
    }

}

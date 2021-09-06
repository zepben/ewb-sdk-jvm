/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ShuntCompensatorInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(ShuntCompensatorInfo().mRID, not(equalTo("")))
        assertThat(ShuntCompensatorInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        ShuntCompensatorInfo().apply {

            assertThat(maxPowerLoss, nullValue())
            assertThat(ratedCurrent, nullValue())
            assertThat(ratedReactivePower, nullValue())
            assertThat(ratedVoltage, nullValue())

            fillFields(NetworkService())

            assertThat(maxPowerLoss, equalTo(1))
            assertThat(ratedCurrent, equalTo(2))
            assertThat(ratedReactivePower, equalTo(3))
            assertThat(ratedVoltage, equalTo(4))
        }
    }


}
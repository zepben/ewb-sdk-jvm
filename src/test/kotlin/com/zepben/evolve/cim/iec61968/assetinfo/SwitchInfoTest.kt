/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
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

internal class SwitchInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(SwitchInfo().mRID, not(equalTo("")))
        assertThat(SwitchInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val switchInfo = SwitchInfo()

        assertThat(switchInfo.ratedInterruptingTime, nullValue())

        switchInfo.fillFields(NetworkService())

        assertThat(switchInfo.ratedInterruptingTime, equalTo(1.1))
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class SwitchInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(SwitchInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val switchInfo = SwitchInfo(generateId())

        assertThat(switchInfo.ratedInterruptingTime, nullValue())

        switchInfo.fillFields(NetworkService())

        assertThat(switchInfo.ratedInterruptingTime, equalTo(1.1))
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.protection

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class CurrentRelayTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentRelay("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val currentRelay = CurrentRelay(generateId())

        assertThat(currentRelay.currentLimit1, nullValue())
        assertThat(currentRelay.inverseTimeFlag, nullValue())
        assertThat(currentRelay.timeDelay1, nullValue())

        currentRelay.fillFields(NetworkService())

        assertThat(currentRelay.currentLimit1, equalTo(1.1))
        assertThat(currentRelay.inverseTimeFlag, equalTo(true))
        assertThat(currentRelay.timeDelay1, equalTo(2.2))
    }

}

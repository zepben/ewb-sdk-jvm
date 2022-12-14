/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentRelayInfo
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class CurrentRelayTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentRelay().mRID, not(equalTo("")))
        assertThat(CurrentRelay("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val currentRelay = CurrentRelay()

        assertThat(currentRelay.assetInfo, nullValue())
        assertThat(currentRelay.currentLimit1, nullValue())
        assertThat(currentRelay.inverseTimeFlag, nullValue())
        assertThat(currentRelay.timeDelay1, nullValue())

        currentRelay.fillFields(NetworkService())

        assertThat(currentRelay.assetInfo, notNullValue())
        assertThat(currentRelay.assetInfo, instanceOf(CurrentRelayInfo::class.java))
        assertThat(currentRelay.currentLimit1, equalTo(1.1))
        assertThat(currentRelay.inverseTimeFlag, equalTo(true))
        assertThat(currentRelay.timeDelay1, equalTo(2.2))
    }

}

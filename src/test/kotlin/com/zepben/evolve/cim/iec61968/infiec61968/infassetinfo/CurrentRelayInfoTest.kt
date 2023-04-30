/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class CurrentRelayInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentRelayInfo().mRID, not(equalTo("")))
        assertThat(CurrentRelayInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val currentRelayInfo = CurrentRelayInfo()

        assertThat(currentRelayInfo.curveSetting, nullValue())
        assertThat(currentRelayInfo.recloseDelays, nullValue())

        currentRelayInfo.fillFields(NetworkService())

        assertThat(currentRelayInfo.curveSetting, equalTo("curveSetting"))
        assertThat(currentRelayInfo.recloseDelays, contains(1.0f, 2.0f, 3.0f))
    }

}

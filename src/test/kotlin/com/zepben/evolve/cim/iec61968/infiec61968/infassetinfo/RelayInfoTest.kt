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

internal class RelayInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(RelayInfo().mRID, not(equalTo("")))
        assertThat(RelayInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val relayInfo = RelayInfo()

        assertThat(relayInfo.curveSetting, nullValue())
        assertThat(relayInfo.recloseDelays, emptyIterable())

        relayInfo.fillFields(NetworkService())

        assertThat(relayInfo.curveSetting, equalTo("curveSetting"))
        assertThat(relayInfo.recloseDelays, contains(1.0, 2.0, 3.0))
    }

}

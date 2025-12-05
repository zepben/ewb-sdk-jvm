/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.assetinfo

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class RelayInfoTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(RelayInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val relayInfo = RelayInfo(generateId())

        assertThat(relayInfo.curveSetting, nullValue())
        assertThat(relayInfo.recloseFast, nullValue())
        assertThat(relayInfo.recloseDelays, emptyIterable())

        relayInfo.fillFields(NetworkService())

        assertThat(relayInfo.curveSetting, equalTo("curveSetting"))
        assertThat(relayInfo.recloseFast, equalTo(true))
        assertThat(relayInfo.recloseDelays, contains(1.0, 2.0, 3.0))
    }

    @Test
    internal fun recloseDelays() {
        PrivateCollectionValidator.validateOrdered(
            ::RelayInfo,
            { it.toDouble() },
            RelayInfo::recloseDelays,
            RelayInfo::numDelays,
            RelayInfo::getDelay,
            RelayInfo::forEachDelay,
            RelayInfo::addDelay,
            RelayInfo::addDelay,
            RelayInfo::removeDelay,
            RelayInfo::removeDelayAt,
            RelayInfo::clearDelays
        )
    }

}

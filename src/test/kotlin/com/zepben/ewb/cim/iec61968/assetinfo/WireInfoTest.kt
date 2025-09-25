/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class WireInfoTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : WireInfo() {}.mRID, not(equalTo("")))
        assertThat(object : WireInfo("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val wireInfo = object : WireInfo() {}

        assertThat(wireInfo.material, equalTo(WireMaterialKind.UNKNOWN))
        assertThat(wireInfo.ratedCurrent, nullValue())

        wireInfo.fillFields(NetworkService())

        assertThat(wireInfo.material, equalTo(WireMaterialKind.aaac))
        assertThat(wireInfo.ratedCurrent, equalTo(123))
    }

}

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
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class WireInfoTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : WireInfo("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val wireInfo = object : WireInfo(generateId()) {}

        assertThat(wireInfo.material, equalTo(WireMaterialKind.UNKNOWN))
        assertThat(wireInfo.ratedCurrent, nullValue())
        assertThat(wireInfo.sizeDescription, nullValue())
        assertThat(wireInfo.strandCount, nullValue())
        assertThat(wireInfo.coreStrandCount, nullValue())
        assertThat(wireInfo.insulated, nullValue())
        assertThat(wireInfo.insulatationMaterial, equalTo(WireInsulationKind.UNKNOWN))
        assertThat(wireInfo.insulatationThickness, nullValue())

        wireInfo.fillFields(NetworkService())

        assertThat(wireInfo.material, equalTo(WireMaterialKind.aaac))
        assertThat(wireInfo.ratedCurrent, equalTo(123))
        assertThat(wireInfo.sizeDescription, equalTo("6.7"))
        assertThat(wireInfo.strandCount, equalTo("8"))
        assertThat(wireInfo.coreStrandCount, equalTo("4"))
        assertThat(wireInfo.insulated, equalTo(true))
        assertThat(wireInfo.insulatationMaterial, equalTo(WireInsulationKind.doubleWireArmour))
        assertThat(wireInfo.insulatationThickness, equalTo(1.2))
    }

}

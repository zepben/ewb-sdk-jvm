/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo

import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PotentialTransformerInfoTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PotentialTransformerInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val potentialTransformerInfo = PotentialTransformerInfo(generateId())

        assertThat(potentialTransformerInfo.accuracyClass, nullValue())
        assertThat(potentialTransformerInfo.nominalRatio, nullValue())
        assertThat(potentialTransformerInfo.primaryRatio, nullValue())
        assertThat(potentialTransformerInfo.ptClass, nullValue())
        assertThat(potentialTransformerInfo.ratedVoltage, nullValue())
        assertThat(potentialTransformerInfo.secondaryRatio, nullValue())

        potentialTransformerInfo.fillFields(NetworkService())

        assertThat(potentialTransformerInfo.accuracyClass, equalTo("accuracyClass"))
        assertThat(potentialTransformerInfo.nominalRatio, equalTo(Ratio(1.1, 2.2)))
        assertThat(potentialTransformerInfo.primaryRatio, equalTo(3.3))
        assertThat(potentialTransformerInfo.ptClass, equalTo("ptClass"))
        assertThat(potentialTransformerInfo.ratedVoltage, equalTo(4))
        assertThat(potentialTransformerInfo.secondaryRatio, equalTo(5.5))
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo

import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CurrentTransformerInfoTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentTransformerInfo().mRID, not(equalTo("")))
        assertThat(CurrentTransformerInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val currentTransformerInfo = CurrentTransformerInfo()

        assertThat(currentTransformerInfo.accuracyClass, nullValue())
        assertThat(currentTransformerInfo.accuracyLimit, nullValue())
        assertThat(currentTransformerInfo.coreCount, nullValue())
        assertThat(currentTransformerInfo.ctClass, nullValue())
        assertThat(currentTransformerInfo.kneePointVoltage, nullValue())
        assertThat(currentTransformerInfo.maxRatio, nullValue())
        assertThat(currentTransformerInfo.nominalRatio, nullValue())
        assertThat(currentTransformerInfo.primaryRatio, nullValue())
        assertThat(currentTransformerInfo.ratedCurrent, nullValue())
        assertThat(currentTransformerInfo.secondaryFlsRating, nullValue())
        assertThat(currentTransformerInfo.secondaryRatio, nullValue())
        assertThat(currentTransformerInfo.usage, nullValue())

        currentTransformerInfo.fillFields(NetworkService())

        assertThat(currentTransformerInfo.accuracyClass, equalTo("accuracyClass"))
        assertThat(currentTransformerInfo.accuracyLimit, equalTo(1.1))
        assertThat(currentTransformerInfo.coreCount, equalTo(2))
        assertThat(currentTransformerInfo.ctClass, equalTo("ctClass"))
        assertThat(currentTransformerInfo.kneePointVoltage, equalTo(3))
        assertThat(currentTransformerInfo.maxRatio, equalTo(Ratio(4.4, 5.5)))
        assertThat(currentTransformerInfo.nominalRatio, equalTo(Ratio(6.6, 7.7)))
        assertThat(currentTransformerInfo.primaryRatio, equalTo(8.8))
        assertThat(currentTransformerInfo.ratedCurrent, equalTo(9))
        assertThat(currentTransformerInfo.secondaryFlsRating, equalTo(10))
        assertThat(currentTransformerInfo.secondaryRatio, equalTo(11.11))
        assertThat(currentTransformerInfo.usage, equalTo("usage"))
    }

}

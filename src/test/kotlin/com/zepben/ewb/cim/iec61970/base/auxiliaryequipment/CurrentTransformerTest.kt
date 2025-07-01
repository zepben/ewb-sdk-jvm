/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CurrentTransformerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(CurrentTransformer().mRID, not(equalTo("")))
        assertThat(CurrentTransformer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val currentTransformer = CurrentTransformer()

        assertThat(currentTransformer.assetInfo, nullValue())
        assertThat(currentTransformer.coreBurden, nullValue())

        currentTransformer.fillFields(NetworkService())

        assertThat(currentTransformer.assetInfo, instanceOf(CurrentTransformerInfo::class.java))
        assertThat(currentTransformer.coreBurden, equalTo(1))
    }

}

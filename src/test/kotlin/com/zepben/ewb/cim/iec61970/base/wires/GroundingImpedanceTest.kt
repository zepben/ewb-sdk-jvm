/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GroundingImpedanceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(GroundingImpedance().mRID, not(equalTo("")))
        assertThat(GroundingImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val groundingImpedance = GroundingImpedance()

        assertThat(groundingImpedance.x, equalTo(null))

        groundingImpedance.fillFields(NetworkService())

        assertThat(groundingImpedance.x, equalTo(1.0))
    }
}

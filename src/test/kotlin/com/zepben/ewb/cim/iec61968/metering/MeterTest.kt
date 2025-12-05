/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MeterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Meter("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val meter = Meter(generateId())

        assertThat(meter.companyMeterId, nullValue())
        assertThat(meter.name, nullValue())

        meter.name = "name"

        assertThat(meter.companyMeterId, equalTo("name"))
        assertThat(meter.name, equalTo("name"))

        meter.companyMeterId = "companyMeterId"

        assertThat(meter.companyMeterId, equalTo("companyMeterId"))
        assertThat(meter.name, equalTo("companyMeterId"))
    }

}

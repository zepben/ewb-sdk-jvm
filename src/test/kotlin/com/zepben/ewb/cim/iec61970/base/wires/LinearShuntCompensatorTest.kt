/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LinearShuntCompensatorTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(LinearShuntCompensator("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val linearShuntCompensator = LinearShuntCompensator(generateId())

        assertThat(linearShuntCompensator.b0PerSection, nullValue())
        assertThat(linearShuntCompensator.bPerSection, nullValue())
        assertThat(linearShuntCompensator.g0PerSection, nullValue())
        assertThat(linearShuntCompensator.gPerSection, nullValue())

        linearShuntCompensator.b0PerSection = 1.1
        linearShuntCompensator.bPerSection = 2.2
        linearShuntCompensator.g0PerSection = 3.3
        linearShuntCompensator.gPerSection = 4.4

        assertThat(linearShuntCompensator.b0PerSection, equalTo(1.1))
        assertThat(linearShuntCompensator.bPerSection, equalTo(2.2))
        assertThat(linearShuntCompensator.g0PerSection, equalTo(3.3))
        assertThat(linearShuntCompensator.gPerSection, equalTo(4.4))
    }

}

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

internal class TransformerTestTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : TransformerTest("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerTest = object : TransformerTest(generateId()) {}

        assertThat(transformerTest.basePower, nullValue())
        assertThat(transformerTest.temperature, nullValue())

        transformerTest.fillFields(NetworkService())

        assertThat(transformerTest.basePower, equalTo(1))
        assertThat(transformerTest.temperature, equalTo(2.2))
    }

}

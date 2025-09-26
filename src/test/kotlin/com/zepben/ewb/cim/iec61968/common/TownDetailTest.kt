/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TownDetailTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TownDetail(), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val townDetail = TownDetail("name", "stateOrProvince")

        assertThat(townDetail.name, equalTo("name"))
        assertThat(townDetail.stateOrProvince, equalTo("stateOrProvince"))
    }

    @Test
    internal fun testAllFieldsNullOrEmpty() {
        assertThat(TownDetail().allFieldsNullOrEmpty(), equalTo(true))
        assertThat(TownDetail("", "").allFieldsNullOrEmpty(), equalTo(true))

        assertThat(TownDetail(name = "value").allFieldsNullOrEmpty(), equalTo(false))
        assertThat(TownDetail(stateOrProvince = "value").allFieldsNullOrEmpty(), equalTo(false))
    }

    @Test
    internal fun testAllFieldsNull() {
        assertThat(TownDetail().allFieldsNull(), equalTo(true))
        assertThat(TownDetail("", "").allFieldsNull(), equalTo(false))

        assertThat(TownDetail(name = "value").allFieldsNull(), equalTo(false))
        assertThat(TownDetail(stateOrProvince = "value").allFieldsNull(), equalTo(false))
    }

}

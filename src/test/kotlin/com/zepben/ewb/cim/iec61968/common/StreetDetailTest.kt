/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
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

internal class StreetDetailTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(StreetDetail(), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val streetDetail = StreetDetail("buildingName", "floorIdentification", "name", "number", "suiteNumber", "type", "displayAddress", "buildingNumber")

        assertThat(streetDetail.buildingName, equalTo("buildingName"))
        assertThat(streetDetail.floorIdentification, equalTo("floorIdentification"))
        assertThat(streetDetail.name, equalTo("name"))
        assertThat(streetDetail.number, equalTo("number"))
        assertThat(streetDetail.suiteNumber, equalTo("suiteNumber"))
        assertThat(streetDetail.type, equalTo("type"))
        assertThat(streetDetail.displayAddress, equalTo("displayAddress"))
        assertThat(streetDetail.buildingNumber, equalTo("buildingNumber"))
    }

    @Test
    internal fun testAllFieldsEmpty() {
        assertThat("allFieldsEmpty should return true for empty StreetDetail", StreetDetail().allFieldsEmpty())
        assertThat("allFieldsEmpty should return true for empty StreetDetail", StreetDetail("", "", "", "", "", "", "", "").allFieldsEmpty())

        assertThat("allFieldsEmpty() should return false for nonempty buildingName", !StreetDetail(buildingName = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty floorIdentification", !StreetDetail(floorIdentification = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty name", !StreetDetail(name = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty number", !StreetDetail(number = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty suiteNumber", !StreetDetail(suiteNumber = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty type", !StreetDetail(type = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty displayAddress", !StreetDetail(displayAddress = "value").allFieldsEmpty())
        assertThat("allFieldsEmpty() should return false for nonempty buildingNumber", !StreetDetail(buildingNumber = "value").allFieldsEmpty())
    }

    @Test
    internal fun testAllFieldsNull() {
        assertThat("allFieldsNull should return true for empty StreetDetail", StreetDetail().allFieldsNull())

        assertThat("allFieldsNull() should return false for nonempty buildingName", !StreetDetail(buildingName = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty floorIdentification", !StreetDetail(floorIdentification = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty name", !StreetDetail(name = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty number", !StreetDetail(number = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty suiteNumber", !StreetDetail(suiteNumber = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty type", !StreetDetail(type = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty displayAddress", !StreetDetail(displayAddress = "value").allFieldsNull())
        assertThat("allFieldsNull() should return false for nonempty buildingNumber", !StreetDetail(buildingNumber = "value").allFieldsNull())
    }

}

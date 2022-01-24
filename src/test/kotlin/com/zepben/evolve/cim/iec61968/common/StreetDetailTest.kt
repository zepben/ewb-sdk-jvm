/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.common

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class StreetDetailTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(StreetDetail(), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val streetDetail = StreetDetail("buildingName", "floorIdentification", "name", "number", "suiteNumber", "type", "displayAddress")

        assertThat(streetDetail.buildingName, equalTo("buildingName"))
        assertThat(streetDetail.floorIdentification, equalTo("floorIdentification"))
        assertThat(streetDetail.name, equalTo("name"))
        assertThat(streetDetail.number, equalTo("number"))
        assertThat(streetDetail.suiteNumber, equalTo("suiteNumber"))
        assertThat(streetDetail.type, equalTo("type"))
        assertThat(streetDetail.displayAddress, equalTo("displayAddress"))
    }

    @Test
    internal fun testAllFieldsEmpty() {
        assertThat(StreetDetail().allFieldsEmpty(), equalTo(true))

        assertThat(StreetDetail(buildingName = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(floorIdentification = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(name = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(number = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(suiteNumber = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(type = "value").allFieldsEmpty(), equalTo(false))
        assertThat(StreetDetail(displayAddress = "value").allFieldsEmpty(), equalTo(false))
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61968.metering

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EndDeviceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : EndDevice() {}.mRID, not(equalTo("")))
        assertThat(object : EndDevice("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val endDevice = object : EndDevice() {}
        val location = Location()

        assertThat(endDevice.customerMRID, nullValue())
        assertThat(endDevice.serviceLocation, nullValue())

        endDevice.apply {
            customerMRID = "customerMRID"
            serviceLocation = location
        }

        assertThat(endDevice.customerMRID, equalTo("customerMRID"))
        assertThat(endDevice.serviceLocation, equalTo(location))
    }

    @Test
    internal fun usagePoints() {
        PrivateCollectionValidator.validate(
            { object : EndDevice() {} },
            { id, _ -> UsagePoint(id) },
            EndDevice::numUsagePoints,
            EndDevice::getUsagePoint,
            EndDevice::usagePoints,
            EndDevice::addUsagePoint,
            EndDevice::removeUsagePoint,
            EndDevice::clearUsagePoints
        )
    }
}

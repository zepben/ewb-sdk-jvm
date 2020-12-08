/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.metering

import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
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

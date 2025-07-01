/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.utils.PrivateCollectionValidator
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
        assertThat(object : com.zepben.ewb.cim.iec61968.metering.EndDevice() {}.mRID, not(equalTo("")))
        assertThat(object : com.zepben.ewb.cim.iec61968.metering.EndDevice("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val endDevice = object : com.zepben.ewb.cim.iec61968.metering.EndDevice() {}
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
        PrivateCollectionValidator.validateUnordered(
            { object : com.zepben.ewb.cim.iec61968.metering.EndDevice() {} },
            ::UsagePoint,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::usagePoints,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::numUsagePoints,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::getUsagePoint,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::addUsagePoint,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::removeUsagePoint,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::clearUsagePoints
        )
    }

    @Test
    internal fun endDeviceFunctions() {
        PrivateCollectionValidator.validateUnordered(
            { object : com.zepben.ewb.cim.iec61968.metering.EndDevice() {} },
            { id -> object : com.zepben.ewb.cim.iec61968.metering.EndDeviceFunction(id) {} },
            com.zepben.ewb.cim.iec61968.metering.EndDevice::functions,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::numFunctions,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::getFunction,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::addFunction,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::removeFunction,
            com.zepben.ewb.cim.iec61968.metering.EndDevice::clearFunctions
        )
    }
}

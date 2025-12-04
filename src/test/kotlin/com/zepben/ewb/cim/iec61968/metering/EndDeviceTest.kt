/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class EndDeviceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : EndDevice("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val endDevice = object : EndDevice(generateId()) {}
        val location = Location(generateId())

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
            { id -> object : EndDevice(id) {} },
            ::UsagePoint,
            EndDevice::usagePoints,
            EndDevice::numUsagePoints,
            EndDevice::getUsagePoint,
            EndDevice::addUsagePoint,
            EndDevice::removeUsagePoint,
            EndDevice::clearUsagePoints
        )
    }

    @Test
    internal fun endDeviceFunctions() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : EndDevice(id) {} },
            { id -> object : EndDeviceFunction(id) {} },
            EndDevice::functions,
            EndDevice::numFunctions,
            EndDevice::getFunction,
            EndDevice::addFunction,
            EndDevice::removeFunction,
            EndDevice::clearFunctions
        )
    }

}

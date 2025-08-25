/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class UsagePointTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(UsagePoint().mRID, not(equalTo("")))
        assertThat(UsagePoint("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val usagePoint = UsagePoint()

        assertThat(usagePoint.usagePointLocation, nullValue())
        assertThat(usagePoint.isVirtual, nullValue())
        assertThat(usagePoint.connectionCategory, nullValue())
        assertThat(usagePoint.ratedPower, nullValue())
        assertThat(usagePoint.approvedInverterCapacity, nullValue())
        assertThat(usagePoint.phaseCode, equalTo(PhaseCode.NONE))

        usagePoint.fillFields(NetworkService())

        assertThat(usagePoint.usagePointLocation, notNullValue())
        assertThat(usagePoint.isVirtual, equalTo(true))
        assertThat(usagePoint.connectionCategory, equalTo("connectionCategory"))
        assertThat(usagePoint.ratedPower, equalTo(2000))
        assertThat(usagePoint.approvedInverterCapacity, equalTo(5000))
        assertThat(usagePoint.phaseCode, equalTo(PhaseCode.AN))
    }

    @Test
    internal fun endDevices() {
        PrivateCollectionValidator.validateUnordered(
            ::UsagePoint,
            { id -> object : EndDevice(id) {} },
            UsagePoint::endDevices,
            UsagePoint::numEndDevices,
            UsagePoint::getEndDevice,
            UsagePoint::addEndDevice,
            UsagePoint::removeEndDevice,
            UsagePoint::clearEndDevices
        )
    }

    @Test
    internal fun equipment() {
        PrivateCollectionValidator.validateUnordered(
            ::UsagePoint,
            { id -> object : Equipment(id) {} },
            UsagePoint::equipment,
            UsagePoint::numEquipment,
            UsagePoint::getEquipment,
            UsagePoint::addEquipment,
            UsagePoint::removeEquipment,
            UsagePoint::clearEquipment
        )
    }
}

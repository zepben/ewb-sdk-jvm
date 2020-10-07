/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.metering

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61970.base.core.Equipment
import com.zepben.cimbend.utils.PrivateCollectionValidator
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
        val location = Location()

        assertThat(usagePoint.usagePointLocation, nullValue())

        usagePoint.usagePointLocation = location

        assertThat(usagePoint.usagePointLocation, equalTo(location))
    }

    @Test
    internal fun endDevices() {
        PrivateCollectionValidator.validate(
            { UsagePoint() },
            { id, _ -> object : EndDevice(id) {} },
            UsagePoint::numEndDevices,
            UsagePoint::getEndDevice,
            UsagePoint::endDevices,
            UsagePoint::addEndDevice,
            UsagePoint::removeEndDevice,
            UsagePoint::clearEndDevices
        )
    }

    @Test
    internal fun equipment() {
        PrivateCollectionValidator.validate(
            { UsagePoint() },
            { id, _ -> object : Equipment(id) {} },
            UsagePoint::numEquipment,
            UsagePoint::getEquipment,
            UsagePoint::equipment,
            UsagePoint::addEquipment,
            UsagePoint::removeEquipment,
            UsagePoint::clearEquipment
        )
    }
}

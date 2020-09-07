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
import com.zepben.cimbend.cim.iec61970.base.core.Equipment
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
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

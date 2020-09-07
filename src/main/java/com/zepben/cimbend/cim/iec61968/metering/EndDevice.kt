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

import com.zepben.cimbend.cim.iec61968.assets.AssetContainer
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.validateReference

/**
 * Asset container that performs one or more end device functions. One type of end device is a meter which can perform metering,
 * load management, connect/disconnect, accounting functions, etc. Some end devices, such as ones monitoring and controlling air
 * conditioners, refrigerators, pool pumps may be connected to a meter. All end devices may have communication capability defined
 * by the associated communication function(s). An end device may be owned by a consumer, a service provider, utility or otherwise.
 *
 * There may be a related end device function that identifies a sensor or control point within a metering application or
 * communications systems (e.g., water, gas, electricity).
 *
 * Some devices may use an optical port that conforms to the ANSI C12.18 standard for communications.
 *
 * @property customerMRID Customer owning this end device.
 * @property serviceLocation Service location whose service delivery is measured by this end device.
 */
abstract class EndDevice(mRID: String = "") : AssetContainer(mRID) {

    private var _usagePoints: MutableList<UsagePoint>? = null
    var customerMRID: String? = null
    var serviceLocation: Location? = null

    /**
     * The usage points belonging to this end device. The returned collection is read only.
     */
    val usagePoints: Collection<UsagePoint> get() = _usagePoints.asUnmodifiable()

    /**
     * Get the number of entries in the [UsagePoint] collection.
     */
    fun numUsagePoints() = _usagePoints?.size ?: 0

    /**
     * Usage point to which this end device belongs.
     *
     * @param mRID the mRID of the required [UsagePoint]
     * @return The [UsagePoint] with the specified [mRID] if it exists, otherwise null
     */
    fun getUsagePoint(mRID: String) = _usagePoints?.asSequence()?.firstOrNull { it.mRID == mRID }

    /**
     * @param usagePoint the usage point to associate with this end device.
     * @return true if the usage point is associated.
     */
    fun addUsagePoint(usagePoint: UsagePoint): EndDevice {
        if (validateReference(usagePoint, ::getUsagePoint, "A UsagePoint"))
            return this

        _usagePoints = _usagePoints ?: mutableListOf()
        _usagePoints!!.add(usagePoint)

        return this
    }

    /**
     * @param usagePoint the usage point to disassociate with this end device.
     * @return true if the usage point is disassociated.
     */
    fun removeUsagePoint(usagePoint: UsagePoint?): Boolean {
        val ret = _usagePoints?.remove(usagePoint) == true
        if (_usagePoints.isNullOrEmpty()) _usagePoints = null
        return ret
    }

    fun clearUsagePoints(): EndDevice {
        _usagePoints = null
        return this
    }
}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61968.assets.AssetContainer
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

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
 * @property usagePoints The collection of [UsagePoint] belonging to this [EndDevice]. The returned collection is read only.
 * @property functions The collection of [EndDeviceFunctionKind] present on this [EndDevice]. The returned collection is read only.
 */
abstract class EndDevice(mRID: String = "") : AssetContainer(mRID) {

    var customerMRID: String? = null
    var serviceLocation: Location? = null

    private var _usagePoints: MutableList<UsagePoint>? = null
    private var _functions: MutableList<EndDeviceFunction>? = null

    val usagePoints: MRIDListWrapper<UsagePoint>
        get() = MRIDListWrapper(
            getter = { _usagePoints },
            setter = { _usagePoints = it })

    @Deprecated("BOILERPLATE: Use usagePoints.size instead")
    fun numUsagePoints(): Int = usagePoints.size

    @Deprecated("BOILERPLATE: Use usagePoints.getByMRID(mRID) instead")
    fun getUsagePoint(mRID: String): UsagePoint? = usagePoints.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use usagePoints.add(usagePoint) instead")
    fun addUsagePoint(usagePoint: UsagePoint): EndDevice {
        usagePoints.add(usagePoint)
        return this
    }

    @Deprecated("BOILERPLATE: Use usagePoints.remove(usagePoint) instead")
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean = usagePoints.remove(usagePoint)

    @Deprecated("BOILERPLATE: Use usagePoints.clear() instead")
    fun clearUsagePoints(): EndDevice {
        usagePoints.clear()
        return this
    }

    val functions: MRIDListWrapper<EndDeviceFunction>
        get() = MRIDListWrapper(
            getter = { _functions },
            setter = { _functions = it })

    @Deprecated("BOILERPLATE: Use functions.size instead")
    fun numFunctions(): Int = functions.size

    @Deprecated("BOILERPLATE: Use functions.getByMRID(mRID) instead")
    fun getFunction(mRID: String): EndDeviceFunction? = functions.getByMRID(mRID)

    /**
     * Add a [EndDeviceFunction] for this [EndDevice]
     *
     * @throws IllegalStateException if the [EndDeviceFunction] references another [EndDevice]
     * @param function the [EndDeviceFunction] to be added to this [EndDevice]
     *
     * @return This [EndDevice] for fluent use
     */
    fun addFunction(function: EndDeviceFunction): EndDevice {
        if (validateFunction(function)) return this

        _functions = _functions.or(::mutableListOf) { add(function) }

        return this
    }

    @Deprecated("BOILERPLATE: Use functions.remove(function) instead")
    fun removeFunction(function: EndDeviceFunction): Boolean = functions.remove(function)

    @Deprecated("BOILERPLATE: Use functions.clear() instead")
    fun clearFunctions(): EndDevice {
        functions.clear()
        return this
    }

    private fun validateFunction(function: EndDeviceFunction): Boolean {
        return validateReference(function, ::getFunction, "A EndDeviceFunction")
    }

}

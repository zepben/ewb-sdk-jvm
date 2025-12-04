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
abstract class EndDevice(mRID: String) : AssetContainer(mRID) {

    var customerMRID: String? = null
    var serviceLocation: Location? = null

    private var _usagePoints: MutableList<UsagePoint>? = null
    private var _functions: MutableList<EndDeviceFunction>? = null

    val usagePoints: Collection<UsagePoint> get() = _usagePoints.asUnmodifiable()

    /**
     * Get the number of entries in the [UsagePoint] collection.
     */
    fun numUsagePoints(): Int = _usagePoints?.size ?: 0

    /**
     * Usage point to which this end device belongs.
     *
     * @param mRID the mRID of the required [UsagePoint]
     * @return The [UsagePoint] with the specified [mRID] if it exists, otherwise null
     */
    fun getUsagePoint(mRID: String): UsagePoint? = _usagePoints?.firstOrNull { it.mRID == mRID }

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
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean {
        val ret = _usagePoints?.remove(usagePoint) == true
        if (_usagePoints.isNullOrEmpty()) _usagePoints = null
        return ret
    }

    /**
     * Clear all [UsagePoint]'s attached to this [EndDevice].
     * @return This [EndDevice] for fluent use.
     */
    fun clearUsagePoints(): EndDevice {
        _usagePoints = null
        return this
    }

    val functions: List<EndDeviceFunction> get() = _functions.asUnmodifiable()

    /**
     * Get the number of entries in the [EndDeviceFunction] collection.
     */
    fun numFunctions(): Int = _functions?.size ?: 0

    /**
     * Get a [EndDeviceFunction] of this [EndDevice] by its [EndDeviceFunction.mRID]
     *
     * @param mRID the mRID of the required [EndDeviceFunction]
     * @return The [EndDeviceFunction] with the specified [mRID] if it exists, otherwise null
     */
    fun getFunction(mRID: String): EndDeviceFunction? = _functions.getByMRID(mRID)

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

    /**
     * @param function the [EndDeviceFunction] to disassociate with this end device.
     * @return true if the [EndDeviceFunction] is disassociated.
     */
    fun removeFunction(function: EndDeviceFunction): Boolean {
        val ret = _functions.safeRemove(function)
        if (_functions.isNullOrEmpty()) _functions = null
        return ret
    }

    /**
     * Clear all [EndDeviceFunction]'s attached to this [EndDevice].
     * @return This [EndDevice] for fluent use.
     */
    fun clearFunctions(): EndDevice {
        _functions = null
        return this
    }

    private fun validateFunction(function: EndDeviceFunction): Boolean {
        return validateReference(function, ::getFunction, "A EndDeviceFunction")
    }

}

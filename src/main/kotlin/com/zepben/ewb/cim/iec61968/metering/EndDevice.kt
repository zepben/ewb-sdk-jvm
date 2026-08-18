/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61968.assets.AssetContainer
import com.zepben.ewb.cim.iec61968.common.Location
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

    val usagePoints: MridCollection<UsagePoint> get() = LazyMridList(
        getter = { _usagePoints },
        setter = { _usagePoints = it },
        owner = this,
        elementDescription = "A UsagePoint"
    )

    val functions: MridCollection<EndDeviceFunction> get() = LazyMridList(
        getter = { _functions },
        setter = { _functions = it },
        owner = this,
        elementDescription = "An EndDeviceFunction"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region usagePoints boilerplate

    /**
     * Get the number of entries in the [UsagePoint] collection.
     */
    @Deprecated(
        message = "Use usagePoints.size instead.",
        replaceWith = ReplaceWith("usagePoints.size")
    )
    fun numUsagePoints(): Int = _usagePoints?.size ?: 0

    /**
     * Usage point to which this end device belongs.
     *
     * @param mRID the mRID of the required [UsagePoint]
     * @return The [UsagePoint] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use usagePoints.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("usagePoints.getByMRID(mRID)")
    )
    fun getUsagePoint(mRID: String): UsagePoint? = _usagePoints?.firstOrNull { it.mRID == mRID }

    /**
     * @param usagePoint the usage point to associate with this end device.
     * @return true if the usage point is associated.
     */
    @Deprecated(
        message = "Use usagePoints.add(usagePoint) instead.",
        replaceWith = ReplaceWith("also { it.usagePoints.add(usagePoint) }")
    )
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
    @Deprecated(
        message = "Use usagePoints.remove(usagePoint) instead.",
        replaceWith = ReplaceWith("usagePoints.remove(usagePoint)")
    )
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean {
        val ret = _usagePoints?.remove(usagePoint) == true
        if (_usagePoints.isNullOrEmpty()) _usagePoints = null
        return ret
    }

    /**
     * Clear all [UsagePoint]'s attached to this [EndDevice].
     * @return This [EndDevice] for fluent use.
     */
    @Deprecated(
        message = "Use usagePoints.clear() instead.",
        replaceWith = ReplaceWith("usagePoints.clear()")
    )
    fun clearUsagePoints(): EndDevice {
        _usagePoints = null
        return this
    }

    // endregion

    // region functions boilerplate

    @Deprecated("Helper for a deprecated function")
    private fun validateFunction(function: EndDeviceFunction): Boolean {
        return validateReference(function, ::getFunction, "A EndDeviceFunction")
    }

    /**
     * Get the number of entries in the [EndDeviceFunction] collection.
     */
    @Deprecated(
        message = "Use functions.size instead.",
        replaceWith = ReplaceWith("functions.size")
    )
    fun numFunctions(): Int = _functions?.size ?: 0

    /**
     * Get a [EndDeviceFunction] of this [EndDevice] by its [EndDeviceFunction.mRID]
     *
     * @param mRID the mRID of the required [EndDeviceFunction]
     * @return The [EndDeviceFunction] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use functions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("functions.getByMRID(mRID)")
    )
    fun getFunction(mRID: String): EndDeviceFunction? = _functions.getByMRID(mRID)

    /**
     * Add a [EndDeviceFunction] for this [EndDevice]
     *
     * @throws IllegalStateException if the [EndDeviceFunction] references another [EndDevice]
     * @param function the [EndDeviceFunction] to be added to this [EndDevice]
     *
     * @return This [EndDevice] for fluent use
     */
    @Deprecated(
        message = "Use functions.add(function) instead.",
        replaceWith = ReplaceWith("also { it.functions.add(function) }")
    )
    fun addFunction(function: EndDeviceFunction): EndDevice {
        if (validateFunction(function)) return this

        _functions = _functions.or(::mutableListOf) { add(function) }

        return this
    }

    /**
     * @param function the [EndDeviceFunction] to disassociate with this end device.
     * @return true if the [EndDeviceFunction] is disassociated.
     */
    @Deprecated(
        message = "Use functions.remove(function) instead.",
        replaceWith = ReplaceWith("functions.remove(function)")
    )
    fun removeFunction(function: EndDeviceFunction): Boolean {
        val ret = _functions.safeRemove(function)
        if (_functions.isNullOrEmpty()) _functions = null
        return ret
    }

    /**
     * Clear all [EndDeviceFunction]'s attached to this [EndDevice].
     * @return This [EndDevice] for fluent use.
     */
    @Deprecated(
        message = "Use functions.clear() instead.",
        replaceWith = ReplaceWith("functions.clear()")
    )
    fun clearFunctions(): EndDevice {
        _functions = null
        return this
    }

    // endregion

    // endregion
}

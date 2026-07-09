/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.assets.AssetContainer
import com.zepben.ewb.cim.iec61968.common.Location

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

    val usagePoints: LazyMridList<UsagePoint> get() = LazyMridList(
        getter = { _usagePoints },
        setter = { _usagePoints = it },
        owner = this,
        elementDescription = "A UsagePoint"
    )

    val functions: LazyMridList<EndDeviceFunction> get() = LazyMridList(
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

    @Deprecated(
        message = "Use usagePoints.size instead.",
        replaceWith = ReplaceWith("usagePoints.size")
    )
    fun numUsagePoints(): Int = usagePoints.size

    @Deprecated(
        message = "Use usagePoints.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("usagePoints.getByMRID(mRID)")
    )
    fun getUsagePoint(mRID: String): UsagePoint? = usagePoints.getByMrid(mRID)

    @Deprecated(
        message = "Use usagePoints.add(usagePoint) instead.",
        replaceWith = ReplaceWith("also { it.usagePoints.add(usagePoint) }")
    )
    fun addUsagePoint(usagePoint: UsagePoint): EndDevice {
        usagePoints.add(usagePoint)
        return this
    }

    @Deprecated(
        message = "Use usagePoints.remove(usagePoint) instead.",
        replaceWith = ReplaceWith("usagePoints.remove(usagePoint)")
    )
    fun removeUsagePoint(usagePoint: UsagePoint): Boolean = usagePoints.remove(usagePoint)

    @Deprecated(
        message = "Use usagePoints.clear() instead.",
        replaceWith = ReplaceWith("usagePoints.clear()")
    )
    fun clearUsagePoints(): EndDevice {
        usagePoints.clear()
        return this
    }

    // endregion

    // region functions boilerplate

    @Deprecated(
        message = "Use functions.size instead.",
        replaceWith = ReplaceWith("functions.size")
    )
    fun numFunctions(): Int = functions.size

    @Deprecated(
        message = "Use functions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("functions.getByMRID(mRID)")
    )
    fun getFunction(mRID: String): EndDeviceFunction? = functions.getByMrid(mRID)

    @Deprecated(
        message = "Use functions.add(function) instead.",
        replaceWith = ReplaceWith("also { it.functions.add(function) }")
    )
    fun addFunction(function: EndDeviceFunction): EndDevice {
        functions.add(function)
        return this
    }

    @Deprecated(
        message = "Use functions.remove(function) instead.",
        replaceWith = ReplaceWith("functions.remove(function)")
    )
    fun removeFunction(function: EndDeviceFunction): Boolean = functions.remove(function)

    @Deprecated(
        message = "Use functions.clear() instead.",
        replaceWith = ReplaceWith("functions.clear()")
    )
    fun clearFunctions(): EndDevice {
        functions.clear()
        return this
    }

    // endregion

    // endregion
}

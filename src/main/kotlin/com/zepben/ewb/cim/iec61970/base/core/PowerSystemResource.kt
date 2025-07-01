/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.iec61968.assets.Asset
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 *  Abstract class, should only be used through subclasses.
 *  A power system resource can be an item of equipment such as a switch, an equipment container containing many individual
 *  items of equipment such as a substation, or an organisational entity such as sub-control area. Power system resources
 *  can have measurements associated.
 *
 * @property assetInfo Datasheet information for this power system resource.
 * @property location Location of this power system resource.
 * @property numControls Number of Control's known to associate with this [PowerSystemResource]
 */
abstract class PowerSystemResource(mRID: String = "") : IdentifiedObject(mRID) {

    private var _assets: MutableList<Asset>? = null
    open val assetInfo: AssetInfo? get() = null
    var location: Location? = null
    var numControls: Int = 0

    /**
     * @return True if this [PowerSystemResource] has at least 1 Control associated with it, false otherwise.
     */
    fun hasControls(): Boolean = numControls > 0

    /**
     * All assets represented by this power system resource. For example, multiple conductor assets are electrically modelled as a single AC line segment.
     */
    val assets: Collection<Asset> get() = _assets.asUnmodifiable()

    /**
     * Get the number of entries in the [Asset]s collection.
     */
    fun numAssets(): Int = _assets?.size ?: 0

    /**
     * Get an [Asset] associated with this [PowerSystemResource]
     *
     * @param mRID the mRID of the required [Asset]
     * @return The [Asset] with the specified [mRID] if it exists, otherwise null
     */
    fun getAsset(mRID: String): Asset? = _assets.getByMRID(mRID)

    /**
     * Add an [Asset] to this PowerSystemResource
     *
     * @param asset the [Asset] to associate with this [PowerSystemResource].
     * @return A reference to this [PowerSystemResource] to allow fluent use.
     */
    fun addAsset(asset: Asset): PowerSystemResource {
        if (validateReference(asset, ::getAsset, "An Asset"))
            return this

        _assets = _assets ?: mutableListOf()
        _assets!!.add(asset)

        return this
    }

    /**
     * @param asset the [Asset] to disassociate from this [PowerSystemResource].
     * @return true if the [Asset] is disassociated.
     */
    fun removeAsset(asset: Asset): Boolean {
        val ret = _assets.safeRemove(asset)
        if (_assets.isNullOrEmpty()) _assets = null
        return ret
    }

    /**
     * Remove all [Asset]s from this PowerSystemResource
     * @return A reference to this [PowerSystemResource] to allow fluent use.
     */
    fun clearAssets(): PowerSystemResource {
        _assets = null
        return this
    }

}

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
import com.zepben.ewb.testing.MRIDListWrapper

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
    var numControls: Int? = null

    /**
     * @return True if this [PowerSystemResource] has at least 1 Control associated with it, false otherwise.
     */
    fun hasControls(): Boolean = (numControls ?: 0) > 0

    /**
     * All assets represented by this power system resource. For example, multiple conductor assets are electrically modelled as a single AC line segment.
     */
    val assets: MRIDListWrapper<Asset>
        get() = MRIDListWrapper(
            getter = { _assets },
            setter = { _assets = it })

    @Deprecated("BOILERPLATE: Use assets.size instead")
    fun numAssets(): Int = assets.size

    @Deprecated("BOILERPLATE: Use assets.getByMRID(mRID) instead")
    fun getAsset(mRID: String): Asset? = assets.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use assets.add(asset) instead")
    fun addAsset(asset: Asset): PowerSystemResource {
        assets.add(asset)
        return this
    }

    @Deprecated("BOILERPLATE: Use assets.remove(asset) instead")
    fun removeAsset(asset: Asset): Boolean = assets.remove(asset)

    @Deprecated("BOILERPLATE: Use assets.clear() instead")
    fun clearAssets(): PowerSystemResource {
        assets.clear()
        return this
    }

}

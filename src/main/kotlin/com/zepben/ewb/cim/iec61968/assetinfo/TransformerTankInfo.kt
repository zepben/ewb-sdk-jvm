/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * Set of transformer tank data, from an equipment library.
 *
 * @property powerTransformerInfo Power transformer data that this tank description is part of.
 */
class TransformerTankInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    var powerTransformerInfo: PowerTransformerInfo? = null

    private var _transformerEndInfos: MutableList<TransformerEndInfo>? = null

    /**
     * Data for all the ends described by this transformer tank data. The returned collection is read only.
     */
    val transformerEndInfos: MRIDListWrapper<TransformerEndInfo>
        get() = MRIDListWrapper(
            getter = { _transformerEndInfos },
            setter = { _transformerEndInfos = it })

    @Deprecated("BOILERPLATE: Use transformerEndInfos.size instead")
    fun numTransformerEndInfos(): Int = transformerEndInfos.size

    @Deprecated("BOILERPLATE: Use transformerEndInfos.getByMRID(mRID) instead")
    fun getTransformerEndInfo(mRID: String): TransformerEndInfo? = transformerEndInfos.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use transformerEndInfos.add(transformerEndInfo) instead")
    fun addTransformerEndInfo(transformerEndInfo: TransformerEndInfo): TransformerTankInfo {
        transformerEndInfos.add(transformerEndInfo)
        return this
    }

    @Deprecated("BOILERPLATE: Use transformerEndInfos.remove(transformerEndInfo) instead")
    fun removeTransformerEndInfo(transformerEndInfo: TransformerEndInfo): Boolean = transformerEndInfos.remove(transformerEndInfo)

    @Deprecated("BOILERPLATE: Use transformerEndInfos.clear() instead")
    fun clearTransformerEndInfos(): TransformerTankInfo {
        transformerEndInfos.clear()
        return this
    }

    /**
     * Get the [ResistanceReactance] for the specified [endNumber] from the datasheet information.
     */
    fun resistanceReactance(endNumber: Int): ResistanceReactance? =
        transformerEndInfos.firstOrNull { it.endNumber == endNumber }?.resistanceReactance()

}

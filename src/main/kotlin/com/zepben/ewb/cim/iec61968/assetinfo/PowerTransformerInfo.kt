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
 * Set of power transformer data, from an equipment library.
 */
class PowerTransformerInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    private var _transformerTankInfos: MutableList<TransformerTankInfo>? = null

    /**
     * Data for all the tanks described by this power transformer data. The returned collection is read only.
     */
    val transformerTankInfos: MRIDListWrapper<TransformerTankInfo>
        get() = MRIDListWrapper(
            getter = { _transformerTankInfos },
            setter = { _transformerTankInfos = it })

    @Deprecated("BOILERPLATE: Use transformerTankInfos.size instead")
    fun numTransformerTankInfos(): Int = transformerTankInfos.size

    @Deprecated("BOILERPLATE: Use transformerTankInfos.getByMRID(mRID) instead")
    fun getTransformerTankInfo(mRID: String): TransformerTankInfo? = transformerTankInfos.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use transformerTankInfos.add(transformerTankInfo) instead")
    fun addTransformerTankInfo(transformerTankInfo: TransformerTankInfo): PowerTransformerInfo {
        transformerTankInfos.add(transformerTankInfo)
        return this
    }

    @Deprecated("BOILERPLATE: Use transformerTankInfos.remove(transformerTankInfo) instead")
    fun removeTransformerTankInfo(transformerTankInfo: TransformerTankInfo): Boolean = transformerTankInfos.remove(transformerTankInfo)

    @Deprecated("BOILERPLATE: Use transformerTankInfos.clear() instead")
    fun clearTransformerTankInfos(): PowerTransformerInfo {
        transformerTankInfos.clear()
        return this
    }

    /**
     * Get the [ResistanceReactance] for the specified [endNumber] from the datasheet information.
     */
    fun resistanceReactance(endNumber: Int): ResistanceReactance? =
        transformerTankInfos.asSequence().mapNotNull { it.resistanceReactance(endNumber) }.firstOrNull()

}

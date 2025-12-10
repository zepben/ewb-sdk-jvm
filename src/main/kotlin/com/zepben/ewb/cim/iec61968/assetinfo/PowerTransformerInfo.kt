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

    /**
     * Get the number of entries in the [TransformerTankInfo] collection.
     */
    fun numTransformerTankInfos(): Int = _transformerTankInfos?.size ?: 0

    /**
     * Get the [TransformerTankInfo] of this [PowerTransformerInfo] represented by [mRID]
     *
     * @param mRID the mRID of the required [TransformerTankInfo]
     * @return The [TransformerTankInfo] with the specified [mRID] if it exists, otherwise null
     */
    fun getTransformerTankInfo(mRID: String): TransformerTankInfo? = _transformerTankInfos.getByMRID(mRID)

    /**
     * Add a [TransformerTankInfo] to this [PowerTransformerInfo]
     *
     * @return This [PowerTransformerInfo] for fluent use
     */
    fun addTransformerTankInfo(transformerTankInfo: TransformerTankInfo): PowerTransformerInfo {
        if (validateReference(transformerTankInfo, ::getTransformerTankInfo, "A TransformerTankInfo"))
            return this

        _transformerTankInfos = _transformerTankInfos ?: mutableListOf()
        _transformerTankInfos!!.add(transformerTankInfo)

        return this
    }

    /**
     * Remove a [TransformerTankInfo] from this [PowerTransformerInfo]
     *
     * @param transformerTankInfo The [TransformerTankInfo] to remove
     * @return true if [transformerTankInfo] is removed from the collection
     */
    fun removeTransformerTankInfo(transformerTankInfo: TransformerTankInfo): Boolean {
        val ret = _transformerTankInfos.safeRemove(transformerTankInfo)
        if (_transformerTankInfos.isNullOrEmpty()) _transformerTankInfos = null
        return ret
    }

    /**
     * Clear all [TransformerTankInfo]'s from this [PowerTransformerInfo]
     *
     * @return This [PowerTransformerInfo] for fluent use
     */
    fun clearTransformerTankInfos(): PowerTransformerInfo {
        _transformerTankInfos = null
        return this
    }

    /**
     * Get the [ResistanceReactance] for the specified [endNumber] from the datasheet information.
     */
    fun resistanceReactance(endNumber: Int): ResistanceReactance? =
        transformerTankInfos.asSequence().mapNotNull { it.resistanceReactance(endNumber) }.firstOrNull()

}

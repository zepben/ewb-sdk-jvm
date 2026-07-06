/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.services.network.ResistanceReactance

/**
 * Set of power transformer data, from an equipment library.
 */
class PowerTransformerInfo(mRID: String) : AssetInfo(mRID) {

    private var _transformerTankInfos: MutableList<TransformerTankInfo>? = null

    /**
     * Data for all the tanks described by this power transformer data. The returned collection is read only.
     */
    val transformerTankInfos: LazyMridList<TransformerTankInfo> get() = LazyMridList(
        getter = { _transformerTankInfos },
        setter = { _transformerTankInfos = it },
        owner = { this },
        elementDescription = "A TransformerTankInfo"
    )

    /**
     * Get the [ResistanceReactance] for the specified [endNumber] from the datasheet information.
     */
    fun resistanceReactance(endNumber: Int): ResistanceReactance? =
        transformerTankInfos.asSequence().mapNotNull { it.resistanceReactance(endNumber) }.firstOrNull()

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    @Deprecated(
        message = "Use transformerTankInfos.size instead.",
        replaceWith = ReplaceWith("transformerTankInfos.size")
    )
    fun numTransformerTankInfos(): Int = transformerTankInfos.size

    @Deprecated(
        message = "Use transformerTankInfos.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("transformerTankInfos.getByMRID(mRID)")
    )
    fun getTransformerTankInfo(mRID: String): TransformerTankInfo? = transformerTankInfos.getByMrid(mRID)

    @Deprecated(
        message = "Use transformerTankInfos.remove(transformerTankInfo) instead.",
        replaceWith = ReplaceWith("transformerTankInfos.remove(transformerTankInfo)")
    )
    fun removeTransformerTankInfo(transformerTankInfo: TransformerTankInfo): Boolean = transformerTankInfos.remove(transformerTankInfo)

    @Deprecated(
        message = "Use transformerTankInfos.clear() instead.",
        replaceWith = ReplaceWith("transformerTankInfos.clear()")
    )
    fun clearTransformerTankInfos(): PowerTransformerInfo {
        transformerTankInfos.clear()
        return this
    }

    @Deprecated(
        message = "Use transformerTankInfos.add(transformerTankInfo) instead.",
        replaceWith = ReplaceWith("also { it.transformerTankInfos.add(transformerTankInfo) }")
    )
    fun addTransformerTankInfo(transformerTankInfo: TransformerTankInfo): PowerTransformerInfo {
        transformerTankInfos.add(transformerTankInfo)
        return this
    }

    // endregion
}

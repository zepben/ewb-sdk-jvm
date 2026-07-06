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
 * Set of transformer tank data, from an equipment library.
 *
 * @property powerTransformerInfo Power transformer data that this tank description is part of.
 */
class TransformerTankInfo(mRID: String) : AssetInfo(mRID) {

    var powerTransformerInfo: PowerTransformerInfo? = null

    private var _transformerEndInfos: MutableList<TransformerEndInfo>? = null

    /**
     * Data for all the ends described by this transformer tank data. The returned collection is read only.
     */
    val transformerEndInfos: LazyMridList<TransformerEndInfo> get() = LazyMridList(
        getter = { _transformerEndInfos },
        setter = { _transformerEndInfos = it },
        owner = { this },
        elementDescription = "A TransformerEndInfo"
    )

    /**
     * Get the [ResistanceReactance] for the specified [endNumber] from the datasheet information.
     */
    fun resistanceReactance(endNumber: Int): ResistanceReactance? =
        transformerEndInfos.firstOrNull { it.endNumber == endNumber }?.resistanceReactance()

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    @Deprecated(
        message = "Use transformerEndInfos.size instead.",
        replaceWith = ReplaceWith("transformerEndInfos.size")
    )
    fun numTransformerEndInfos(): Int = transformerEndInfos.size

    @Deprecated(
        message = "Use transformerEndInfos.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("transformerEndInfos.getByMRID(mRID)")
    )
    fun getTransformerEndInfo(mRID: String): TransformerEndInfo? = transformerEndInfos.getByMrid(mRID)

    @Deprecated(
        message = "Use transformerEndInfos.remove(transformerEndInfo) instead.",
        replaceWith = ReplaceWith("transformerEndInfos.remove(transformerEndInfo)")
    )
    fun removeTransformerEndInfo(transformerEndInfo: TransformerEndInfo): Boolean = transformerEndInfos.remove(transformerEndInfo)

    @Deprecated(
        message = "Use transformerEndInfos.clear() instead.",
        replaceWith = ReplaceWith("transformerEndInfos.clear()")
    )
    fun clearTransformerEndInfos(): TransformerTankInfo {
        transformerEndInfos.clear()
        return this
    }

    @Deprecated(
        message = "Use transformerEndInfos.add(transformerEndInfo) instead.",
        replaceWith = ReplaceWith("also { it.transformerEndInfos.add(transformerEndInfo) }")
    )
    fun addTransformerEndInfo(transformerEndInfo: TransformerEndInfo): TransformerTankInfo {
        transformerEndInfos.add(transformerEndInfo)
        return this
    }

    // endregion
}

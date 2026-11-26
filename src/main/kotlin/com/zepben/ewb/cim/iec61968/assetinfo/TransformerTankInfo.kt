/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridList
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
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
    val transformerEndInfos: MridList<TransformerEndInfo> get() = LazyMridList(
        getter = { _transformerEndInfos },
        setter = { _transformerEndInfos = it },
        owner = this,
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

    // region transformerEndInfos boilerplate

    /**
     * Get the number of entries in the [TransformerEndInfo] collection.
     */
    @Deprecated(
        message = "Use transformerEndInfos.size instead.",
        replaceWith = ReplaceWith("transformerEndInfos.size")
    )
    fun numTransformerEndInfos(): Int = _transformerEndInfos?.size ?: 0

    /**
     * Get the [TransformerEndInfo] of this [TransformerTankInfo] represented by [mRID]
     *
     * @param mRID the mRID of the required [TransformerEndInfo]
     * @return The [TransformerEndInfo] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use transformerEndInfos.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("transformerEndInfos.getByMRID(mRID)")
    )
    fun getTransformerEndInfo(mRID: String): TransformerEndInfo? = _transformerEndInfos.getByMRID(mRID)

    /**
     * Add a [TransformerEndInfo] to this [TransformerTankInfo]
     *
     * @return This [TransformerTankInfo] for fluent use
     */
    @Deprecated(
        message = "Use transformerEndInfos.add(transformerEndInfo) instead.",
        replaceWith = ReplaceWith("also { it.transformerEndInfos.add(transformerEndInfo) }")
    )
    fun addTransformerEndInfo(transformerEndInfo: TransformerEndInfo): TransformerTankInfo {
        if (validateReference(transformerEndInfo, ::getTransformerEndInfo, "A TransformerEndInfo"))
            return this

        _transformerEndInfos = _transformerEndInfos ?: mutableListOf()
        _transformerEndInfos!!.add(transformerEndInfo)

        return this
    }

    /**
     * Remove a [TransformerEndInfo] from this [TransformerTankInfo]
     *
     * @param transformerEndInfo The [TransformerEndInfo] to remove
     * @return true if [transformerEndInfo] is removed from the collection
     */
    @Deprecated(
        message = "Use transformerEndInfos.remove(transformerEndInfo) instead.",
        replaceWith = ReplaceWith("transformerEndInfos.remove(transformerEndInfo)")
    )
    fun removeTransformerEndInfo(transformerEndInfo: TransformerEndInfo): Boolean {
        val ret = _transformerEndInfos.safeRemove(transformerEndInfo)
        if (_transformerEndInfos.isNullOrEmpty()) _transformerEndInfos = null
        return ret
    }

    /**
     * Clear all [TransformerEndInfo]'s from this [TransformerTankInfo]
     *
     * @return This [TransformerTankInfo] for fluent use
     */
    @Deprecated(
        message = "Use transformerEndInfos.clear() instead.",
        replaceWith = ReplaceWith("transformerEndInfos.clear()")
    )
    fun clearTransformerEndInfos(): TransformerTankInfo {
        _transformerEndInfos = null
        return this
    }

    // endregion

    // endregion
}

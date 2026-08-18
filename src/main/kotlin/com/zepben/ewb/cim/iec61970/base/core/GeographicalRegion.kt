/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A geographical region of a power system network model.
 */
class GeographicalRegion(mRID: String) : IdentifiedObject(mRID) {

    private var _subGeographicalRegions: MutableList<SubGeographicalRegion>? = null

    /**
     * All sub-geographical regions within this geographical region. The returned collection is read only.
     */
    val subGeographicalRegions: MridCollection<SubGeographicalRegion> get() = LazyMridList(
        getter = { _subGeographicalRegions },
        setter = { _subGeographicalRegions = it },
        owner = this,
        elementDescription = "A SubGeographicalRegion",
        backfill = Backfill(
            { it.geographicalRegion },
            { it, geo -> it.geographicalRegion = geo },
            SubGeographicalRegion::geographicalRegion
        )
    )


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region subGeographicalRegions boilerplate

    /**
     * Get the number of entries in the [SubGeographicalRegion] collection.
     */
    @Deprecated(
        message = "Use subGeographicalRegions.size instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.size")
    )
    fun numSubGeographicalRegions(): Int = _subGeographicalRegions?.size ?: 0

    /**
     * All sub-geographical regions within this geographical region.
     *
     * @param mRID the mRID of the required [SubGeographicalRegion]
     * @return The [SubGeographicalRegion] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use subGeographicalRegions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.getByMRID(mRID)")
    )
    fun getSubGeographicalRegion(mRID: String): SubGeographicalRegion? = _subGeographicalRegions.getByMRID(mRID)

    /**
     * @param subGeographicalRegion The sub geographical region to associate within this geographical region.
     * @return A reference to this [GeographicalRegion] to allow fluent use.
     */
    @Deprecated(
        message = "Use subGeographicalRegions.add(subGeographicalRegion) instead.",
        replaceWith = ReplaceWith("also { it.subGeographicalRegions.add(subGeographicalRegion) }")
    )
    fun addSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): GeographicalRegion {
        if (validateReference(subGeographicalRegion, ::getSubGeographicalRegion, "A SubGeographicalRegion"))
            return this

        if (subGeographicalRegion.geographicalRegion == null)
            subGeographicalRegion.geographicalRegion = this

        require(subGeographicalRegion.geographicalRegion === this) {
            "${subGeographicalRegion.typeNameAndMRID()} `geographicalRegion` property references ${subGeographicalRegion.geographicalRegion!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _subGeographicalRegions = _subGeographicalRegions ?: mutableListOf()
        _subGeographicalRegions!!.add(subGeographicalRegion)

        return this
    }

    /**
     * @param subGeographicalRegion The sub geographical region to disassociate from this geographical region.
     * @return True if the subGeographicalRegion existed and was removed from this GeographicalRegion, false otherwise
     */
    @Deprecated(
        message = "Use subGeographicalRegions.remove(subGeographicalRegion) instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.remove(subGeographicalRegion)")
    )
    fun removeSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): Boolean {
        val ret = _subGeographicalRegions.safeRemove(subGeographicalRegion)
        if (_subGeographicalRegions.isNullOrEmpty()) _subGeographicalRegions = null
        return ret
    }

    /**
     * @return A reference to this [GeographicalRegion] to allow fluent use.
     */
    @Deprecated(
        message = "Use subGeographicalRegions.clear() instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.clear()")
    )
    fun clearSubGeographicalRegions(): GeographicalRegion {
        _subGeographicalRegions = null
        return this
    }

    // endregion

    // endregion
}

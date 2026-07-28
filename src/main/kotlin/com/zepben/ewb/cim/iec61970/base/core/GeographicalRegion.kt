/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection

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

    @Deprecated(
        message = "Use subGeographicalRegions.size instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.size")
    )
    fun numSubGeographicalRegions(): Int = subGeographicalRegions.size

    @Deprecated(
        message = "Use subGeographicalRegions.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.getByMRID(mRID)")
    )
    fun getSubGeographicalRegion(mRID: String): SubGeographicalRegion? = subGeographicalRegions.getByMrid(mRID)

    @Deprecated(
        message = "Use subGeographicalRegions.add(subGeographicalRegion) instead.",
        replaceWith = ReplaceWith("also { it.subGeographicalRegions.add(subGeographicalRegion) }")
    )
    fun addSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): GeographicalRegion {
        subGeographicalRegions.add(subGeographicalRegion)
        return this
    }

    @Deprecated(
        message = "Use subGeographicalRegions.remove(subGeographicalRegion) instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.remove(subGeographicalRegion)")
    )
    fun removeSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): Boolean = subGeographicalRegions.remove(subGeographicalRegion)

    @Deprecated(
        message = "Use subGeographicalRegions.clear() instead.",
        replaceWith = ReplaceWith("subGeographicalRegions.clear()")
    )
    fun clearSubGeographicalRegions(): GeographicalRegion {
        subGeographicalRegions.clear()
        return this
    }

    // endregion

    // endregion
}

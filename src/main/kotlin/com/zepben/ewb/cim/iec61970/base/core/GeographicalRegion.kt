/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.services.common.extensions.*
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * A geographical region of a power system network model.
 */
class GeographicalRegion @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _subGeographicalRegions: MutableList<SubGeographicalRegion>? = null

    /**
     * All sub-geographical regions within this geographical region. The returned collection is read only.
     */
    val subGeographicalRegions: MRIDListWrapper<SubGeographicalRegion>
        get() = MRIDListWrapper(
            getter = { _subGeographicalRegions },
            setter = { _subGeographicalRegions = it },
            customAdd = { addSubGeographicalRegionCustom(it) })


    @Deprecated("BOILERPLATE: Use subGeographicalRegions.size instead")
    fun numSubGeographicalRegions(): Int = subGeographicalRegions.size

    @Deprecated("BOILERPLATE: Use subGeographicalRegions.getByMRID(mRID) instead")
    fun getSubGeographicalRegion(mRID: String): SubGeographicalRegion? = subGeographicalRegions.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use subGeographicalRegions.add(subGeographicalRegion) instead")
    fun addSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): GeographicalRegion {
        subGeographicalRegions.add(subGeographicalRegion)
        return this
    }

    private fun addSubGeographicalRegionCustom(subGeographicalRegion: SubGeographicalRegion): Boolean {
        if (validateReference(subGeographicalRegion, ::getSubGeographicalRegion, "A SubGeographicalRegion"))
            return false

        if (subGeographicalRegion.geographicalRegion == null)
            subGeographicalRegion.geographicalRegion = this

        require(subGeographicalRegion.geographicalRegion === this) {
            "${subGeographicalRegion.typeNameAndMRID()} `geographicalRegion` property references ${subGeographicalRegion.geographicalRegion!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _subGeographicalRegions = _subGeographicalRegions ?: mutableListOf()
        return _subGeographicalRegions!!.add(subGeographicalRegion)
    }

    @Deprecated("BOILERPLATE: Use subGeographicalRegions.remove(subGeographicalRegion) instead")
    fun removeSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): Boolean = subGeographicalRegions.remove(subGeographicalRegion)

    @Deprecated("BOILERPLATE: Use subGeographicalRegions.clear() instead")
    fun clearSubGeographicalRegions(): GeographicalRegion {
        subGeographicalRegions.clear()
        return this
    }
}

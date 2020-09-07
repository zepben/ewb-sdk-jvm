/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.safeRemove
import com.zepben.cimbend.common.extensions.validateReference

/**
 * A geographical region of a power system network model.
 */
class GeographicalRegion @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _subGeographicalRegions: MutableList<SubGeographicalRegion>? = null

    /**
     * All sub-geographical regions within this geographical region. The returned collection is read only.
     */
    val subGeographicalRegions: Collection<SubGeographicalRegion> get() = _subGeographicalRegions.asUnmodifiable()

    /**
     * Get the number of entries in the [SubGeographicalRegion] collection.
     */
    fun numSubGeographicalRegions() = _subGeographicalRegions?.size ?: 0

    /**
     * All sub-geographical regions within this geographical region.
     *
     * @param mRID the mRID of the required [SubGeographicalRegion]
     * @return The [SubGeographicalRegion] with the specified [mRID] if it exists, otherwise null
     */
    fun getSubGeographicalRegion(mRID: String) = _subGeographicalRegions.getByMRID(mRID)

    /**
     * @param subGeographicalRegion The sub geographical region to associate within this geographical region.
     * @return A reference to this [GeographicalRegion] to allow fluent use.
     */
    fun addSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion): GeographicalRegion {
        if (validateReference(subGeographicalRegion, ::getSubGeographicalRegion, "A SubGeographicalRegion"))
            return this

        _subGeographicalRegions = _subGeographicalRegions ?: mutableListOf()
        _subGeographicalRegions!!.add(subGeographicalRegion)

        return this
    }

    /**
     * @param subGeographicalRegion The sub geographical region to disassociate from this geographical region.
     * @return True if the subGeographicalRegion existed and was removed from this GeographicalRegion, false otherwise
     */
    fun removeSubGeographicalRegion(subGeographicalRegion: SubGeographicalRegion?): Boolean {
        val ret = _subGeographicalRegions.safeRemove(subGeographicalRegion)
        if (_subGeographicalRegions.isNullOrEmpty()) _subGeographicalRegions = null
        return ret
    }

    fun clearSubGeographicalRegions(): GeographicalRegion {
        _subGeographicalRegions = null
        return this
    }
}

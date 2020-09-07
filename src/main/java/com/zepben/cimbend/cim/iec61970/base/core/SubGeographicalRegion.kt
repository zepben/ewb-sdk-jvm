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
 * A subset of a geographical region of a power system network model.
 * @property geographicalRegion The geographical region to which this sub-geographical region is within.
 */
class SubGeographicalRegion @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var geographicalRegion: GeographicalRegion? = null
    private var _substations: MutableList<Substation>? = null

    /**
     * All substations belonging to this sub geographical region. The returned collection is read only.
     */
    val substations: Collection<Substation> get() = _substations.asUnmodifiable()

    /**
     * Get the number of entries in the [Substation] collection.
     */
    fun numSubstations() = _substations?.size ?: 0

    /**
     * The substations in this sub-geographical region.
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    fun getSubstation(mRID: String) = _substations.getByMRID(mRID)

    /**
     * @param substation the [Substation] to associate with this [SubGeographicalRegion].
     * @return A reference to this [SubGeographicalRegion] to allow fluent use.
     */
    fun addSubstation(substation: Substation): SubGeographicalRegion {
        if (validateReference(substation, ::getSubstation, "A Substation"))
            return this

        _substations = _substations ?: mutableListOf()
        _substations!!.add(substation)

        return this
    }

    /**
     * @param substation the [Substation] to disassociate with this [SubGeographicalRegion].
     * @return true if the substation is disassociated.
     */
    fun removeSubstation(substation: Substation?): Boolean {
        val ret = _substations.safeRemove(substation)
        if (_substations.isNullOrEmpty()) _substations = null
        return ret
    }

    /**
     * Clear this [SubGeographicalRegion]'s [Substation]'s
     * @return this [SubGeographicalRegion]
     */
    fun clearSubstations(): SubGeographicalRegion {
        _substations = null
        return this
    }
}

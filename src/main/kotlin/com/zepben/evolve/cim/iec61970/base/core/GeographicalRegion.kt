/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.*

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
    fun numSubGeographicalRegions(): Int = _subGeographicalRegions?.size ?: 0

    /**
     * All sub-geographical regions within this geographical region.
     *
     * @param mRID the mRID of the required [SubGeographicalRegion]
     * @return The [SubGeographicalRegion] with the specified [mRID] if it exists, otherwise null
     */
    fun getSubGeographicalRegion(mRID: String): SubGeographicalRegion? = _subGeographicalRegions.getByMRID(mRID)

    /**
     * @param subGeographicalRegion The sub geographical region to associate within this geographical region.
     * @return A reference to this [GeographicalRegion] to allow fluent use.
     */
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

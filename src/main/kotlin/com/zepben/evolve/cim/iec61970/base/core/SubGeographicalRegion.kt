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
    fun numSubstations(): Int = _substations?.size ?: 0

    /**
     * The substations in this sub-geographical region.
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    fun getSubstation(mRID: String): Substation? = _substations.getByMRID(mRID)

    /**
     * @param substation the [Substation] to associate with this [SubGeographicalRegion].
     * @return A reference to this [SubGeographicalRegion] to allow fluent use.
     */
    fun addSubstation(substation: Substation): SubGeographicalRegion {
        if (validateReference(substation, ::getSubstation, "A Substation"))
            return this

        if (substation.subGeographicalRegion == null)
            substation.subGeographicalRegion = this

        require(substation.subGeographicalRegion === this) {
            "${substation.typeNameAndMRID()} `subGeographicalRegion` property references ${substation.subGeographicalRegion!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _substations = _substations ?: mutableListOf()
        _substations!!.add(substation)

        return this
    }

    /**
     * @param substation the [Substation] to disassociate with this [SubGeographicalRegion].
     * @return true if the substation is disassociated.
     */
    fun removeSubstation(substation: Substation): Boolean {
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

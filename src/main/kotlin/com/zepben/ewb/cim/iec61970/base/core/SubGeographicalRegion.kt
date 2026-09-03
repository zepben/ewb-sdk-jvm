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
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A subset of a geographical region of a power system network model.
 * @property geographicalRegion The geographical region to which this sub-geographical region is within.
 */
class SubGeographicalRegion(mRID: String) : IdentifiedObject(mRID) {

    var geographicalRegion: GeographicalRegion? = null
    private var _substations: MutableList<Substation>? = null

    /**
     * All substations belonging to this sub geographical region. The returned collection is read only.
     */
    val substations: MridCollection<Substation> get() = LazyMridList(
        getter = { _substations },
        setter = { _substations = it },
        owner = this,
        elementDescription = "A Substation",
        backfill = Backfill(
            { it.subGeographicalRegion },
            { it, subgeo -> it.subGeographicalRegion = subgeo },
            Substation::subGeographicalRegion
        )
    )


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region substations boilerplate

    /**
     * Get the number of entries in the [Substation] collection.
     */
    @Deprecated(
        message = "Use substations.size instead.",
        replaceWith = ReplaceWith("substations.size")
    )
    fun numSubstations(): Int = _substations?.size ?: 0

    /**
     * The substations in this sub-geographical region.
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use substations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("substations.getByMRID(mRID)")
    )
    fun getSubstation(mRID: String): Substation? = _substations.getByMRID(mRID)

    /**
     * @param substation the [Substation] to associate with this [SubGeographicalRegion].
     * @return A reference to this [SubGeographicalRegion] to allow fluent use.
     */
    @Deprecated(
        message = "Use substations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.substations.add(substation) }")
    )
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
    @Deprecated(
        message = "Use substations.remove(substation) instead.",
        replaceWith = ReplaceWith("substations.remove(substation)")
    )
    fun removeSubstation(substation: Substation): Boolean {
        val ret = _substations.safeRemove(substation)
        if (_substations.isNullOrEmpty()) _substations = null
        return ret
    }

    /**
     * Clear this [SubGeographicalRegion]'s [Substation]'s
     * @return this [SubGeographicalRegion]
     */
    @Deprecated(
        message = "Use substations.clear() instead.",
        replaceWith = ReplaceWith("substations.clear()")
    )
    fun clearSubstations(): SubGeographicalRegion {
        _substations = null
        return this
    }

    // endregion

    // endregion
}

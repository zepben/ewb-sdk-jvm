/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.LazyMridList

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
    val substations: LazyMridList<Substation> get() = LazyMridList(
        getter = { _substations },
        setter = { _substations = it },
        owner = this,
        elementDescription = "A Substation",
        validate = { validateSubstation(it) }
    )

    private fun validateSubstation(substation: Substation) {
        if (substation.subGeographicalRegion == null)
            substation.subGeographicalRegion = this

        require(substation.subGeographicalRegion === this) {
            "${substation.typeNameAndMRID()} `subGeographicalRegion` property references ${substation.subGeographicalRegion!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
    }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region substations boilerplate

    @Deprecated(
        message = "Use substations.size instead.",
        replaceWith = ReplaceWith("substations.size")
    )
    fun numSubstations(): Int = substations.size

    @Deprecated(
        message = "Use substations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("substations.getByMRID(mRID)")
    )
    fun getSubstation(mRID: String): Substation? = substations.getByMrid(mRID)

    @Deprecated(
        message = "Use substations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.substations.add(substation) }")
    )
    fun addSubstation(substation: Substation): SubGeographicalRegion {
        substations.add(substation)
        return this
    }

    @Deprecated(
        message = "Use substations.remove(substation) instead.",
        replaceWith = ReplaceWith("substations.remove(substation)")
    )
    fun removeSubstation(substation: Substation): Boolean = substations.remove(substation)

    @Deprecated(
        message = "Use substations.clear() instead.",
        replaceWith = ReplaceWith("substations.clear()")
    )
    fun clearSubstations(): SubGeographicalRegion {
        substations.clear()
        return this
    }

    // endregion

    // endregion
}

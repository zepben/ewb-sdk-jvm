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
 * A subset of a geographical region of a power system network model.
 * @property geographicalRegion The geographical region to which this sub-geographical region is within.
 */
class SubGeographicalRegion @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    var geographicalRegion: GeographicalRegion? = null
    private var _substations: MutableList<Substation>? = null

    /**
     * All substations belonging to this sub geographical region. The returned collection is read only.
     */
    val substations: MRIDListWrapper<Substation>
        get() = MRIDListWrapper(
            getter = { _substations },
            setter = { _substations = it },
            customAdd = { addSubstationCustom(it) })

    @Deprecated("BOILERPLATE: Use substations.size instead")
    fun numSubstations(): Int = substations.size

    @Deprecated("BOILERPLATE: Use substations.getByMRID(mRID) instead")
    fun getSubstation(mRID: String): Substation? = substations.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use substations.add(substation) instead")
    fun addSubstation(substation: Substation): SubGeographicalRegion {
        substations.add(substation)
        return this
    }

    /**
     * @param substation the [Substation] to associate with this [SubGeographicalRegion].
     * @return A reference to this [SubGeographicalRegion] to allow fluent use.
     */
    private fun addSubstationCustom(substation: Substation): Boolean {
        if (validateReference(substation, ::getSubstation, "A Substation"))
            return false

        if (substation.subGeographicalRegion == null)
            substation.subGeographicalRegion = this

        require(substation.subGeographicalRegion === this) {
            "${substation.typeNameAndMRID()} `subGeographicalRegion` property references ${substation.subGeographicalRegion!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _substations = _substations ?: mutableListOf()
        _substations!!.add(substation)

        return true
    }

    @Deprecated("BOILERPLATE: Use substations.remove(substation) instead")
    fun removeSubstation(substation: Substation): Boolean = substations.remove(substation)

    @Deprecated("BOILERPLATE: Use substations.clear() instead")
    fun clearSubstations(): SubGeographicalRegion {
        substations.clear()
        return this
    }
}

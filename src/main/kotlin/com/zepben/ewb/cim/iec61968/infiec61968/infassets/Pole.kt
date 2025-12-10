/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassets

import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.assets.Structure
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * A Pole asset
 */
class Pole @JvmOverloads constructor(mRID: String = "") : Structure(mRID) {
    /**
     * Pole class: 1, 2, 3, 4, 5, 6, 7, H1, H2, Other, Unknown.
     */
    var classification: String? = null

    private var _streetlights: MutableList<Streetlight>? = null

    /**
     * All streetlights attached to this Pole. Collection is read only.
     */
    val streetlights: MRIDListWrapper<Streetlight>
        get() = MRIDListWrapper(
            getter = { _streetlights },
            setter = { _streetlights = it })

    @Deprecated("BOILERPLATE: Use streetlights.size instead")
    fun numStreetlights(): Int = streetlights.size

    @Deprecated("BOILERPLATE: Use streetlights.getByMRID(mRID) instead")
    fun getStreetlight(mRID: String): Streetlight? = streetlights.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use streetlights.add(streetlight) instead")
    fun addStreetlight(streetlight: Streetlight): Pole {
        streetlights.add(streetlight)
        return this
    }

    @Deprecated("BOILERPLATE: Use streetlights.remove(streetlight) instead")
    fun removeStreetlight(streetlight: Streetlight): Boolean = streetlights.remove(streetlight)

    /**
     * Clear all [Streetlight]s attached to this [Pole].
     */
    fun clearStreetlights(): Pole {
        _streetlights = null
        return this
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassets

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.assets.Structure

/**
 * A Pole asset
 *
 * @property classification Pole class: 1, 2, 3, 4, 5, 6, 7, H1, H2, Other, Unknown.
 */
class Pole(mRID: String) : Structure(mRID) {

    var classification: String? = null

    private var _streetlights: MutableList<Streetlight>? = null

    /**
     * All streetlights attached to this Pole. Collection is read only.
     */
    val streetlights: LazyMridList<Streetlight> get() = LazyMridList(
        getter = { _streetlights },
        setter = { _streetlights = it },
        owner = this,
        elementDescription = "A Streetlight",
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region streetlights boilerplate

    @Deprecated(
        message = "Use streetlights.size instead.",
        replaceWith = ReplaceWith("streetlights.size")
    )
    fun numStreetlights(): Int = streetlights.size

    @Deprecated(
        message = "Use streetlights.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("streetlights.getByMRID(mRID)")
    )
    fun getStreetlight(mRID: String): Streetlight? = streetlights.getByMrid(mRID)

    @Deprecated(
        message = "Use streetlights.add(streetlight) instead.",
        replaceWith = ReplaceWith("also { it.streetlights.add(streetlight) }")
    )
    fun addStreetlight(streetlight: Streetlight): Pole {
        streetlights.add(streetlight)
        return this
    }

    @Deprecated(
        message = "Use streetlights.remove(streetlight) instead.",
        replaceWith = ReplaceWith("streetlights.remove(streetlight)")
    )
    fun removeStreetlight(streetlight: Streetlight): Boolean = streetlights.remove(streetlight)

    @Deprecated(
        message = "Use streetlights.clear() instead.",
        replaceWith = ReplaceWith("streetlights.clear()")
    )
    fun clearStreetlights(): Pole {
        streetlights.clear()
        return this
    }

    // endregion

    // endregion
}

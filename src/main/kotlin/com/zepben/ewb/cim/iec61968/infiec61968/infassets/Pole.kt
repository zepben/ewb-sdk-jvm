/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassets

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.assets.Structure
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

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
    val streetlights: MridCollection<Streetlight> get() = LazyMridList(
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

    /**
     * Get the number of entries in the [Streetlight] collection.
     */
    @Deprecated(
        message = "Use streetlights.size instead.",
        replaceWith = ReplaceWith("streetlights.size")
    )
    fun numStreetlights(): Int = _streetlights?.size ?: 0

    /**
     * Get a [Streetlight] attached to this Pole by its mRID.
     *
     * @param mRID the mRID of the required [Streetlight]
     * @return The [Streetlight] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use streetlights.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("streetlights.getByMRID(mRID)")
    )
    fun getStreetlight(mRID: String): Streetlight? = _streetlights.getByMRID(mRID)

    /**
     * @param streetlight the [Streetlight] to associate with this [Pole].
     * @return A reference to this [Pole] to allow fluent use.
     */
    @Deprecated(
        message = "Use streetlights.add(streetlight) instead.",
        replaceWith = ReplaceWith("also { it.streetlights.add(streetlight) }")
    )
    fun addStreetlight(streetlight: Streetlight): Pole {
        if (validateReference(streetlight, ::getStreetlight, "A Streetlight"))
            return this

        _streetlights = _streetlights ?: mutableListOf()
        _streetlights!!.add(streetlight)

        return this
    }

    /**
     * @param streetlight The [Streetlight] to remove from this [Pole].
     * @return true if the streetlight is removed.
     */
    @Deprecated(
        message = "Use streetlights.remove(streetlight) instead.",
        replaceWith = ReplaceWith("streetlights.remove(streetlight)")
    )
    fun removeStreetlight(streetlight: Streetlight): Boolean {
        val ret = _streetlights.safeRemove(streetlight)
        if (_streetlights.isNullOrEmpty()) _streetlights = null
        return ret
    }

    /**
     * Clear all [Streetlight]s attached to this [Pole].
     */
    @Deprecated(
        message = "Use streetlights.clear() instead.",
        replaceWith = ReplaceWith("streetlights.clear()")
    )
    fun clearStreetlights(): Pole {
        _streetlights = null
        return this
    }

    // endregion

    // endregion
}

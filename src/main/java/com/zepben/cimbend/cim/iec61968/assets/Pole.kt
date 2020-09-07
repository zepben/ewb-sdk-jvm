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
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.safeRemove
import com.zepben.cimbend.common.extensions.validateReference

/**
 * A Pole asset
 */
class Pole(mRID: String = "") : Structure(mRID) {
    /**
     * Pole class: 1, 2, 3, 4, 5, 6, 7, H1, H2, Other, Unknown.
     */
    var classification: String = ""

    private var _streetlights: MutableList<Streetlight>? = null

    /**
     * All streetlights attached to this Pole. Collection is read only.
     */
    val streetlights: Collection<Streetlight> get() = _streetlights.asUnmodifiable()

    /**
     * Get the number of entries in the [Streetlight] collection.
     */
    fun numStreetlights() = _streetlights?.size ?: 0

    /**
     * Get a [Streetlight] attached to this Pole by its mRID.
     *
     * @param mRID the mRID of the required [Streetlight]
     * @return The [Streetlight] with the specified [mRID] if it exists, otherwise null
     */
    fun getStreetlight(mRID: String) = _streetlights.getByMRID(mRID)

    /**
     * @param streetlight the [Streetlight] to associate with this [Pole].
     * @return A reference to this [Pole] to allow fluent use.
     */
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
    fun removeStreetlight(streetlight: Streetlight?): Boolean {
        val ret = _streetlights.safeRemove(streetlight)
        if (_streetlights.isNullOrEmpty()) _streetlights = null
        return ret
    }

    /**
     * Clear all [Streetlight]s attached to this [Pole].
     */
    fun clearStreetlights(): Pole {
        _streetlights = null
        return this
    }

}

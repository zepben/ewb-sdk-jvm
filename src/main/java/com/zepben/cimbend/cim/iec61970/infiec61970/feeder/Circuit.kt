/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.infiec61970.feeder

import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.Line
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * <no description from CIM>
 *
 * @property loop The [Loop] containing this [Circuit].
 */
class Circuit @JvmOverloads constructor(mRID: String = "") : Line(mRID) {

    var loop: Loop? = null

    private var _endTerminals: MutableList<Terminal>? = null
    private var _endSubstations: MutableList<Substation>? = null

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val endTerminals: List<Terminal> get() = _endTerminals.asUnmodifiable()

    /**
     * Get the number of entries in the [endTerminals] collection.
     */
    fun numEndTerminals() = _endTerminals?.size ?: 0

    /**
     * Retrieve a [Terminal] from the [endTerminals] collection.
     *
     * @param mRID the mRID of the required [Terminal]
     * @return The [Terminal] with the specified [mRID] if it exists, otherwise null
     */
    fun getEndTerminal(mRID: String) = _endTerminals.getByMRID(mRID)

    /**
     * @param endTerminal the [Terminal] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
    fun addEndTerminal(endTerminal: Terminal): Circuit {
        if (validateReference(endTerminal, ::getEndTerminal, "A Terminal"))
            return this

        _endTerminals = _endTerminals ?: mutableListOf()
        _endTerminals!!.add(endTerminal)

        return this
    }

    /**
     * @param endTerminal the [Terminal] to disassociate with this [Circuit].
     * @return `true` if [endTerminal] has been successfully removed; `false` if it was not present.
     */
    fun removeEndTerminal(endTerminal: Terminal?): Boolean {
        val ret = _endTerminals?.remove(endTerminal) == true
        if (_endTerminals.isNullOrEmpty()) _endTerminals = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endTerminals].
     * @return this [Circuit]
     */
    fun clearEndTerminals(): Circuit {
        _endTerminals = null
        return this
    }

    /**
     * Simplification of the CIM association via Bay to [Substation].
     * The returned collection is read only.
     */
    val endSubstations: List<Substation> get() = _endSubstations.asUnmodifiable()

    /**
     * Get the number of entries in the [endSubstations] collection.
     */
    fun numEndSubstations() = _endSubstations?.size ?: 0

    /**
     * Retrieve a [Substation] that is associated with this [Circuit].
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    fun getEndSubstation(mRID: String) = _endSubstations.getByMRID(mRID)

    /**
     * @param substation the [Substation] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
    fun addEndSubstation(substation: Substation): Circuit {
        if (validateReference(substation, ::getEndSubstation, "A Substation"))
            return this

        _endSubstations = _endSubstations ?: mutableListOf()
        _endSubstations!!.add(substation)

        return this
    }

    /**
     * @param substation the [Substation] to disassociate with this [Circuit].
     * @return `true` if [substation] has been successfully removed; `false` if it was not present.
     */
    fun removeEndSubstation(substation: Substation?): Boolean {
        val ret = _endSubstations?.remove(substation) == true
        if (_endSubstations.isNullOrEmpty()) _endSubstations = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endSubstations].
     * @return this [Circuit]
     */
    fun clearEndSubstations(): Circuit {
        _endSubstations = null
        return this
    }
}

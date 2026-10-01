/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.feeder

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridList
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Line
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * <no description from CIM>
 *
 * @property loop The [Loop] containing this [Circuit].
 */
class Circuit(mRID: String) : Line(mRID) {

    var loop: Loop? = null

    private var _endTerminals: MutableList<Terminal>? = null
    private var _endSubstations: MutableList<Substation>? = null

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val endTerminals: MridList<Terminal> get() = LazyMridList(
        getter = { _endTerminals },
        setter = { _endTerminals = it },
        owner = this,
        elementDescription = "A Terminal"
    )

    /**
     * Simplification of the CIM association via Bay to [Substation].
     * The returned collection is read only.
     */
    val endSubstations: MridList<Substation> get() = LazyMridList(
        getter = { _endSubstations },
        setter = { _endSubstations = it },
        owner = this,
        elementDescription = "A Substation"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region endTerminals boilerplate

    /**
     * Get the number of entries in the [endTerminals] collection.
     */
    @Deprecated(
        message = "Use endTerminals.size instead.",
        replaceWith = ReplaceWith("endTerminals.size")
    )
    fun numEndTerminals(): Int = _endTerminals?.size ?: 0

    /**
     * Retrieve a [Terminal] from the [endTerminals] collection.
     *
     * @param mRID the mRID of the required [Terminal]
     * @return The [Terminal] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use endTerminals.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("endTerminals.getByMRID(mRID)")
    )
    fun getEndTerminal(mRID: String): Terminal? = _endTerminals.getByMRID(mRID)

    /**
     * @param endTerminal the [Terminal] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
    @Deprecated(
        message = "Use endTerminals.add(endTerminal) instead.",
        replaceWith = ReplaceWith("also { it.endTerminals.add(endTerminal) }")
    )
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
    @Deprecated(
        message = "Use endTerminals.remove(endTerminal) instead.",
        replaceWith = ReplaceWith("endTerminals.remove(endTerminal)")
    )
    fun removeEndTerminal(endTerminal: Terminal): Boolean {
        val ret = _endTerminals?.remove(endTerminal) == true
        if (_endTerminals.isNullOrEmpty()) _endTerminals = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endTerminals].
     * @return this [Circuit]
     */
    @Deprecated(
        message = "Use endTerminals.clear() instead.",
        replaceWith = ReplaceWith("endTerminals.clear()")
    )
    fun clearEndTerminals(): Circuit {
        _endTerminals = null
        return this
    }

    // endregion

    // region endSubstations boilerplate

    /**
     * Get the number of entries in the [endSubstations] collection.
     */
    @Deprecated(
        message = "Use endSubstations.size instead.",
        replaceWith = ReplaceWith("endSubstations.size")
    )
    fun numEndSubstations(): Int = _endSubstations?.size ?: 0

    /**
     * Retrieve a [Substation] that is associated with this [Circuit].
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use endSubstations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("endSubstations.getByMRID(mRID)")
    )
    fun getEndSubstation(mRID: String): Substation? = _endSubstations.getByMRID(mRID)

    /**
     * @param substation the [Substation] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
    @Deprecated(
        message = "Use endSubstations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.endSubstations.add(substation) }")
    )
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
    @Deprecated(
        message = "Use endSubstations.remove(substation) instead.",
        replaceWith = ReplaceWith("endSubstations.remove(substation)")
    )
    fun removeEndSubstation(substation: Substation): Boolean {
        val ret = _endSubstations?.remove(substation) == true
        if (_endSubstations.isNullOrEmpty()) _endSubstations = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endSubstations].
     * @return this [Circuit]
     */
    @Deprecated(
        message = "Use endSubstations.clear() instead.",
        replaceWith = ReplaceWith("endSubstations.clear()")
    )
    fun clearEndSubstations(): Circuit {
        _endSubstations = null
        return this
    }

    // endregion

    // endregion
}

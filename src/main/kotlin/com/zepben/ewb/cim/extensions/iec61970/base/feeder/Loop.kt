/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX]
 * Sub-transmission circuits are usually arranged in loops so that a single line failure does not cut off service
 * to many customers for more than a short time.
 */
@ZBEX
class Loop(mRID: String) : IdentifiedObject(mRID) {

    private var _circuits: MutableList<Circuit>? = null
    private var _substations: MutableList<Substation>? = null
    private var _energizingSubstations: MutableList<Substation>? = null

    /**
     * [ZBEX] Sub-transmission circuits that form part of this loop.
     * The returned collection is read only.
     */
    @ZBEX
    val circuits: MridList<Circuit> get() = LazyMridList(
        getter = { _circuits },
        setter = { _circuits = it },
        owner = this,
        elementDescription = "A Circuit",
    )

    /**
     * [ZBEX] [Substation]s that are powered by this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val substations: MridList<Substation> get() = LazyMridList(
        getter = { _substations },
        setter = { _substations = it },
        owner = this,
        elementDescription = "A Substation",
    )

    /**
     * [ZBEX] The [Substation]s that normally energize this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val energizingSubstations: MridList<Substation> get() = LazyMridList(
        getter = { _energizingSubstations },
        setter = { _energizingSubstations = it },
        owner = this,
        elementDescription = "A Substation",
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region circuits boilerplate

    /**
     * Get the number of entries in the [circuits] collection.
     */
    @Deprecated(
        message = "Use circuits.size instead.",
        replaceWith = ReplaceWith("circuits.size")
    )
    fun numCircuits(): Int = _circuits?.size ?: 0

    /**
     * Retrieve a [Circuit] from the [circuits] collection.
     *
     * @param mRID the mRID of the required [Circuit]
     * @return The [Circuit] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use circuits.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("circuits.getByMRID(mRID)")
    )
    fun getCircuit(mRID: String): Circuit? = _circuits.getByMRID(mRID)

    /**
     * @param circuit the [Circuit] to associate with this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
    @Deprecated(
        message = "Use circuits.add(circuit) instead.",
        replaceWith = ReplaceWith("also { it.circuits.add(circuit) }")
    )
    fun addCircuit(circuit: Circuit): Loop {
        if (validateReference(circuit, ::getCircuit, "A Circuit"))
            return this

        _circuits = _circuits ?: mutableListOf()
        _circuits!!.add(circuit)

        return this
    }

    /**
     * @param circuit the [Circuit] to disassociate with this [Loop].
     * @return `true` if [circuit] has been successfully removed; `false` if it was not present.
     */
    @Deprecated(
        message = "Use circuits.remove(circuit) instead.",
        replaceWith = ReplaceWith("circuits.remove(circuit)")
    )
    fun removeCircuit(circuit: Circuit): Boolean {
        val ret = _circuits?.remove(circuit) == true
        if (_circuits.isNullOrEmpty()) _circuits = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [circuits].
     * @return this [Loop]
     */
    @Deprecated(
        message = "Use circuits.clear() instead.",
        replaceWith = ReplaceWith("circuits.clear()")
    )
    fun clearCircuits(): Loop {
        _circuits = null
        return this
    }

    // endregion

    // region substations boilerplate

    /**
     * Get the number of entries in the [substations] collection.
     */
    @Deprecated(
        message = "Use substations.size instead.",
        replaceWith = ReplaceWith("substations.size")
    )
    fun numSubstations(): Int = _substations?.size ?: 0

    /**
     * Retrieve a [Substation] that is powered by this [Loop].
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
     * @param substation the [Substation] that is powered by this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
    @Deprecated(
        message = "Use substations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.substations.add(substation) }")
    )
    fun addSubstation(substation: Substation): Loop {
        if (validateReference(substation, ::getSubstation, "A Substation"))
            return this

        _substations = _substations ?: mutableListOf()
        _substations!!.add(substation)

        return this
    }

    /**
     * @param substation the [Substation] no longer powered by this [Loop].
     * @return `true` if [substation] has been successfully removed; `false` if it was not present.
     */
    @Deprecated(
        message = "Use substations.remove(substation) instead.",
        replaceWith = ReplaceWith("substations.remove(substation)")
    )
    fun removeSubstation(substation: Substation): Boolean {
        val ret = _substations?.remove(substation) == true
        if (_substations.isNullOrEmpty()) _substations = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [substations].
     * @return this [Loop]
     */
    @Deprecated(
        message = "Use substations.clear() instead.",
        replaceWith = ReplaceWith("substations.clear()")
    )
    fun clearSubstations(): Loop {
        _substations = null
        return this
    }

    // endregion

    // region energizingSubstations boilerplate

    /**
     * Get the number of entries in the [energizingSubstations] collection.
     */
    @Deprecated(
        message = "Use energizingSubstations.size instead.",
        replaceWith = ReplaceWith("energizingSubstations.size")
    )
    fun numEnergizingSubstations(): Int = _energizingSubstations?.size ?: 0

    /**
     * Retrieve a [Substation] that is energizing this [Loop].
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use energizingSubstations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("energizingSubstations.getByMRID(mRID)")
    )
    fun getEnergizingSubstation(mRID: String): Substation? = _energizingSubstations.getByMRID(mRID)

    /**
     * @param substation the [Substation] that energizing this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
    @Deprecated(
        message = "Use energizingSubstations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.energizingSubstations.add(substation) }")
    )
    fun addEnergizingSubstation(substation: Substation): Loop {
        if (validateReference(substation, ::getEnergizingSubstation, "A Substation"))
            return this

        _energizingSubstations = _energizingSubstations ?: mutableListOf()
        _energizingSubstations!!.add(substation)

        return this
    }

    /**
     * @param substation the [Substation] that is no longer energizing this [Loop].
     * @return `true` if [substation] has been successfully removed; `false` if it was not present.
     */
    @Deprecated(
        message = "Use energizingSubstations.remove(substation) instead.",
        replaceWith = ReplaceWith("energizingSubstations.remove(substation)")
    )
    fun removeEnergizingSubstation(substation: Substation): Boolean {
        val ret = _energizingSubstations?.remove(substation) == true
        if (_energizingSubstations.isNullOrEmpty()) _energizingSubstations = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [energizingSubstations].
     * @return this [Loop]
     */
    @Deprecated(
        message = "Use energizingSubstations.clear() instead.",
        replaceWith = ReplaceWith("energizingSubstations.clear()")
    )
    fun clearEnergizingSubstations(): Loop {
        _energizingSubstations = null
        return this
    }

    // endregion

    // endregion
}

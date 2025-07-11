/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX]
 * Sub-transmission circuits are usually arranged in loops so that a single line failure does not cut off service
 * to many customers for more than a short time.
 */
@ZBEX
class Loop @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private var _circuits: MutableList<Circuit>? = null
    private var _substations: MutableList<Substation>? = null
    private var _energizingSubstations: MutableList<Substation>? = null

    /**
     * [ZBEX] Sub-transmission circuits that form part of this loop.
     * The returned collection is read only.
     */
    @ZBEX
    val circuits: List<Circuit> get() = _circuits.asUnmodifiable()

    /**
     * Get the number of entries in the [circuits] collection.
     */
    fun numCircuits(): Int = _circuits?.size ?: 0

    /**
     * Retrieve a [Circuit] from the [circuits] collection.
     *
     * @param mRID the mRID of the required [Circuit]
     * @return The [Circuit] with the specified [mRID] if it exists, otherwise null
     */
    fun getCircuit(mRID: String): Circuit? = _circuits.getByMRID(mRID)

    /**
     * @param circuit the [Circuit] to associate with this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
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
    fun removeCircuit(circuit: Circuit): Boolean {
        val ret = _circuits?.remove(circuit) == true
        if (_circuits.isNullOrEmpty()) _circuits = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [circuits].
     * @return this [Loop]
     */
    fun clearCircuits(): Loop {
        _circuits = null
        return this
    }

    /**
     * [ZBEX] [Substation]s that are powered by this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val substations: List<Substation> get() = _substations.asUnmodifiable()

    /**
     * Get the number of entries in the [substations] collection.
     */
    fun numSubstations(): Int = _substations?.size ?: 0

    /**
     * Retrieve a [Substation] that is powered by this [Loop].
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    fun getSubstation(mRID: String): Substation? = _substations.getByMRID(mRID)

    /**
     * @param substation the [Substation] that is powered by this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
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
    fun removeSubstation(substation: Substation): Boolean {
        val ret = _substations?.remove(substation) == true
        if (_substations.isNullOrEmpty()) _substations = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [substations].
     * @return this [Loop]
     */
    fun clearSubstations(): Loop {
        _substations = null
        return this
    }

    /**
     * [ZBEX] The [Substation]s that normally energize this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val energizingSubstations: List<Substation> get() = _energizingSubstations.asUnmodifiable()

    /**
     * Get the number of entries in the [energizingSubstations] collection.
     */
    fun numEnergizingSubstations(): Int = _energizingSubstations?.size ?: 0

    /**
     * Retrieve a [Substation] that is energizing this [Loop].
     *
     * @param mRID the mRID of the required [Substation]
     * @return The [Substation] with the specified [mRID] if it exists, otherwise null
     */
    fun getEnergizingSubstation(mRID: String): Substation? = _energizingSubstations.getByMRID(mRID)

    /**
     * @param substation the [Substation] that energizing this [Loop].
     * @return A reference to this [Loop] to allow fluent use.
     */
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
    fun removeEnergizingSubstation(substation: Substation): Boolean {
        val ret = _energizingSubstations?.remove(substation) == true
        if (_energizingSubstations.isNullOrEmpty()) _energizingSubstations = null
        return ret
    }

    /**
     * Clear this [Loop]'s associated [energizingSubstations].
     * @return this [Loop]
     */
    fun clearEnergizingSubstations(): Loop {
        _energizingSubstations = null
        return this
    }

}

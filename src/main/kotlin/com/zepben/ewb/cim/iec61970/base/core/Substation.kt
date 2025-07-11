/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.extensions.*

/**
 * A collection of equipment for purposes other than generation or utilization, through which electric energy in bulk
 * is passed for the purposes of switching or modifying its characteristics.
 *
 * @property subGeographicalRegion The SubGeographicalRegion containing the substation.
 */
class Substation @JvmOverloads constructor(mRID: String = "") : EquipmentContainer(mRID) {

    var subGeographicalRegion: SubGeographicalRegion? = null
    private var _normalEnergizedFeeders: MutableList<Feeder>? = null
    private var _loops: MutableList<Loop>? = null
    private var _energizedLoops: MutableList<Loop>? = null
    private var _circuits: MutableList<Circuit>? = null

    /**
     * The normal energized feeders of the substation. Also used for naming purposes. The returned collection is read only.
     */
    val feeders: Collection<Feeder> get() = _normalEnergizedFeeders.asUnmodifiable()

    /**
     * Get the number of entries in the [Feeder] collection.
     */
    fun numFeeders(): Int = _normalEnergizedFeeders?.size ?: 0

    /**
     * The normal energized feeders of the substation. Also used for naming purposes.
     *
     * @param mRID the mRID of the required [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getFeeder(mRID: String): Feeder? = _normalEnergizedFeeders.getByMRID(mRID)

    /**
     * @param feeder the [Feeder] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    fun addFeeder(feeder: Feeder): Substation {
        if (validateReference(feeder, ::getFeeder, "A Feeder"))
            return this

        if (feeder.normalEnergizingSubstation == null)
            feeder.normalEnergizingSubstation = this

        require(feeder.normalEnergizingSubstation === this) {
            "${feeder.typeNameAndMRID()} `normalEnergizingSubstation` property references ${feeder.normalEnergizingSubstation!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _normalEnergizedFeeders = _normalEnergizedFeeders ?: mutableListOf()
        _normalEnergizedFeeders!!.add(feeder)

        return this
    }

    /**
     * @param feeder the [Feeder] to disassociate with this [Substation].
     * @return true if the feeder is disassociated.
     */
    fun removeFeeder(feeder: Feeder): Boolean {
        val ret = _normalEnergizedFeeders.safeRemove(feeder)
        if (_normalEnergizedFeeders.isNullOrEmpty()) _normalEnergizedFeeders = null
        return ret
    }

    /**
     * Clear this [Substation]'s [Feeder]'s
     * @return this [Substation]
     */
    fun clearFeeders(): Substation {
        _normalEnergizedFeeders = null
        return this
    }

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val loops: List<Loop> get() = _loops.asUnmodifiable()

    /**
     * Get the number of entries in the [loops] collection.
     */
    fun numLoops(): Int = _loops?.size ?: 0

    /**
     * Retrieve a [Loop] from the [loops] collection.
     *
     * @param mRID the mRID of the required [Loop]
     * @return The [Loop] with the specified [mRID] if it exists, otherwise null
     */
    fun getLoop(mRID: String): Loop? = _loops.getByMRID(mRID)

    /**
     * @param loop the [Loop] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    fun addLoop(loop: Loop): Substation {
        if (validateReference(loop, ::getLoop, "A Loop"))
            return this

        _loops = _loops ?: mutableListOf()
        _loops!!.add(loop)

        return this
    }

    /**
     * @param loop the [Loop] to disassociate with this [Substation].
     * @return `true` if [loop] has been successfully removed; `false` if it was not present.
     */
    fun removeLoop(loop: Loop): Boolean {
        val ret = _loops?.remove(loop) == true
        if (_loops.isNullOrEmpty()) _loops = null
        return ret
    }

    /**
     * Clear this [Substation]'s associated [loops].
     * @return this [Substation]
     */
    fun clearLoops(): Substation {
        _loops = null
        return this
    }

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val energizedLoops: List<Loop> get() = _energizedLoops.asUnmodifiable()

    /**
     * Get the number of entries in the [energizedLoops] collection.
     */
    fun numEnergizedLoops(): Int = _energizedLoops?.size ?: 0

    /**
     * Retrieve a [Loop] from the [energizedLoops] collection.
     *
     * @param mRID the mRID of the required [Loop]
     * @return The [Loop] with the specified [mRID] if it exists, otherwise null
     */
    fun getEnergizedLoop(mRID: String): Loop? = _energizedLoops.getByMRID(mRID)

    /**
     * @param loop the [Loop] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    fun addEnergizedLoop(loop: Loop): Substation {
        if (validateReference(loop, ::getEnergizedLoop, "A Loop"))
            return this

        _energizedLoops = _energizedLoops ?: mutableListOf()
        _energizedLoops!!.add(loop)

        return this
    }

    /**
     * @param loop the [Loop] to disassociate with this [Substation].
     * @return `true` if [loop] has been successfully removed; `false` if it was not present.
     */
    fun removeEnergizedLoop(loop: Loop): Boolean {
        val ret = _energizedLoops?.remove(loop) == true
        if (_energizedLoops.isNullOrEmpty()) _energizedLoops = null
        return ret
    }

    /**
     * Clear this [Substation]'s associated [energizedLoops].
     * @return this [Substation]
     */
    fun clearEnergizedLoops(): Substation {
        _energizedLoops = null
        return this
    }

    /**
     * Simplification of the CIM association via Bay to [Circuit].
     * The returned collection is read only.
     */
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
     * @param circuit the [Circuit] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    fun addCircuit(circuit: Circuit): Substation {
        if (validateReference(circuit, ::getCircuit, "A Circuit"))
            return this

        _circuits = _circuits ?: mutableListOf()
        _circuits!!.add(circuit)

        return this
    }

    /**
     * @param circuit the [Circuit] to disassociate with this [Substation].
     * @return `true` if [circuit] has been successfully removed; `false` if it was not present.
     */
    fun removeCircuit(circuit: Circuit): Boolean {
        val ret = _circuits?.remove(circuit) == true
        if (_circuits.isNullOrEmpty()) _circuits = null
        return ret
    }

    /**
     * Clear this [Substation]'s associated [circuits].
     * @return this [Substation]
     */
    fun clearCircuits(): Substation {
        _circuits = null
        return this
    }
}

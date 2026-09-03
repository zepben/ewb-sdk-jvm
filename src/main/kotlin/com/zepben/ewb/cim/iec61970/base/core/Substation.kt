/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.boilerplate.collections.interfaces.MridList
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A collection of equipment for purposes other than generation or utilization, through which electric energy in bulk
 * is passed for the purposes of switching or modifying its characteristics.
 *
 * @property subGeographicalRegion The SubGeographicalRegion containing the substation.
 */
class Substation(mRID: String) : EquipmentContainer(mRID) {

    var subGeographicalRegion: SubGeographicalRegion? = null
    private var _normalEnergizedFeeders: MutableList<Feeder>? = null
    private var _loops: MutableList<Loop>? = null
    private var _energizedLoops: MutableList<Loop>? = null
    private var _circuits: MutableList<Circuit>? = null

    /**
     * The normal energized feeders of the substation. Also used for naming purposes. The returned collection is read only.
     */
    val feeders: MridCollection<Feeder> get() = LazyMridList(
        getter = { _normalEnergizedFeeders },
        setter = { _normalEnergizedFeeders = it },
        owner = this,
        elementDescription = "A Feeder",
        backfill = Backfill(
            { it.normalEnergizingSubstation },
            { it, other -> it.normalEnergizingSubstation = other },
            Feeder::normalEnergizingSubstation,
        ),
    )

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val loops: MridList<Loop> get() = LazyMridList(
        getter = { _loops },
        setter = { _loops = it },
        owner = this,
        elementDescription = "A Loop"
    )

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val energizedLoops: MridList<Loop> get() = LazyMridList(
        getter = { _energizedLoops },
        setter = { _energizedLoops = it },
        owner = this,
        elementDescription = "A Loop"
    )

    /**
     * Simplification of the CIM association via Bay to [Circuit].
     * The returned collection is read only.
     */
    val circuits: MridList<Circuit> get() = LazyMridList(
        getter = { _circuits },
        setter = { _circuits = it },
        owner = this,
        elementDescription = "A Circuit"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region feeders boilerplate

    /**
     * Get the number of entries in the [Feeder] collection.
     */
    @Deprecated(
        message = "Use feeders.size instead.",
        replaceWith = ReplaceWith("feeders.size")
    )
    fun numFeeders(): Int = _normalEnergizedFeeders?.size ?: 0

    /**
     * The normal energized feeders of the substation. Also used for naming purposes.
     *
     * @param mRID the mRID of the required [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use feeders.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("feeders.getByMRID(mRID)")
    )
    fun getFeeder(mRID: String): Feeder? = _normalEnergizedFeeders.getByMRID(mRID)

    /**
     * @param feeder the [Feeder] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    @Deprecated(
        message = "Use feeders.add(feeder) instead.",
        replaceWith = ReplaceWith("also { it.feeders.add(feeder) }")
    )
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
    @Deprecated(
        message = "Use feeders.remove(feeder) instead.",
        replaceWith = ReplaceWith("feeders.remove(feeder)")
    )
    fun removeFeeder(feeder: Feeder): Boolean {
        val ret = _normalEnergizedFeeders.safeRemove(feeder)
        if (_normalEnergizedFeeders.isNullOrEmpty()) _normalEnergizedFeeders = null
        return ret
    }

    /**
     * Clear this [Substation]'s [Feeder]'s
     * @return this [Substation]
     */
    @Deprecated(
        message = "Use feeders.clear() instead.",
        replaceWith = ReplaceWith("feeders.clear()")
    )
    fun clearFeeders(): Substation {
        _normalEnergizedFeeders = null
        return this
    }

    // endregion

    // region loops boilerplate

    /**
     * Get the number of entries in the [loops] collection.
     */
    @Deprecated(
        message = "Use loops.size instead.",
        replaceWith = ReplaceWith("loops.size")
    )
    fun numLoops(): Int = _loops?.size ?: 0

    /**
     * Retrieve a [Loop] from the [loops] collection.
     *
     * @param mRID the mRID of the required [Loop]
     * @return The [Loop] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use loops.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("loops.getByMRID(mRID)")
    )
    fun getLoop(mRID: String): Loop? = _loops.getByMRID(mRID)

    /**
     * @param loop the [Loop] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    @Deprecated(
        message = "Use loops.add(loop) instead.",
        replaceWith = ReplaceWith("also { it.loops.add(loop) }")
    )
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
    @Deprecated(
        message = "Use loops.remove(loop) instead.",
        replaceWith = ReplaceWith("loops.remove(loop)")
    )
    fun removeLoop(loop: Loop): Boolean {
        val ret = _loops?.remove(loop) == true
        if (_loops.isNullOrEmpty()) _loops = null
        return ret
    }

    /**
     * Clear this [Substation]'s associated [loops].
     * @return this [Substation]
     */
    @Deprecated(
        message = "Use loops.clear() instead.",
        replaceWith = ReplaceWith("loops.clear()")
    )
    fun clearLoops(): Substation {
        _loops = null
        return this
    }

    // endregion

    // region energizedLoops boilerplate

    /**
     * Get the number of entries in the [energizedLoops] collection.
     */
    @Deprecated(
        message = "Use energizedLoops.size instead.",
        replaceWith = ReplaceWith("energizedLoops.size")
    )
    fun numEnergizedLoops(): Int = _energizedLoops?.size ?: 0

    /**
     * Retrieve a [Loop] from the [energizedLoops] collection.
     *
     * @param mRID the mRID of the required [Loop]
     * @return The [Loop] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use energizedLoops.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("energizedLoops.getByMRID(mRID)")
    )
    fun getEnergizedLoop(mRID: String): Loop? = _energizedLoops.getByMRID(mRID)

    /**
     * @param loop the [Loop] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    @Deprecated(
        message = "Use energizedLoops.add(loop) instead.",
        replaceWith = ReplaceWith("also { it.energizedLoops.add(loop) }")
    )
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
    @Deprecated(
        message = "Use energizedLoops.remove(loop) instead.",
        replaceWith = ReplaceWith("energizedLoops.remove(loop)")
    )
    fun removeEnergizedLoop(loop: Loop): Boolean {
        val ret = _energizedLoops?.remove(loop) == true
        if (_energizedLoops.isNullOrEmpty()) _energizedLoops = null
        return ret
    }

    /**
     * Clear this [Substation]'s associated [energizedLoops].
     * @return this [Substation]
     */
    @Deprecated(
        message = "Use energizedLoops.clear() instead.",
        replaceWith = ReplaceWith("energizedLoops.clear()")
    )
    fun clearEnergizedLoops(): Substation {
        _energizedLoops = null
        return this
    }

    // endregion

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
     * @param circuit the [Circuit] to associate with this [Substation].
     * @return A reference to this [Substation] to allow fluent use.
     */
    @Deprecated(
        message = "Use circuits.add(circuit) instead.",
        replaceWith = ReplaceWith("also { it.circuits.add(circuit) }")
    )
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
     * Clear this [Substation]'s associated [circuits].
     * @return this [Substation]
     */
    @Deprecated(
        message = "Use circuits.clear() instead.",
        replaceWith = ReplaceWith("circuits.clear()")
    )
    fun clearCircuits(): Substation {
        _circuits = null
        return this
    }

    // endregion

    // endregion
}

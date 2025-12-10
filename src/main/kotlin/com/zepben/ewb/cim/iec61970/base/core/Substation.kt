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
import com.zepben.ewb.testing.MRIDListWrapper

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
    val feeders: MRIDListWrapper<Feeder>
        get() = MRIDListWrapper(
            getter = { _normalEnergizedFeeders },
            setter = { _normalEnergizedFeeders = it })

    @Deprecated("BOILERPLATE: Use feeders.size instead")
    fun numFeeders(): Int = feeders.size

    @Deprecated("BOILERPLATE: Use normalEnergizedFeeders.getByMRID(mRID) instead")
    fun getFeeder(mRID: String): Feeder? = feeders.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use feeders.remove(feeder) instead")
    fun removeFeeder(feeder: Feeder): Boolean = feeders.remove(feeder)

    @Deprecated("BOILERPLATE: Use feeders.clear() instead")
    fun clearFeeders(): Substation {
        feeders.clear()
        return this
    }

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val loops: MRIDListWrapper<Loop>
        get() = MRIDListWrapper(
            getter = { _loops },
            setter = { _loops = it })

    @Deprecated("BOILERPLATE: Use loops.size instead")
    fun numLoops(): Int = loops.size

    @Deprecated("BOILERPLATE: Use loops.getByMRID(mRID) instead")
    fun getLoop(mRID: String): Loop? = loops.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use loops.remove(loop) instead")
    fun removeLoop(loop: Loop): Boolean = loops.remove(loop)

    @Deprecated("BOILERPLATE: Use loops.clear() instead")
    fun clearLoops(): Substation {
        loops.clear()
        return this
    }

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val energizedLoops: MRIDListWrapper<Loop>
        get() = MRIDListWrapper(
            getter = { _energizedLoops },
            setter = { _energizedLoops = it })

    @Deprecated("BOILERPLATE: Use energizedLoops.size instead")
    fun numEnergizedLoops(): Int = energizedLoops.size

    @Deprecated("BOILERPLATE: Use energizedLoops.getByMRID(mRID) instead")
    fun getEnergizedLoop(mRID: String): Loop? = energizedLoops.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use energizedLoops.remove(loop) instead")
    fun removeEnergizedLoop(loop: Loop): Boolean = energizedLoops.remove(loop)

    @Deprecated("BOILERPLATE: Use energizedLoops.clear() instead")
    fun clearEnergizedLoops(): Substation {
        energizedLoops.clear()
        return this
    }

    /**
     * Simplification of the CIM association via Bay to [Circuit].
     * The returned collection is read only.
     */
    val circuits: MRIDListWrapper<Circuit>
        get() = MRIDListWrapper(
            getter = { _circuits },
            setter = { _circuits = it })

    @Deprecated("BOILERPLATE: Use circuits.size instead")
    fun numCircuits(): Int = circuits.size

    @Deprecated("BOILERPLATE: Use circuits.getByMRID(mRID) instead")
    fun getCircuit(mRID: String): Circuit? = circuits.getByMRID(mRID)

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

    @Deprecated("BOILERPLATE: Use circuits.remove(circuit) instead")
    fun removeCircuit(circuit: Circuit): Boolean = circuits.remove(circuit)

    @Deprecated("BOILERPLATE: Use circuits.clear() instead")
    fun clearCircuits(): Substation {
        circuits.clear()
        return this
    }
}

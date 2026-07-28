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
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit

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
    val loops: MridCollection<Loop> get() = LazyMridList(
        getter = { _loops },
        setter = { _loops = it },
        owner = this,
        elementDescription = "A Loop"
    )

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val energizedLoops: MridCollection<Loop> get() = LazyMridList(
        getter = { _energizedLoops },
        setter = { _energizedLoops = it },
        owner = this,
        elementDescription = "A Loop"
    )

    /**
     * Simplification of the CIM association via Bay to [Circuit].
     * The returned collection is read only.
     */
    val circuits: MridCollection<Circuit> get() = LazyMridList(
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

    @Deprecated(
        message = "Use feeders.size instead.",
        replaceWith = ReplaceWith("feeders.size")
    )
    fun numFeeders(): Int = feeders.size

    @Deprecated(
        message = "Use feeders.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("feeders.getByMRID(mRID)")
    )
    fun getFeeder(mRID: String): Feeder? = feeders.getByMrid(mRID)

    @Deprecated(
        message = "Use feeders.add(feeder) instead.",
        replaceWith = ReplaceWith("also { it.feeders.add(feeder) }")
    )
    fun addFeeder(feeder: Feeder): Substation {
        feeders.add(feeder)
        return this
    }

    @Deprecated(
        message = "Use feeders.remove(feeder) instead.",
        replaceWith = ReplaceWith("feeders.remove(feeder)")
    )
    fun removeFeeder(feeder: Feeder): Boolean = feeders.remove(feeder)

    @Deprecated(
        message = "Use feeders.clear() instead.",
        replaceWith = ReplaceWith("feeders.clear()")
    )
    fun clearFeeders(): Substation {
        feeders.clear()
        return this
    }

    // endregion

    // region loops boilerplate

    @Deprecated(
        message = "Use loops.size instead.",
        replaceWith = ReplaceWith("loops.size")
    )
    fun numLoops(): Int = loops.size

    @Deprecated(
        message = "Use loops.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("loops.getByMRID(mRID)")
    )
    fun getLoop(mRID: String): Loop? = loops.getByMrid(mRID)

    @Deprecated(
        message = "Use loops.add(loop) instead.",
        replaceWith = ReplaceWith("also { it.loops.add(loop) }")
    )
    fun addLoop(loop: Loop): Substation {
        loops.add(loop)
        return this
    }

    @Deprecated(
        message = "Use loops.remove(loop) instead.",
        replaceWith = ReplaceWith("loops.remove(loop)")
    )
    fun removeLoop(loop: Loop): Boolean = loops.remove(loop)

    @Deprecated(
        message = "Use loops.clear() instead.",
        replaceWith = ReplaceWith("loops.clear()")
    )
    fun clearLoops(): Substation {
        loops.clear()
        return this
    }

    // endregion

    // region energizedLoops boilerplate

    @Deprecated(
        message = "Use energizedLoops.size instead.",
        replaceWith = ReplaceWith("energizedLoops.size")
    )
    fun numEnergizedLoops(): Int = energizedLoops.size

    @Deprecated(
        message = "Use energizedLoops.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("energizedLoops.getByMRID(mRID)")
    )
    fun getEnergizedLoop(mRID: String): Loop? = energizedLoops.getByMrid(mRID)

    @Deprecated(
        message = "Use energizedLoops.add(loop) instead.",
        replaceWith = ReplaceWith("also { it.energizedLoops.add(loop) }")
    )
    fun addEnergizedLoop(loop: Loop): Substation {
        energizedLoops.add(loop)
        return this
    }

    @Deprecated(
        message = "Use energizedLoops.remove(loop) instead.",
        replaceWith = ReplaceWith("energizedLoops.remove(loop)")
    )
    fun removeEnergizedLoop(loop: Loop): Boolean = energizedLoops.remove(loop)

    @Deprecated(
        message = "Use energizedLoops.clear() instead.",
        replaceWith = ReplaceWith("energizedLoops.clear()")
    )
    fun clearEnergizedLoops(): Substation {
        energizedLoops.clear()
        return this
    }

    // endregion

    // region circuits boilerplate

    @Deprecated(
        message = "Use circuits.size instead.",
        replaceWith = ReplaceWith("circuits.size")
    )
    fun numCircuits(): Int = circuits.size

    @Deprecated(
        message = "Use circuits.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("circuits.getByMRID(mRID)")
    )
    fun getCircuit(mRID: String): Circuit? = circuits.getByMrid(mRID)

    @Deprecated(
        message = "Use circuits.add(circuit) instead.",
        replaceWith = ReplaceWith("also { it.circuits.add(circuit) }")
    )
    fun addCircuit(circuit: Circuit): Substation {
        circuits.add(circuit)
        return this
    }

    @Deprecated(
        message = "Use circuits.remove(circuit) instead.",
        replaceWith = ReplaceWith("circuits.remove(circuit)")
    )
    fun removeCircuit(circuit: Circuit): Boolean = circuits.remove(circuit)

    @Deprecated(
        message = "Use circuits.clear() instead.",
        replaceWith = ReplaceWith("circuits.clear()")
    )
    fun clearCircuits(): Substation {
        circuits.clear()
        return this
    }

    // endregion

    // endregion
}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit

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
    val circuits: LazyMridList<Circuit> get() = LazyMridList(
        getter = { _circuits },
        setter = { _circuits = it },
        owner = { this },
        elementDescription = "A Circuit",
    )

    /**
     * [ZBEX] [Substation]s that are powered by this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val substations: LazyMridList<Substation> get() = LazyMridList(
        getter = { _substations },
        setter = { _substations = it },
        owner = { this },
        elementDescription = "A Substation",
    )

    /**
     * [ZBEX] The [Substation]s that normally energize this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val energizingSubstations: LazyMridList<Substation> get() = LazyMridList(
        getter = { _energizingSubstations },
        setter = { _energizingSubstations = it },
        owner = { this },
        elementDescription = "A Substation",
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

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
    fun addCircuit(circuit: Circuit): Loop {
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
    fun clearCircuits(): Loop {
        circuits.clear()
        return this
    }

    // endregion

    // region substations boilerplate

    @Deprecated(
        message = "Use substations.size instead.",
        replaceWith = ReplaceWith("substations.size")
    )
    fun numSubstations(): Int = substations.size

    @Deprecated(
        message = "Use substations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("substations.getByMRID(mRID)")
    )
    fun getSubstation(mRID: String): Substation? = substations.getByMrid(mRID)

    @Deprecated(
        message = "Use substations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.substations.add(substation) }")
    )
    fun addSubstation(substation: Substation): Loop {
        substations.add(substation)
        return this
    }

    @Deprecated(
        message = "Use substations.remove(substation) instead.",
        replaceWith = ReplaceWith("substations.remove(substation)")
    )
    fun removeSubstation(substation: Substation): Boolean = substations.remove(substation)

    @Deprecated(
        message = "Use substations.clear() instead.",
        replaceWith = ReplaceWith("substations.clear()")
    )
    fun clearSubstations(): Loop {
        substations.clear()
        return this
    }

    // endregion

    // region energizingSubstations boilerplate

    @Deprecated(
        message = "Use energizingSubstations.size instead.",
        replaceWith = ReplaceWith("energizingSubstations.size")
    )
    fun numEnergizingSubstations(): Int = energizingSubstations.size

    @Deprecated(
        message = "Use energizingSubstations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("energizingSubstations.getByMRID(mRID)")
    )
    fun getEnergizingSubstation(mRID: String): Substation? = energizingSubstations.getByMrid(mRID)

    @Deprecated(
        message = "Use energizingSubstations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.energizingSubstations.add(substation) }")
    )
    fun addEnergizingSubstation(substation: Substation): Loop {
        energizingSubstations.add(substation)
        return this
    }

    @Deprecated(
        message = "Use energizingSubstations.remove(substation) instead.",
        replaceWith = ReplaceWith("energizingSubstations.remove(substation)")
    )
    fun removeEnergizingSubstation(substation: Substation): Boolean = energizingSubstations.remove(substation)

    @Deprecated(
        message = "Use energizingSubstations.clear() instead.",
        replaceWith = ReplaceWith("energizingSubstations.clear()")
    )
    fun clearEnergizingSubstations(): Loop {
        energizingSubstations.clear()
        return this
    }

    // endregion

    // endregion
}

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
import com.zepben.ewb.testing.MRIDListWrapper

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
    val circuits: MRIDListWrapper<Circuit>
        get() = MRIDListWrapper(
            getter = { _circuits },
            setter = { _circuits = it })

    @Deprecated("BOILERPLATE: Use circuits.size instead")
    fun numCircuits(): Int = circuits.size

    @Deprecated("BOILERPLATE: Use circuits.getByMRID(mRID) instead")
    fun getCircuit(mRID: String): Circuit? = circuits.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use circuits.add(circuit) instead")
    fun addCircuit(circuit: Circuit): Loop {
        circuits.add(circuit)
        return this
    }

    @Deprecated("BOILERPLATE: Use circuits.remove(circuit) instead")
    fun removeCircuit(circuit: Circuit): Boolean = circuits.remove(circuit)

    @Deprecated("BOILERPLATE: Use circuits.clear() instead")
    fun clearCircuits(): Loop {
        circuits.clear()
        return this
    }

    /**
     * [ZBEX] [Substation]s that are powered by this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val substations: MRIDListWrapper<Substation>
        get() = MRIDListWrapper(
            getter = { _substations },
            setter = { _substations = it })

    @Deprecated("BOILERPLATE: Use substations.size instead")
    fun numSubstations(): Int = substations.size

    @Deprecated("BOILERPLATE: Use substations.getByMRID(mRID) instead")
    fun getSubstation(mRID: String): Substation? = substations.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use substations.add(substation) instead")
    fun addSubstation(substation: Substation): Loop {
        substations.add(substation)
        return this
    }

    @Deprecated("BOILERPLATE: Use substations.remove(substation) instead")
    fun removeSubstation(substation: Substation): Boolean = substations.remove(substation)

    @Deprecated("BOILERPLATE: Use substations.clear() instead")
    fun clearSubstations(): Loop {
        substations.clear()
        return this
    }

    /**
     * [ZBEX] The [Substation]s that normally energize this [Loop].
     * The returned collection is read only.
     */
    @ZBEX
    val energizingSubstations: MRIDListWrapper<Substation>
        get() = MRIDListWrapper(
            getter = { _energizingSubstations },
            setter = { _energizingSubstations = it })

    @Deprecated("BOILERPLATE: Use energizingSubstations.size instead")
    fun numEnergizingSubstations(): Int = energizingSubstations.size

    @Deprecated("BOILERPLATE: Use energizingSubstations.getByMRID(mRID) instead")
    fun getEnergizingSubstation(mRID: String): Substation? = energizingSubstations.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use energizingSubstations.add(substation) instead")
    fun addEnergizingSubstation(substation: Substation): Loop {
        energizingSubstations.add(substation)
        return this
    }

    @Deprecated("BOILERPLATE: Use energizingSubstations.remove(substation) instead")
    fun removeEnergizingSubstation(substation: Substation): Boolean = energizingSubstations.remove(substation)

    @Deprecated("BOILERPLATE: Use energizingSubstations.clear() instead")
    fun clearEnergizingSubstations(): Loop {
        energizingSubstations.clear()
        return this
    }

}

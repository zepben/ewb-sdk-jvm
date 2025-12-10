/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.feeder

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Line
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

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
    val endTerminals: MRIDListWrapper<Terminal>
        get() = MRIDListWrapper(
            getter = { _endTerminals },
            setter = { _endTerminals = it })

    @Deprecated("BOILERPLATE: Use endTerminals.size instead")
    fun numEndTerminals(): Int = endTerminals.size

    @Deprecated("BOILERPLATE: Use endTerminals.getByMRID(mRID) instead")
    fun getEndTerminal(mRID: String): Terminal? = endTerminals.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use endTerminals.add(endTerminal) instead")
    fun addEndTerminal(endTerminal: Terminal): Circuit {
        endTerminals.add(endTerminal)
        return this
    }

    @Deprecated("BOILERPLATE: Use endTerminals.remove(endTerminal) instead")
    fun removeEndTerminal(endTerminal: Terminal): Boolean = endTerminals.remove(endTerminal)

    @Deprecated("BOILERPLATE: Use endTerminals.clear() instead")
    fun clearEndTerminals(): Circuit {
        endTerminals.clear()
        return this
    }

    /**
     * Simplification of the CIM association via Bay to [Substation].
     * The returned collection is read only.
     */
    val endSubstations: MRIDListWrapper<Substation>
        get() = MRIDListWrapper(
            getter = { _endSubstations },
            setter = { _endSubstations = it })

    @Deprecated("BOILERPLATE: Use endSubstations.size instead")
    fun numEndSubstations(): Int = endSubstations.size

    @Deprecated("BOILERPLATE: Use endSubstations.getByMRID(mRID) instead")
    fun getEndSubstation(mRID: String): Substation? = endSubstations.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use endSubstations.add(substation) instead")
    fun addEndSubstation(substation: Substation): Circuit {
        endSubstations.add(substation)
        return this
    }

    @Deprecated("BOILERPLATE: Use endSubstations.remove(substation) instead")
    fun removeEndSubstation(substation: Substation): Boolean = endSubstations.remove(substation)

    @Deprecated("BOILERPLATE: Use endSubstations.clear() instead")
    fun clearEndSubstations(): Circuit {
        endSubstations.clear()
        return this
    }
}

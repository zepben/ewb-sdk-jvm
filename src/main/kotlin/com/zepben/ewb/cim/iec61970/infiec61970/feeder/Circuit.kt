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

    /**
     * @param endTerminal the [Terminal] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
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
    fun removeEndTerminal(endTerminal: Terminal): Boolean {
        val ret = _endTerminals?.remove(endTerminal) == true
        if (_endTerminals.isNullOrEmpty()) _endTerminals = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endTerminals].
     * @return this [Circuit]
     */
    fun clearEndTerminals(): Circuit {
        _endTerminals = null
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

    /**
     * @param substation the [Substation] to associate with this [Circuit].
     * @return A reference to this [Circuit] to allow fluent use.
     */
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
    fun removeEndSubstation(substation: Substation): Boolean {
        val ret = _endSubstations?.remove(substation) == true
        if (_endSubstations.isNullOrEmpty()) _endSubstations = null
        return ret
    }

    /**
     * Clear this [Circuit]'s associated [endSubstations].
     * @return this [Circuit]
     */
    fun clearEndSubstations(): Circuit {
        _endSubstations = null
        return this
    }
}

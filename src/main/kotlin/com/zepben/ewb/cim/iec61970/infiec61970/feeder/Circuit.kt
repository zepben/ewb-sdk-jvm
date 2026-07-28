/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.feeder

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Line

/**
 * <no description from CIM>
 *
 * @property loop The [Loop] containing this [Circuit].
 */
class Circuit(mRID: String) : Line(mRID) {

    var loop: Loop? = null

    private var _endTerminals: MutableList<Terminal>? = null
    private var _endSubstations: MutableList<Substation>? = null

    /**
     * <no description from CIM>
     * The returned collection is read only.
     */
    val endTerminals: MridCollection<Terminal> get() = LazyMridList(
        getter = { _endTerminals },
        setter = { _endTerminals = it },
        owner = this,
        elementDescription = "A Terminal"
    )

    /**
     * Simplification of the CIM association via Bay to [Substation].
     * The returned collection is read only.
     */
    val endSubstations: MridCollection<Substation> get() = LazyMridList(
        getter = { _endSubstations },
        setter = { _endSubstations = it },
        owner = this,
        elementDescription = "A Substation"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region endTerminals boilerplate

    @Deprecated(
        message = "Use endTerminals.size instead.",
        replaceWith = ReplaceWith("endTerminals.size")
    )
    fun numEndTerminals(): Int = endTerminals.size

    @Deprecated(
        message = "Use endTerminals.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("endTerminals.getByMRID(mRID)")
    )
    fun getEndTerminal(mRID: String): Terminal? = endTerminals.getByMrid(mRID)

    @Deprecated(
        message = "Use endTerminals.add(endTerminal) instead.",
        replaceWith = ReplaceWith("also { it.endTerminals.add(endTerminal) }")
    )
    fun addEndTerminal(endTerminal: Terminal): Circuit {
        endTerminals.add(endTerminal)
        return this
    }

    @Deprecated(
        message = "Use endTerminals.remove(endTerminal) instead.",
        replaceWith = ReplaceWith("endTerminals.remove(endTerminal)")
    )
    fun removeEndTerminal(endTerminal: Terminal): Boolean = endTerminals.remove(endTerminal)

    @Deprecated(
        message = "Use endTerminals.clear() instead.",
        replaceWith = ReplaceWith("endTerminals.clear()")
    )
    fun clearEndTerminals(): Circuit {
        endTerminals.clear()
        return this
    }

    // endregion

    // region endSubstations boilerplate

    @Deprecated(
        message = "Use endSubstations.size instead.",
        replaceWith = ReplaceWith("endSubstations.size")
    )
    fun numEndSubstations(): Int = endSubstations.size

    @Deprecated(
        message = "Use endSubstations.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("endSubstations.getByMRID(mRID)")
    )
    fun getEndSubstation(mRID: String): Substation? = endSubstations.getByMrid(mRID)

    @Deprecated(
        message = "Use endSubstations.add(substation) instead.",
        replaceWith = ReplaceWith("also { it.endSubstations.add(substation) }")
    )
    fun addEndSubstation(substation: Substation): Circuit {
        endSubstations.add(substation)
        return this
    }

    @Deprecated(
        message = "Use endSubstations.remove(substation) instead.",
        replaceWith = ReplaceWith("endSubstations.remove(substation)")
    )
    fun removeEndSubstation(substation: Substation): Boolean = endSubstations.remove(substation)

    @Deprecated(
        message = "Use endSubstations.clear() instead.",
        replaceWith = ReplaceWith("endSubstations.clear()")
    )
    fun clearEndSubstations(): Circuit {
        endSubstations.clear()
        return this
    }

    // endregion

    // endregion
}

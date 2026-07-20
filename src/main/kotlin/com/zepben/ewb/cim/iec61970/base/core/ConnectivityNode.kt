/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.MridList
import com.zepben.ewb.boilerplate.RefMridList
import com.zepben.ewb.services.common.extensions.getByMRID

/**
 * Connectivity nodes are points where terminals of AC conducting equipment are connected together with zero impedance.
 */
class ConnectivityNode(mRID: String) : IdentifiedObject(mRID) {

    private val _terminals: MutableList<Terminal> = mutableListOf()

    /**
     * The terminals for this connectivity node. The collection is read only
     */
    val terminals: MridList<Terminal> get() = RefMridList(
        _terminals,
        owner = this,
        elementDescription = "A Terminal"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region terminals boilerplate

    @Deprecated(
        message = "Use terminals.size instead.",
        replaceWith = ReplaceWith("terminals.size")
    )
    fun numTerminals(): Int = terminals.size

    @Deprecated(
        message = "Use terminals.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("terminals.getByMRID(mRID)")
    )
    fun getTerminal(mRID: String): Terminal? = terminals.getByMRID(mRID)

    @Deprecated(
        message = "Use terminals.add(terminal) instead.",
        replaceWith = ReplaceWith("also { it.terminals.add(terminal) }")
    )
    fun addTerminal(terminal: Terminal): ConnectivityNode {
        terminals.add(terminal)
        return this
    }

    @Deprecated(
        message = "Use terminals.remove(terminal) instead.",
        replaceWith = ReplaceWith("terminals.remove(terminal)")
    )
    fun removeTerminal(terminal: Terminal): Boolean = terminals.remove(terminal)

    @Deprecated(
        message = "Use terminals.clear() instead.",
        replaceWith = ReplaceWith("terminals.clear()")
    )
    fun clearTerminals(): ConnectivityNode {
        terminals.clear()
        return this
    }

    // endregion

    // endregion

}

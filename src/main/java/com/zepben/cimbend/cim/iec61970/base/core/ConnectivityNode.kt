/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * Connectivity nodes are points where terminals of AC conducting equipment are connected together with zero impedance.
 */
class ConnectivityNode @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID) {

    private val _terminals: MutableList<Terminal> = mutableListOf()

    /**
     * The terminals for this connectivity node. The collection is read only
     */
    val terminals: Collection<Terminal>
        get() = _terminals.asUnmodifiable()

    /**
     * Get the number of entries in the [Terminal] collection.
     */
    fun numTerminals() = _terminals.size

    /**
     * Terminals interconnected with zero impedance at a this connectivity node.
     *
     * @param mRID the mRID of the required [Terminal]
     * @return The [Terminal] with the specified [mRID] if it exists, otherwise null
     */
    fun getTerminal(mRID: String) = _terminals.getByMRID(mRID)

    /**
     * Add a [Terminal] to this [ConnectivityNode]
     * @param terminal The [Terminal] to add
     * @return this [ConnectivityNode]
     */
    fun addTerminal(terminal: Terminal): ConnectivityNode {
        if (validateReference(terminal, ::getTerminal, "A Terminal"))
            return this

        _terminals.add(terminal)

        return this
    }

    /**
     * Remove a terminal from this [ConnectivityNode]
     * @param terminal The [Terminal] to remove
     * @return true if [terminal] is removed from the collection
     */
    fun removeTerminal(terminal: Terminal?): Boolean = _terminals.remove(terminal)

    /**
     * Clear all [Terminal]'s from this [ConnectivityNode}
     * @return this [ConnectivityNode]
     */
    fun clearTerminals(): ConnectivityNode {
        _terminals.clear()
        return this
    }
}

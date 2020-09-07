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

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.TracedPhases
import com.zepben.cimbend.network.tracing.PhaseSelector
import com.zepben.cimbend.network.tracing.PhaseStatus
import java.lang.ref.WeakReference

/**
 * An AC electrical connection point to a piece of conducting equipment. Terminals are connected at physical connection points called connectivity nodes.
 *
 * @property conductingEquipment The conducting equipment of the terminal.  Conducting equipment have  terminals that may be connected to other
 *                               conducting equipment terminals via connectivity nodes or topological nodes.
 * @property phases Represents the normal network phasing condition.
 *                  If the attribute is missing three phases (ABC or ABCN) shall be assumed.
 * @property sequenceNumber The orientation of the terminal connections for a multiple terminal conducting equipment.
 *                          The sequence numbering starts with 1 and additional terminals should follow in increasing order.
 *                          The first terminal is the "starting point" for a two terminal branch.
 * @property tracedPhases the phase object representing the traced phases in both the normal and current network. If
 *                        properly configured you would expect the normal state phases to match those in {@code phases}
 */
class Terminal @JvmOverloads constructor(mRID: String = "") : AcDcTerminal(mRID) {

    var conductingEquipment: ConductingEquipment? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("conductingEquipment has already been set to $field. Cannot set this field again")
        }

    var phases: PhaseCode = PhaseCode.ABC
    var sequenceNumber: Int = 0
    val tracedPhases: TracedPhases = TracedPhases()

    // The reference to the connectivity node is weak so if a Network object goes out of scope, holding a single conducting equipment
    // reference does not cause everything connected to it in the network to stay in memory.
    private var _connectivityNode: WeakReference<ConnectivityNode> = NO_CONNECTIVITY_NODE

    var connectivityNode: ConnectivityNode?
        get() = _connectivityNode.get()
        set(value) {
            _connectivityNode = if (value != null) WeakReference(value) else NO_CONNECTIVITY_NODE
        }

    /**
     * @return The ID of the connectivity node to which this terminal connects with zero impedance.
     */
    fun connectivityNodeId(): String? = connectivityNode?.mRID

    /**
     * Helper method for checking if the terminal is connected.
     *
     * @return true if the terminal is wired to a connectivity node.
     */
    fun isConnected() = connectivityNode != null

    /**
     * Convenience method for accessing the normal phase status of a nominal phase.
     *
     * @param nominalPhase to get the phase status for.
     * @return the phases status for the nominal phase in the normal state of the network.
     */
    fun normalPhases(nominalPhase: SinglePhaseKind): PhaseStatus = PhaseSelector.NORMAL_PHASES.status(this, nominalPhase)

    /**
     * Convenience method for accessing the current phase status of a nominal phase.
     *
     * @param nominalPhase to get the phase status for.
     * @return the phases status for the nominal phase in the current state of the network.
     */
    fun currentPhases(nominalPhase: SinglePhaseKind): PhaseStatus = PhaseSelector.CURRENT_PHASES.status(this, nominalPhase)

    // NOTE: This is meant to be package private to prevent external linking of objects. Use the network
    //       to connect from outside this package.
    //
    fun connect(connectivityNode: ConnectivityNode) {
        this._connectivityNode = WeakReference(connectivityNode)
    }

    //
    // NOTE: This is meant to be package private to prevent external linking of objects. Use the network
    //       to disconnect from outside this package. It could be made 'internal' when all other classes are ported to Kotlin
    //
    fun disconnect() {
        _connectivityNode = NO_CONNECTIVITY_NODE
    }

    companion object {
        private val NO_CONNECTIVITY_NODE = WeakReference<ConnectivityNode>(null)
    }
}

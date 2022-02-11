/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.phases.PhaseSelector
import com.zepben.evolve.services.network.tracing.phases.PhaseStatus
import com.zepben.evolve.services.network.tracing.phases.TracedPhases
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
 * @property normalFeederDirection Stores the direction of the feeder head relative to this [Terminal] in the normal state of the network.
 * @property currentFeederDirection Stores the direction of the feeder head relative to this [Terminal] in the current state of the network.
 * @property tracedPhases The phase object representing the traced phases in both the normal and current network. If
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

    var normalFeederDirection: FeederDirection = FeederDirection.NONE
    var currentFeederDirection: FeederDirection = FeederDirection.NONE

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
    val connectivityNodeId: String?; get() = connectivityNode?.mRID

    /**
     * Helper method for checking if the terminal is connected.
     *
     * @return true if the terminal is wired to a connectivity node.
     */
    val isConnected: Boolean; get() = connectivityNode != null

    /**
     * Convenience method for accessing the normal phases.
     *
     * @return the [PhaseStatus] for the terminal in the normal state of the network.
     */
    val normalPhases: PhaseStatus; get() = PhaseSelector.NORMAL_PHASES.phases(this)

    /**
     * Convenience method for accessing the current phases.
     *
     * @return the [PhaseStatus] for the terminal in the normal state of the network.
     */
    val currentPhases: PhaseStatus; get() = PhaseSelector.CURRENT_PHASES.phases(this)

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

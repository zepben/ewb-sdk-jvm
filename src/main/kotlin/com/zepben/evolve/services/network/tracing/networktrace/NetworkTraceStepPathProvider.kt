/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators

internal class NetworkTraceStepPathProvider(val stateOperators: NetworkStateOperators) {

    fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path> {
        val nextTerminals = nextTerminals(path)

        return if (path.nominalPhasePaths.isNotEmpty()) {
            val phasePaths = path.nominalPhasePaths.map { it.to }.toSet()
            nextTerminals
                .map { nextTerminal -> TerminalConnectivityConnected.terminalConnectivity(path.toTerminal, nextTerminal, phasePaths) }
                .filter { it.nominalPhasePaths.isNotEmpty() }
                .map { NetworkTraceStep.Path(path.toTerminal, it.toTerminal, it.nominalPhasePaths) }
        } else {
            nextTerminals.map { NetworkTraceStep.Path(path.toTerminal, it) }
        }
    }

    private fun nextTerminals(path: NetworkTraceStep.Path): Sequence<Terminal> {
        val nextTerminals = if (path.tracedInternally) {
            // We need to step externally to connected terminals. However:
            // Busbars are only modelled with a single terminal. So if we find any we need to step to them before the
            // other (non busbar) equipment connected to the same connectivity node. Once the busbar has been
            // visited we then step to the other non busbar terminals connected to the same connectivity node.
            if (path.toTerminal.hasConnectedBusbars())
                path.toTerminal.connectedTerminals().filter { it.conductingEquipment is BusbarSection }
            else
                path.toTerminal.connectedTerminals()
        } else {
            // If we just visited a busbar, we step to the other terminals that share the same connectivity node.
            // Otherwise, we internally step to the other terminals on the equipment
            if (path.toEquipment is BusbarSection) {
                // We don't need to step to terminals that are busbars as they would have been queued at the same time this busbar step was.
                // We also don't try and go back to the terminal we came from as we already visited it to get to this busbar.
                path.toTerminal.connectedTerminals().filter { it != path.fromTerminal && it.conductingEquipment !is BusbarSection }
            } else {
                path.toTerminal.otherTerminals()
            }
        }

        return nextTerminals.filter { terminal -> terminal.conductingEquipment?.let { stateOperators.isInService(it) } == true }
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false
}

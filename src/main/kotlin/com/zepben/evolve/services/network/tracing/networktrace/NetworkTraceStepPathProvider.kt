/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.evolve.services.network.tracing.networktrace.operators.InServiceStateOperators
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
        val nextTerminals = when (val toEquipment = path.toEquipment) {
            is AcLineSegment -> nextTerminalsFromAcLineSegment(toEquipment, path)
            is BusbarSection -> nextTerminalsFromBusbar(path)
            is Clamp -> nextTerminalsFromClamp(toEquipment, path)
            is Cut -> nextTerminalsFromCut(toEquipment, path)
            else -> if (path.tracedInternally) {
                nextExternalTerminals(path)
            } else {
                path.toTerminal.otherTerminals()
            }
        }

        return nextTerminals.filter { terminal -> stateOperators.isInService(terminal.conductingEquipment) }
    }

    private fun nextTerminalsFromAcLineSegment(segment: AcLineSegment, path: NetworkTraceStep.Path): Sequence<Terminal> {
        // If the path traversed the segment, we need to step externally from the segment terminal.
        // Otherwise, we traverse the segment
        return if (path.tracedInternally || path.traversedAcLineSegment) {
            nextExternalTerminals(path)
        } else {
            if (path.toTerminal.sequenceNumber == 1)
                segment.traverseFromTerminalTowardsT2(path.toTerminal, 0.0)
            else
                segment.traverseFromTerminalTowardsT1(path.toTerminal, Double.MAX_VALUE)
        }
    }

    // We don't need to step to terminals that are busbars as they would have been queued at the same time this busbar step was.
    // We also don't try and go back to the terminal we came from as we already visited it to get to this busbar.
    private fun nextTerminalsFromBusbar(path: NetworkTraceStep.Path): Sequence<Terminal> =
        path.toTerminal.connectedTerminals().filter { it != path.fromTerminal && it.conductingEquipment !is BusbarSection }

    private fun nextTerminalsFromClamp(clamp: Clamp, path: NetworkTraceStep.Path): Sequence<Terminal> {
        // If the path was from traversing an AcLineSegment, we need to step externally to other equipment.
        // Else if we stepped here externally not from a segment, we need to traverse the segment both ways.
        return if (path.traversedAcLineSegment) {
            nextExternalTerminals(path)
        } else {
            when (val segment = clamp.acLineSegment) {
                null -> emptySequence()
                else -> segment.traverseFromTerminalBothWays(path.toTerminal, clamp.lengthFromT1Or0)
            }
        }
    }

    private fun nextTerminalsFromCut(cut: Cut, path: NetworkTraceStep.Path): Sequence<Terminal> {
        // If the path was from traversing an AcLineSegment, we need to step externally to other equipment, and internally on the cut.
        // Else if we stepped here externally not from a segment, we need to traverse the segment plus step internally on the cut.
        val nextTerminals = if (path.traversedAcLineSegment) {
            nextExternalTerminals(path)
        } else {
            when (val segment = cut.acLineSegment) {
                null -> emptySequence()
                else -> {
                    val towardsT2: Boolean = path.toTerminal.sequenceNumber != 1
                    segment.traverseFromTerminal(path.toTerminal, cut.lengthFromT1Or0, towardsT2)
                }
            }
        }

        return if (path.tracedInternally) {
            nextTerminals + nextExternalTerminals(path)
        } else {
            val cutOtherTerminal = cut.getTerminal(if (path.toTerminal.sequenceNumber == 1) 2 else 1)
            nextTerminals + cutOtherTerminal.asSequenceOrEmpty()
        }
    }

    private fun nextExternalTerminals(path: NetworkTraceStep.Path): Sequence<Terminal> {
        // When we step externally to other terminals we need to consider:
        // Busbars are only modelled with a single terminal. So if we find any we need to step to them before the
        // other (non busbar) equipment connected to the same connectivity node. Once the busbar has been
        // visited we then step to the other non busbar terminals connected to the same connectivity node.
        return when {
            path.toEquipment is BusbarSection -> nextTerminalsFromBusbar(path)
            path.toTerminal.hasConnectedBusbars() -> path.toTerminal.connectedTerminals().filter { it.conductingEquipment is BusbarSection }
            else -> path.toTerminal.connectedTerminals()
        }
    }

    private fun AcLineSegment.traverseFromTerminalTowardsT1(fromTerminal: Terminal, lengthFromT1: Double): Sequence<Terminal> =
        traverseFromTerminal(fromTerminal, lengthFromT1, false)

    private fun AcLineSegment.traverseFromTerminalTowardsT2(fromTerminal: Terminal, lengthFromT1: Double): Sequence<Terminal> =
        traverseFromTerminal(fromTerminal, lengthFromT1, true)

    private fun AcLineSegment.traverseFromTerminalBothWays(fromTerminal: Terminal, lengthFromT1: Double): Sequence<Terminal> =
        traverseFromTerminalTowardsT1(fromTerminal, lengthFromT1) + traverseFromTerminalTowardsT2(fromTerminal, lengthFromT1)

    private fun AcLineSegment.traverseFromTerminal(fromTerminal: Terminal, lengthFromT1: Double, towardsSegmentT2: Boolean): Sequence<Terminal> {
        // We need to ignore cuts that are not "in service" because that means they do not exist!
        // We also make sure we filter out the cut or the clamp we are starting at, so we don't compare it in our checks
        val cuts = cuts.filter { it != fromTerminal.conductingEquipment && stateOperators.isInService(it) }
        val clamps = clamps.filter { it != fromTerminal.conductingEquipment && stateOperators.isInService(it) }

        // Can do a simple return if we don't need to do any special cuts/clamps processing
        if (cuts.isEmpty() && clamps.isEmpty())
            return fromTerminal.otherTerminals()

        val nextCut = when {
            towardsSegmentT2 -> cuts.filter { it.lengthFromT1Or0 > lengthFromT1 }.minByOrNull { it.lengthFromT1Or0 }
            else -> cuts.filter { it.lengthFromT1Or0 < lengthFromT1 }.maxByOrNull { it.lengthFromT1Or0 }
        }

        val nextTerminalLengthFromTerminal1: Double = when {
            towardsSegmentT2 -> nextCut?.lengthFromTerminal1 ?: Double.MAX_VALUE
            else -> nextCut?.lengthFromTerminal1 ?: 0.0
        }

        val clampsBeforeNextTerminal = when {
            towardsSegmentT2 -> clamps.asSequence().filter { it.lengthFromT1Or0 in lengthFromT1..nextTerminalLengthFromTerminal1 }
            else -> clamps.asSequence().filter { it.lengthFromT1Or0 in nextTerminalLengthFromTerminal1..lengthFromT1 }
        }

        val nextTerminal = when {
            nextCut == null -> getTerminal(if (towardsSegmentT2) 2 else 1)
            else -> nextCut.getTerminal(if (towardsSegmentT2) 1 else 2)
        }

        return clampsBeforeNextTerminal.mapNotNull { it.getTerminal(1) } + nextTerminal.asSequenceOrEmpty()
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false


    // todo: We know if we traversed when we compute the next terminal. Should we store this on the step so we don't need to compute it?
    private val NetworkTraceStep.Path.traversedAcLineSegment: Boolean get() =
        when {
            tracedInternally -> false
            fromEquipment is AcLineSegment -> when (toEquipment) {
                is Clamp -> toEquipment.acLineSegment === fromEquipment
                is Cut -> toEquipment.acLineSegment === fromEquipment
                else -> false
            }
            fromEquipment is Cut -> when (toEquipment) {
                is AcLineSegment -> fromEquipment.acLineSegment === toEquipment
                is Clamp -> fromEquipment.acLineSegment === toEquipment.acLineSegment
                else -> false
            }
            fromEquipment is Clamp -> when (toEquipment) {
                is AcLineSegment -> fromEquipment.acLineSegment === toEquipment
                is Cut -> fromEquipment.acLineSegment === toEquipment.acLineSegment
                else -> false
            }
            else -> false
        }

    private val Cut.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0
    private val Clamp.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0

    private fun Terminal?.asSequenceOrEmpty(): Sequence<Terminal> = if (this != null) sequenceOf(this) else emptySequence()

    private fun InServiceStateOperators.isInService(equipment: Equipment?): Boolean = equipment?.let { isInService(it) } ?: false
}

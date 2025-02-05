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

internal class NetworkTraceStepPathProvider(val stateOperators: InServiceStateOperators) {

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
        // If the current path traversed the segment, we need to step externally from the segment terminal.
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

    private fun nextTerminalsFromBusbar(path: NetworkTraceStep.Path): Sequence<Terminal> {
        return path.toTerminal.connectedTerminals()
            // We don't go back to the terminal we came from as we already visited it to get to this busbar.
            .filter { it != path.fromTerminal }
            // We don't step to terminals that are busbars as they would have been returned at the same time this busbar step was.
            .filter { it.conductingEquipment !is BusbarSection }
    }

    private fun nextTerminalsFromClamp(clamp: Clamp, path: NetworkTraceStep.Path): Sequence<Terminal> {
        // If the current path was from traversing an AcLineSegment, we need to step externally to other equipment.
        // Otherwise, we need to traverse the segment both ways.
        return if (path.traversedAcLineSegment) {
            nextExternalTerminals(path)
        } else {
            clamp.acLineSegment?.traverseFromTerminalBothWays(path.toTerminal, clamp.lengthFromT1Or0).orEmpty()
        }
    }

    private fun nextTerminalsFromCut(cut: Cut, path: NetworkTraceStep.Path): Sequence<Terminal> {
        // If the current path was from traversing an AcLineSegment, we need to step externally to other equipment.
        // Else we need to traverse the segment.
        val nextTerminals = if (path.traversedAcLineSegment) {
            nextExternalTerminals(path)
        } else {
            val towardsT2: Boolean = path.toTerminal.sequenceNumber != 1
            cut.acLineSegment?.traverseFromTerminal(path.toTerminal, cut.lengthFromT1Or0, towardsT2).orEmpty()
        }

        // If the current path traced internally, we need to also return the external terminals
        // Else we need to step internally to the Cut's other terminal.
        return if (path.tracedInternally) {
            // traversedAcLineSegment and tracedInternally should never both be true, so we should never get external terminals twice
            nextTerminals + nextExternalTerminals(path)
        } else {
            val cutOtherTerminal = cut.getTerminal(if (path.toTerminal.sequenceNumber == 1) 2 else 1)
            nextTerminals + cutOtherTerminal.asSequence()
        }
    }

    private fun nextExternalTerminals(path: NetworkTraceStep.Path): Sequence<Terminal> {
        // Busbars are only modelled with a single terminal. So if we find any we need to step to them before the
        // other (non busbar) equipment connected to the same connectivity node. Once the busbar has been
        // visited we then step to the other non busbar terminals connected to the same connectivity node.
        // If there are no busbars we can just step to all other connected terminals.
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

    /**
     * This returns terminals found traversing along an AcLineSegment from any terminal "on" the segment. Terminals considered on the segment are any clamp
     * or cut terminals that belong to the segment as well as the segment's own terminals. When traversing the segment, the traversal stops
     * at and returns the next cut terminal found along the segment plus any clamp terminals it found between the fromTerminal and the cut terminal.
     * If there are no cuts on the segment the terminal, the other end of the segment is returned along with all clamp terminals.
     * To determine order of terminals on the segment, `lengthFromTerminal1` is used for cuts and clamps. When this property is null a default value of 0.0 is
     * assumed, effectively placing it at the start of the segment. Terminal 1 on the segment is deemed at 0.0 and Terminal 2 is deemed at Double.MAX_VALUE.
     *
     * This algorithm assumes AcLineSegments have exactly 2 terminals, cuts have exactly 2 terminals and clamps have exactly 1 terminal.
     * However, if you are using the legacy model where there were multiple terminals on a segment, this function will still return all the
     * terminals on the segment as long as there are no clamps or cuts on the segment. Basically, segments with cuts and clamps are mutually exclusive
     * to segments with multiple terminals (the data model should enforce this anyway).
     *
     * @param fromTerminal The terminal on the segment to traverse from. This could either be a segment terminal, or a terminal from any cut or clamp on the segment.
     * @param lengthFromT1 The length from terminal 1 the fromTerminal is.
     * @param towardsSegmentT2 Use `true` if the segment should be traversed towards terminal 2, otherwise `false` to traverse towards terminal 1.
     */
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

        return clampsBeforeNextTerminal.mapNotNull { it.getTerminal(1) } + nextTerminal.asSequence()
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false


    private val Cut.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0
    private val Clamp.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0

    private fun Terminal?.asSequence(): Sequence<Terminal> = if (this != null) sequenceOf(this) else emptySequence()

    private fun InServiceStateOperators.isInService(equipment: Equipment?): Boolean = if (equipment != null) isInService(equipment) else false
}

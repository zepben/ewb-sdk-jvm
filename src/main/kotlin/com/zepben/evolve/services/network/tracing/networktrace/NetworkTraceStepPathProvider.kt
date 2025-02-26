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

private typealias PathFactory = (nextTerminal: Terminal, traversedAcLineSegment: AcLineSegment?) -> NetworkTraceStep.Path?

internal class NetworkTraceStepPathProvider(val stateOperators: InServiceStateOperators) {

    fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path> {
        val pathFactory = if (path.nominalPhasePaths.isNotEmpty())
            createPathWithPhasesFactory(path)
        else
            createPathFactory(path)

        val nextPaths = when (val toEquipment = path.toEquipment) {
            is AcLineSegment -> nextPathsFromAcLineSegment(toEquipment, path, pathFactory)
            is BusbarSection -> nextPathsFromBusbar(path, pathFactory)
            is Clamp -> nextPathsFromClamp(toEquipment, path, pathFactory)
            is Cut -> nextPathsFromCut(toEquipment, path, pathFactory)
            else -> if (path.tracedInternally) {
                nextExternalPaths(path, pathFactory)
            } else {
                path.toTerminal.otherTerminals().mapToPath(pathFactory)
            }
        }

        return nextPaths.filter { stateOperators.isInService(it.toTerminal.conductingEquipment) }
    }

    private fun createPathFactory(path: NetworkTraceStep.Path): PathFactory =
        { nextTerminal: Terminal, traversed: AcLineSegment? ->
            NetworkTraceStep.Path(path.toTerminal, nextTerminal, traversed)
        }

    private fun createPathWithPhasesFactory(currentPath: NetworkTraceStep.Path): PathFactory {
        val phasePaths = currentPath.nominalPhasePaths.map { it.to }.toSet()
        val nextFromTerminal = currentPath.toTerminal
        return { nextTerminal: Terminal, traversed: AcLineSegment? ->
            val nextPaths = TerminalConnectivityConnected.terminalConnectivity(nextFromTerminal, nextTerminal, phasePaths)
            if (nextPaths.nominalPhasePaths.isNotEmpty())
                NetworkTraceStep.Path(nextFromTerminal, nextTerminal, traversed, nextPaths.nominalPhasePaths)
            else
                null
        }
    }

    private fun nextPathsFromAcLineSegment(segment: AcLineSegment, path: NetworkTraceStep.Path, pathFactory: PathFactory): Sequence<NetworkTraceStep.Path> {
        // If the current path traversed the segment, we need to step externally from the segment terminal.
        // Otherwise, we traverse the segment
        return if (path.tracedInternally || path.didTraverseAcLineSegment) {
            nextExternalPaths(path, pathFactory)
        } else {
            if (path.toTerminal.sequenceNumber == 1)
                segment.traverseFromTerminal(
                    path.toTerminal,
                    lengthFromT1 = 0.0,
                    towardsSegmentT2 = true,
                    canStopAtCutsAtSamePosition = true,
                    cutAtSamePositionTerminalNumber = 1,
                    pathFactory = pathFactory
                )
            else {
                segment.traverseFromTerminal(
                    path.toTerminal,
                    lengthFromT1 = segment.lengthOrMax,
                    towardsSegmentT2 = false,
                    canStopAtCutsAtSamePosition = true,
                    cutAtSamePositionTerminalNumber = 2,
                    pathFactory = pathFactory
                )
            }
        }
    }

    private fun nextPathsFromBusbar(path: NetworkTraceStep.Path, pathFactory: PathFactory): Sequence<NetworkTraceStep.Path> {
        return path.toTerminal.connectedTerminals()
            // We don't go back to the terminal we came from as we already visited it to get to this busbar.
            .filter { it != path.fromTerminal }
            // We don't step to terminals that are busbars as they would have been returned at the same time this busbar step was.
            .filter { it.conductingEquipment !is BusbarSection }
            .mapToPath(pathFactory)
    }

    private fun nextPathsFromClamp(clamp: Clamp, path: NetworkTraceStep.Path, pathFactory: PathFactory): Sequence<NetworkTraceStep.Path> {
        return when {
            // If we traversed the AcLineSegment, we go to external paths only, even if this path is the "start" (tracedInternally) item
            path.didTraverseAcLineSegment -> nextExternalPaths(path, pathFactory)
            // If this is the start item (the only way a clamp can have tracedInternally) we go externally and traverse the segment
            path.tracedInternally -> nextExternalPaths(path, pathFactory) + traverseAcLineSegmentFromClamp(clamp, path, pathFactory)
            // Otherwise we externally stepped to the clamp so just traverse the segment
            else -> traverseAcLineSegmentFromClamp(clamp, path, pathFactory)
        }
    }

    private fun traverseAcLineSegmentFromClamp(
        clamp: Clamp,
        path: NetworkTraceStep.Path,
        pathFactory: PathFactory
    ): Sequence<NetworkTraceStep.Path> {
        // Because we consider clamps at the same position as a cut on the terminal 1 side of the cut, we do not stop at cuts at the same position when
        // traversing towards t1, but we do when traversing towards t2.
        val nextPathsTowardsT1 = clamp.acLineSegment?.traverseFromTerminal(
            path.toTerminal,
            lengthFromT1 = clamp.lengthFromT1Or0,
            towardsSegmentT2 = false,
            canStopAtCutsAtSamePosition = false,
            cutAtSamePositionTerminalNumber = 1,
            pathFactory = pathFactory
        ).orEmpty()

        val nextPathsTowardsT2 = clamp.acLineSegment?.traverseFromTerminal(
            path.toTerminal,
            lengthFromT1 = clamp.lengthFromT1Or0,
            towardsSegmentT2 = true,
            canStopAtCutsAtSamePosition = true,
            cutAtSamePositionTerminalNumber = 1,
            pathFactory = pathFactory
        ).orEmpty()

        return (nextPathsTowardsT1 + nextPathsTowardsT2).distinctBy { it.toEquipment }
    }

    private fun nextPathsFromCut(cut: Cut, path: NetworkTraceStep.Path, pathFactory: PathFactory): Sequence<NetworkTraceStep.Path> {
        // If the current path was from traversing an AcLineSegment, we need to step externally to other equipment.
        // Else we need to traverse the segment.
        val nextTerminals = if (path.didTraverseAcLineSegment) {
            nextExternalPaths(path, pathFactory)
        } else {
            cut.acLineSegment?.traverseFromTerminal(
                path.toTerminal,
                lengthFromT1 = cut.lengthFromT1Or0,
                towardsSegmentT2 = path.toTerminal.sequenceNumber != 1,
                canStopAtCutsAtSamePosition = false,
                cutAtSamePositionTerminalNumber = path.toTerminal.sequenceNumber,
                pathFactory = pathFactory
            ).orEmpty()
        }

        // If the current path traced internally, we need to also return the external terminals
        // Else we need to step internally to the Cut's other terminal.
        return if (path.tracedInternally) {
            // traversedAcLineSegment and tracedInternally should never both be true from a cut, so we should never get external terminals twice.
            nextTerminals + nextExternalPaths(path, pathFactory)
        } else {
            val cutOtherTerminal = cut.getTerminal(if (path.toTerminal.sequenceNumber == 1) 2 else 1)
            nextTerminals + cutOtherTerminal.mapToPath(pathFactory)
        }
    }

    private fun nextExternalPaths(path: NetworkTraceStep.Path, pathFactory: PathFactory): Sequence<NetworkTraceStep.Path> {
        // Busbars are only modelled with a single terminal. So if we find any we need to step to them before the
        // other (non busbar) equipment connected to the same connectivity node. Once the busbar has been
        // visited we then step to the other non busbar terminals connected to the same connectivity node.
        // If there are no busbars we can just step to all other connected terminals.
        return when {
            path.toEquipment is BusbarSection -> nextPathsFromBusbar(path, pathFactory)
            path.toTerminal.hasConnectedBusbars() -> path.toTerminal.connectedTerminals()
                .filter { it.conductingEquipment is BusbarSection }
                .mapToPath(pathFactory)

            else -> path.toTerminal.connectedTerminals().mapToPath(pathFactory)
        }
    }

    /**
     * This returns terminals found traversing along an AcLineSegment from any terminal "on" the segment. Terminals considered on the segment are any clamp
     * or cut terminals that belong to the segment as well as the segment's own terminals. When traversing the segment, the traversal stops
     * at and returns the next cut terminal found along the segment plus any clamp terminals it found between the fromTerminal and the cut terminal.
     * If there are no cuts on the segment the terminal, the other end of the segment is returned along with all clamp terminals.
     * To determine order of terminals on the segment, `lengthFromTerminal1` is used for cuts and clamps. When this property is null a default value of 0.0 is
     * assumed, effectively placing it at the start of the segment. Terminal 1 on the segment is deemed at 0.0 and Terminal 2 is deemed at
     * [AcLineSegment.length] or [Double.MAX_VALUE] if the length or the segment is `null`.
     *
     * This algorithm assumes AcLineSegments have exactly 2 terminals, cuts have exactly 2 terminals and clamps have exactly 1 terminal.
     *
     * If there is a cut and a clamp at the exact same length on the segment, it is assumed the clamp is on the terminal 1 side of the cut. This is so you do not
     * get the clamp twice when traversing a segment from one end to the other. As a clamp can't technically be in the exact same spot as a cut, you should
     * realistically model this either attaching the equipment attached by the clamp to the appropriate cut terminal, or, place a clamp at a length that is
     * not exactly the same as the cut. This would yield more accurate and deterministic behaviour.
     *
     * @param fromTerminal The terminal on the segment to traverse from. This could either be a segment terminal, or a terminal from any cut or clamp on the segment.
     * @param lengthFromT1 The length from terminal 1 the fromTerminal is.
     * @param towardsSegmentT2 Use `true` if the segment should be traversed towards terminal 2, otherwise `false` to traverse towards terminal 1.
     */
    private fun AcLineSegment.traverseFromTerminal(
        fromTerminal: Terminal,
        lengthFromT1: Double,
        towardsSegmentT2: Boolean,
        canStopAtCutsAtSamePosition: Boolean,
        cutAtSamePositionTerminalNumber: Int,
        pathFactory: PathFactory
    ): Sequence<NetworkTraceStep.Path> {
        // We need to ignore cuts that are not "in service" because that means they do not exist!
        // We also make sure we filter out the cut or the clamp we are starting at, so we don't compare it in our checks
        val cuts = cuts.filter { it != fromTerminal.conductingEquipment && stateOperators.isInService(it) }
        val clamps = clamps.filter { it != fromTerminal.conductingEquipment && stateOperators.isInService(it) }

        // Can do a simple return if we don't need to do any special cuts/clamps processing
        if (cuts.isEmpty() && clamps.isEmpty())
            return fromTerminal.otherTerminals().mapToPath(pathFactory, this)

        val cutsAtSamePosition = cuts.filter { it.lengthFromT1Or0 == lengthFromT1 }
        val stopAtCutsAtSamePosition: Boolean = canStopAtCutsAtSamePosition && cutsAtSamePosition.isNotEmpty()

        val nextCutLengthFromTerminal1 = when {
            stopAtCutsAtSamePosition -> lengthFromT1
            towardsSegmentT2 -> cuts.filter { it.lengthFromT1Or0 > lengthFromT1 }.minOfOrNull { it.lengthFromT1Or0 }
            else -> cuts.filter { it.lengthFromT1Or0 < lengthFromT1 }.maxOfOrNull { it.lengthFromT1Or0 }
        }

        val nextCuts = nextCutLengthFromTerminal1?.let { cuts.filter { it.lengthFromT1Or0 == nextCutLengthFromTerminal1 } }.orEmpty()

        val nextTerminalLengthFromTerminal1: Double = when {
            nextCutLengthFromTerminal1 != null -> nextCutLengthFromTerminal1
            towardsSegmentT2 -> lengthOrMax
            else -> 0.0
        }

        val clampsBeforeNextTerminal = clamps.asSequence().filter(when {
            fromTerminal.conductingEquipment is AcLineSegment && towardsSegmentT2 -> { it: Clamp -> it.lengthFromT1Or0 in lengthFromT1..nextTerminalLengthFromTerminal1 }
            towardsSegmentT2 -> { it: Clamp -> it.lengthFromT1Or0 > lengthFromT1 && it.lengthFromT1Or0 <= nextTerminalLengthFromTerminal1 }
            nextTerminalLengthFromTerminal1 == 0.0 && nextCuts.isEmpty() -> { it: Clamp -> it.lengthFromT1Or0 in nextTerminalLengthFromTerminal1..lengthFromT1 }
            else -> { it: Clamp -> it.lengthFromT1Or0 <= lengthFromT1 && it.lengthFromT1Or0 > nextTerminalLengthFromTerminal1 }
        })

        val nextStopTerminals = when {
            stopAtCutsAtSamePosition -> sequenceOf()
            nextCuts.isNotEmpty() -> nextCuts.asSequence().mapNotNull { it.getTerminal(if (towardsSegmentT2) 1 else 2) }
            else -> getTerminal(if (towardsSegmentT2) 2 else 1)?.let { sequenceOf(it) }.orEmpty()
        }

        val nextTerminals = cutsAtSamePosition.asSequence().mapNotNull { it.getTerminal(cutAtSamePositionTerminalNumber) } +
            clampsBeforeNextTerminal.mapNotNull { it.getTerminal(1) } +
            nextStopTerminals

        return nextTerminals.mapToPath(pathFactory, this)
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false


    private val Cut.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0
    private val Clamp.lengthFromT1Or0: Double get() = lengthFromTerminal1 ?: 0.0
    private val AcLineSegment.lengthOrMax: Double get() = length ?: Double.MAX_VALUE

    private fun Terminal?.mapToPath(pathFactory: PathFactory, traversedAcLineSegment: AcLineSegment? = null): Sequence<NetworkTraceStep.Path> =
        if (this != null) sequenceOf(this).mapToPath(pathFactory, traversedAcLineSegment) else emptySequence()

    private fun Sequence<Terminal>.mapToPath(pathFactory: PathFactory, traversedAcLineSegment: AcLineSegment? = null): Sequence<NetworkTraceStep.Path> =
        mapNotNull { pathFactory(it, traversedAcLineSegment) }

    private fun InServiceStateOperators.isInService(equipment: Equipment?): Boolean = if (equipment != null) isInService(equipment) else false
}

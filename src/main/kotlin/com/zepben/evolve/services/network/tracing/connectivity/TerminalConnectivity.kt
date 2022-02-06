/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.connectivity.PhasePaths.straightPhaseConnectivity
import com.zepben.evolve.services.network.tracing.connectivity.PhasePaths.viableInferredPhaseConnectivity
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath

class TerminalConnectivity @JvmOverloads constructor(
    private val createCandidatePhases: () -> XyCandidatePhasePaths = { XyCandidatePhasePaths() }
) {

    fun connectedTerminals(terminal: Terminal, phaseCode: PhaseCode): List<ConnectivityResult> {
        return connectedTerminals(terminal, phaseCode.singlePhases)
    }

    @JvmOverloads
    fun connectedTerminals(terminal: Terminal, phases: Iterable<SinglePhaseKind> = terminal.phases.singlePhases): List<ConnectivityResult> {
        val tracePhases = phases.intersect(terminal.phases.singlePhases.toSet())
        val connectivityNode = terminal.connectivityNode ?: return emptyList()

        val results = mutableListOf<ConnectivityResult>()
        connectivityNode.terminals.forEach { connectedTerminal ->
            if (connectedTerminal != terminal) {
                val cr = terminalConnectivity(terminal, connectedTerminal, tracePhases)
                if (cr.nominalPhasePaths.isNotEmpty())
                    results.add(cr)
            }
        }
        return results
    }

    private fun terminalConnectivity(
        terminal: Terminal,
        connectedTerminal: Terminal,
        tracedPhases: Set<SinglePhaseKind>
    ): ConnectivityResult =
        ConnectivityResult.between(
            terminal,
            connectedTerminal,
            (findStraightPhasePaths(terminal, connectedTerminal)
                ?: findXyPhasePaths(terminal, connectedTerminal)
                ?: emptyList())
                .filter { it.from in tracedPhases }
                .filter { it.to in connectedTerminal.phases }
        )

    private fun findStraightPhasePaths(terminal: Terminal, connectedTerminal: Terminal): Collection<NominalPhasePath>? =
        straightPhaseConnectivity[terminal.phases]?.get(connectedTerminal.phases)

    private fun findXyPhasePaths(terminal: Terminal, connectedTerminal: Terminal): Collection<NominalPhasePath>? {
        val xyPhases = terminal.findXyPhases()
        val connectedXyPhases = connectedTerminal.findXyPhases()

        if ((xyPhases.isNone() && connectedXyPhases.isNone()) || (xyPhases.isNotNone() && connectedXyPhases.isNotNone()))
            return null

        val nominalPhasePaths = mutableListOf<NominalPhasePath>()
        if (terminal.phases.contains(N) && connectedTerminal.phases.contains(N))
            nominalPhasePaths.add(NominalPhasePath(N, N))

        if (xyPhases.isNotNone())
            findXyPhasePaths(terminal) { from, to -> nominalPhasePaths.add(NominalPhasePath(from, to)) }
        else
            findXyPhasePaths(terminal) { from, to -> nominalPhasePaths.add(NominalPhasePath(to, from)) }

        return nominalPhasePaths
    }

    private fun findXyPhasePaths(
        terminal: Terminal,
        addPath: (SinglePhaseKind, SinglePhaseKind) -> Unit
    ) {
        val cn = terminal.connectivityNode!!

        val xyPhases = cn.terminals.associateWith { it.findXyPhases() }.filterValues { it.isNotNone() }
        val primaryPhases = cn.terminals.associateWith { it.findPrimaryPhases() }.filterValues { it.isNotNone() }

        findXyCandidatePhases(xyPhases, primaryPhases).apply {
            calculatePaths()
                .asSequence()
                .filter { (_, to) -> to != NONE }
                .filter { (from, to) -> (from in terminal.phases.singlePhases) || (to in terminal.phases.singlePhases) }
                .forEach { (from, to) -> addPath(from, to) }
        }
    }

    private fun findXyCandidatePhases(
        xyPhases: Map<Terminal, PhaseCode>,
        primaryPhases: Map<Terminal, PhaseCode>
    ): XyCandidatePhasePaths {
        val queue = ArrayDeque<XyPhaseStep>()
        val visited = mutableSetOf<XyPhaseStep>()
        val candidatePhases = createCandidatePhases()

        xyPhases.forEach { (terminal, xyPhaseCode) ->
            primaryPhases.values.forEach { primaryPhaseCode ->
                viableInferredPhaseConnectivity[xyPhaseCode]?.get(primaryPhaseCode)?.forEach { (phase, candidates) ->
                    candidatePhases.addCandidates(phase, candidates)
                }
            }
            findXyCandidatePhases(XyPhaseStep(terminal, xyPhaseCode), visited, queue, candidatePhases)
        }

        while (queue.isNotEmpty())
            findXyCandidatePhases(queue.removeFirst(), visited, queue, candidatePhases)

        return candidatePhases
    }

    private fun findXyCandidatePhases(
        step: XyPhaseStep,
        visited: MutableSet<XyPhaseStep>,
        queue: ArrayDeque<XyPhaseStep>,
        candidatePhases: XyCandidatePhasePaths
    ) {
        if (!visited.add(step))
            return

        val withoutNeutral = step.terminal.phases.withoutNeutral()
        if (withoutNeutral.singlePhases.any { (it == X) || (it == Y) }) {
            if (!checkTracedPhases(step, candidatePhases))
                queueNext(step.terminal, withoutNeutral, queue)
        } else {
            viableInferredPhaseConnectivity[step.phaseCode]?.get(withoutNeutral)?.forEach { (phase, candidates) ->
                candidatePhases.addCandidates(phase, candidates)
            }
        }
    }

    private fun checkTracedPhases(step: XyPhaseStep, candidatePhases: XyCandidatePhasePaths): Boolean {
        var foundTraced = false
        step.terminal.tracedPhases.apply {
            phaseNormal(X).takeIf { it != NONE }?.also {
                candidatePhases.addKnown(X, it)
                foundTraced = true
            }
            phaseNormal(Y).takeIf { it != NONE }?.also {
                candidatePhases.addKnown(Y, it)
                foundTraced = true
            }
        }
        return foundTraced
    }

    private fun queueNext(terminal: Terminal, phaseCode: PhaseCode, queue: ArrayDeque<XyPhaseStep>) {
        terminal.conductingEquipment?.let { ce ->
            if ((ce !is Switch) || !ce.isNormallyOpen()) {
                ce.terminals
                    .asSequence()
                    .filter { it != terminal }
                    .flatMap { it.connectivityNode?.terminals ?: emptyList() }
                    .filter { it.conductingEquipment != ce }
                    .forEach { queue.add(XyPhaseStep(it, phaseCode)) }
            }
        }
    }

    private fun Terminal.findXyPhases() =
        when (phases) {
            PhaseCode.XY, PhaseCode.XYN -> PhaseCode.XY
            PhaseCode.X, PhaseCode.XN -> PhaseCode.X
            PhaseCode.Y, PhaseCode.YN -> PhaseCode.Y
            else -> PhaseCode.NONE
        }

    private fun Terminal.findPrimaryPhases() =
        when (phases) {
            PhaseCode.ABC, PhaseCode.ABCN -> PhaseCode.ABC
            PhaseCode.AB, PhaseCode.ABN -> PhaseCode.AB
            PhaseCode.AC, PhaseCode.ACN -> PhaseCode.AC
            PhaseCode.BC, PhaseCode.BCN -> PhaseCode.BC
            PhaseCode.A, PhaseCode.AN -> PhaseCode.A
            PhaseCode.B, PhaseCode.BN -> PhaseCode.B
            PhaseCode.C, PhaseCode.CN -> PhaseCode.C
            else -> PhaseCode.NONE
        }

    private fun PhaseCode.isNone(): Boolean {
        return this == PhaseCode.NONE
    }

    private fun PhaseCode.isNotNone(): Boolean {
        return this != PhaseCode.NONE
    }

}

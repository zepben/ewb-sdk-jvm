/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.tracing.connectivity.TerminalConnectivityConnected
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep.Path
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.Traversal

private typealias CheckInService = (ConductingEquipment) -> Boolean

internal object NetworkTraceQueueNext {
    fun <T> basic(isInService: CheckInService, computeData: ComputeData<T>): Traversal.QueueNext<NetworkTraceStep<T>> {
        return Traversal.QueueNext { item, context, queueItem ->
            nextTraceSteps(isInService, item, context, computeData).forEach { queueItem(it) }
        }
    }

    fun <T> basic(isInService: CheckInService, computeNextT: ComputeDataWithPaths<T>): Traversal.QueueNext<NetworkTraceStep<T>> {
        return Traversal.QueueNext { item, context, queueItem ->
            nextTraceSteps(isInService, item, context, computeNextT).forEach { queueItem(it) }
        }
    }

    fun <T> branching(isInService: CheckInService, computeData: ComputeData<T>): Traversal.BranchingQueueNext<NetworkTraceStep<T>> {
        return Traversal.BranchingQueueNext { item, context, queueItem, queueBranch ->
            queueNextStepsBranching(nextTraceSteps(isInService, item, context, computeData).toList(), queueItem, queueBranch)
        }
    }

    fun <T> branching(isInService: CheckInService, computeNextT: ComputeDataWithPaths<T>): Traversal.BranchingQueueNext<NetworkTraceStep<T>> {
        return Traversal.BranchingQueueNext { item, context, queueItem, queueBranch ->
            queueNextStepsBranching(nextTraceSteps(isInService, item, context, computeNextT), queueItem, queueBranch)
        }
    }

    private fun <T> queueNextStepsBranching(
        nextSteps: List<NetworkTraceStep<T>>,
        queueItem: (NetworkTraceStep<T>) -> Boolean,
        queueBranch: (NetworkTraceStep<T>) -> Boolean,
    ) {
        when {
            nextSteps.size == 1 -> queueItem(nextSteps[0])
            nextSteps.size > 1 -> nextSteps.forEach { queueBranch(it) }
        }
    }

    private fun <T> nextTraceSteps(
        isInService: CheckInService,
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeData: ComputeData<T>,
    ): Sequence<NetworkTraceStep<T>> {
        val nextNumTerminalSteps = currentStep.nextNumTerminalSteps()
        val nextNumEquipmentSteps = currentStep.nextNumEquipmentSteps()
        return nextStepPaths(isInService, currentStep.path).map {
            NetworkTraceStep(it, nextNumTerminalSteps, nextNumEquipmentSteps, computeData.computeNext(currentStep, currentContext, it))
        }
    }

    private fun <T> nextTraceSteps(
        isInService: CheckInService,
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeDataWithPaths<T>,
    ): List<NetworkTraceStep<T>> {
        val nextNumTerminalSteps = currentStep.nextNumTerminalSteps()
        val nextNumEquipmentSteps = currentStep.nextNumEquipmentSteps()
        val nextPaths = nextStepPaths(isInService, currentStep.path).toList()
        return nextPaths.map {
            NetworkTraceStep(it, nextNumTerminalSteps, nextNumEquipmentSteps, computeNextT.computeNext(currentStep, currentContext, it, nextPaths))
        }
    }

    private fun nextStepPaths(isInService: CheckInService, path: Path): Sequence<Path> {
        val nextTerminals = nextTerminals(isInService, path)

        return if (path.nominalPhasePaths.isNotEmpty()) {
            val phasePaths = path.nominalPhasePaths.map { it.to }.toSet()
            nextTerminals
                .map { nextTerminal -> TerminalConnectivityConnected.terminalConnectivity(path.toTerminal, nextTerminal, phasePaths) }
                .filter { it.nominalPhasePaths.isNotEmpty() }
                .map { Path(path.toTerminal, it.toTerminal, it.nominalPhasePaths) }
        } else {
            nextTerminals.map { Path(path.toTerminal, it) }
        }
    }

    private fun nextTerminals(isInService: CheckInService, path: Path): Sequence<Terminal> {
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

        return nextTerminals.filter { terminal -> terminal.conductingEquipment?.let { isInService(it) } == true }
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false

    private fun NetworkTraceStep<*>.nextNumTerminalSteps() = numTerminalSteps + 1
    private fun NetworkTraceStep<*>.nextNumEquipmentSteps() = if (path.tracedInternally) numEquipmentSteps + 1 else numEquipmentSteps


}

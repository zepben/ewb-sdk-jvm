/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.Traversal

internal object NetworkTraceQueueNext {
    fun <T> basic(computeNextT: ComputeNextT<T>): Traversal.QueueNext<NetworkTraceStep<T>> {
        return Traversal.QueueNext { item, context, queueItem ->
            nextTraceSteps(item, context, computeNextT).forEach { queueItem(it) }
        }
    }

    fun <T> basic(computeNextT: ComputeNextTWithPaths<T>): Traversal.QueueNext<NetworkTraceStep<T>> {
        return Traversal.QueueNext { item, context, queueItem ->
            nextTraceSteps(item, context, computeNextT).forEach { queueItem(it) }
        }
    }

    fun <T> branching(computeNextT: ComputeNextT<T>): Traversal.BranchingQueueNext<NetworkTraceStep<T>> {
        return Traversal.BranchingQueueNext { item, context, queueItem, queueBranch ->
            queueNextStepsBranching(nextTraceSteps(item, context, computeNextT).toList(), queueItem, queueBranch)
        }
    }

    fun <T> branching(computeNextT: ComputeNextTWithPaths<T>): Traversal.BranchingQueueNext<NetworkTraceStep<T>> {
        return Traversal.BranchingQueueNext { item, context, queueItem, queueBranch ->
            queueNextStepsBranching(nextTraceSteps(item, context, computeNextT), queueItem, queueBranch)
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
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeNextT<T>,
    ): Sequence<NetworkTraceStep<T>> {
        return nextStepPaths(currentStep.path).map { NetworkTraceStep(it, computeNextT.compute(currentStep, currentContext, it)) }
    }

    private fun <T> nextTraceSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeNextTWithPaths<T>,
    ): List<NetworkTraceStep<T>> {
        val nextPaths = nextStepPaths(currentStep.path).toList()
        return nextPaths.map { NetworkTraceStep(it, computeNextT.compute(currentStep, currentContext, it, nextPaths)) }
    }

    private fun nextStepPaths(path: StepPath): Sequence<StepPath> {
        val terminals = if (path.tracedInternally) {
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
                // TODO [Review]: Is it safe to assume a single terminal as this is how it is supposed to be modelled?
                // We don't need to step to terminals that are busbars as they would have been queued at the same time this busbar step was.
                path.toTerminal.connectedTerminals().filter { it.conductingEquipment !is BusbarSection }
            } else {
                path.toTerminal.otherTerminals()
            }
        }

        val nextNumEquipmentSteps = if (path.tracedInternally) path.numEquipmentSteps else path.numEquipmentSteps + 1
        return terminals.map { nextTerminal ->
            StepPath(path.toTerminal, nextTerminal, path.numTerminalSteps + 1, nextNumEquipmentSteps)
        }
    }

    private fun Terminal.hasConnectedBusbars(): Boolean =
        connectivityNode?.terminals?.any { it !== this && it.conductingEquipment is BusbarSection } ?: false
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

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
        // Check if we last moved between equipment, or across it.
        val terminals = if (path.tracedInternally) path.toTerminal?.connectedTerminals() else path.toTerminal?.otherTerminals()
        if (terminals == null)
            return emptySequence()

        return terminals.map {
            when (path) {
                is TerminalToTerminalPath -> {
                    TerminalToTerminalPath(
                        path.toTerminal,
                        it,
                        path.numTerminalSteps + 1,
                        if (path.tracedInternally) path.numEquipmentSteps else path.numEquipmentSteps + 1,
                    )
                }
            }
        }
    }
}

/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.Traversal
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

private typealias NetworkTraceQueueNext<T> = Traversal.QueueNext<NetworkTraceStep<T>>
private typealias BranchingNetworkTraceQueueNext<T> = Traversal.BranchingQueueNext<NetworkTraceStep<T>>

// TODO: Should these be fun interfaces? Names could probably be better too.
typealias ComputeNextT<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T
typealias ComputeNextTNextPaths<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath, nextPaths: List<StepPath>) -> T

object Tracing {

    fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toEquipment },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTrace(queue, tracker, computeNextT, null)
    }

    fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toEquipment },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTrace(queue, tracker, null, computeNextT)
    }

    fun connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<Unit>> = NetworkTraceTracker { it.path.toEquipment },
    ): NetworkTrace<Unit> {
        return connectedEquipmentTrace(queue, tracker, { _, _, _ -> }, null)
    }

    private fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toEquipment },
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = NetworkTraceQueueNext { ts, ctx, queueItem ->
            val nextSteps = nextConductingEquipmentSteps(ts, ctx, computeNextT, computeNextTNextPaths)
            nextSteps.forEach { queueItem(it) }
        }

        return NetworkTrace(queueNext, queue, tracker)
    }

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTraceBranching(queueFactory, trackerFactory, branchQueueFactory, computeNextT, null)
    }

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTraceBranching(queueFactory, trackerFactory, branchQueueFactory, null, computeNextT)
    }

    fun connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<Unit>> = { NetworkTraceTracker { it.path.toEquipment } },
    ): NetworkTrace<Unit> {
        return connectedEquipmentTraceBranching(
            queueFactory,
            trackerFactory,
            branchQueueFactory,
            null
        ) { _, _, _, _ -> }
    }

    private fun <T> connectedEquipmentTraceBranching(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        trackerFactory: () -> Tracker<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = BranchingNetworkTraceQueueNext { ts, ctx, queueItem, queueBranch ->
            val nextSteps = nextConductingEquipmentSteps(ts, ctx, computeNextT, computeNextTNextPaths).toList()
            if (nextSteps.size > 1) {
                nextSteps.forEach { queueBranch(it) }
            } else {
                nextSteps.forEach { queueItem(it) }
            }
        }

        return NetworkTrace(queueNext, queueFactory, branchQueueFactory, trackerFactory)
    }

    private fun <T> nextConductingEquipmentSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): Sequence<NetworkTraceStep<T>> {
        val path = currentStep.path
        val nextSteps = path.toEquipment.terminals.asSequence()
            .filter { it != path.toTerminal }
            .flatMap { fromTerminal ->
                fromTerminal.connectedTerminals().map { nextTerminal ->
                    when (path) {
                        is TerminalToTerminalPath -> {
                            TerminalToTerminalPath(
                                fromTerminal,
                                nextTerminal,
                                // TODO: What number makes sense here - you can effectively jump terminals
                                path.numTerminalSteps + 1,
                                path.numEquipmentSteps + 1,
                            )
                        }
                    }
                }
            }

        return nextSteps(currentStep, currentContext, nextSteps, computeNextT, computeNextTNextPaths)
    }

    fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toTerminal },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTrace(queue, tracker, computeNextT, null)
    }

    fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toTerminal },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTrace(queue, tracker, null, computeNextT)
    }

    fun connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<Unit>> = NetworkTraceTracker { it.path.toTerminal },
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(queue, tracker) { _, _, _ -> }
    }

    private fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>>,
        tracker: Tracker<NetworkTraceStep<T>>,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = NetworkTraceQueueNext { ts, ctx, queueItem ->
            val nextSteps = nextTerminalSteps(ts, ctx, computeNextT, computeNextTNextPaths)
            nextSteps.forEach { queueItem(it) }
        }

        return NetworkTrace(queueNext, queue, tracker)
    }

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTraceBranching(queueFactory, trackerFactory, branchQueueFactory, computeNextT, null)
    }

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTraceBranching(queueFactory, trackerFactory, branchQueueFactory, null, computeNextT)
    }

    fun connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<Unit>> = { NetworkTraceTracker { it.path.toTerminal } },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(
            queueFactory,
            branchQueueFactory,
            trackerFactory
        ) { _, _, _, _ -> }
    }

    private fun <T> connectedTerminalTraceBranching(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = BranchingNetworkTraceQueueNext { ts, ctx, queueItem, queueBranch ->
            val nextSteps = nextTerminalSteps(ts, ctx, computeNextT, computeNextTNextPaths).toList()
            if (nextSteps.size > 1) {
                nextSteps.forEach { queueBranch(it) }
            } else {
                nextSteps.forEach { queueItem(it) }
            }
        }

        return NetworkTrace(queueNext, queueFactory, branchQueueFactory, trackerFactory)
    }

    private fun <T> nextTerminalSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): Sequence<NetworkTraceStep<T>> {
        val path = currentStep.path
        // Check if we last moved between equipment, or across it.
        val terminals = if (path.tracedInternally) path.toTerminal?.connectedTerminals() else path.toTerminal?.otherTerminals()
        val nextSteps = (terminals ?: emptySequence()).map {
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

        return nextSteps(currentStep, currentContext, nextSteps, computeNextT, computeNextTNextPaths)
    }

    private fun <T> nextSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        nextSteps: Sequence<StepPath>,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): Sequence<NetworkTraceStep<T>> {
        return when {
            computeNextT != null -> {
                nextSteps.map { NetworkTraceStep(it, computeNextT(currentStep, currentContext, it)) }
            }

            computeNextTNextPaths != null -> {
                val nextStepsList = nextSteps.toList()
                nextSteps.map { NetworkTraceStep(it, computeNextTNextPaths(currentStep, currentContext, it, nextStepsList)) }
            }

            else -> throw IllegalArgumentException("INTERNAL ERROR: computeNextT or computeNextTNextPaths must not be null")
        }
    }
}

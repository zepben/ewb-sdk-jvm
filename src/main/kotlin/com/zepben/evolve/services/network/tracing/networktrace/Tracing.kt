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
// TODO: Should these be fun interfaces? Names could probably be better too.
typealias ComputeNextT<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T
typealias ComputeNextTNextPaths<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath, nextPaths: List<StepPath>) -> T

object Tracing {

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        branching: Boolean = false,
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTrace(queueFactory, trackerFactory, branching, computeNextT, null)
    }

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        branching: Boolean = false,
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedEquipmentTrace(queueFactory, trackerFactory, branching, null, computeNextT)
    }

    private fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        branching: Boolean = false,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = if (!branching) {
            NetworkTraceQueueNext<T> { ts, ctx, queueItem, queueBranch ->
                val nextSteps = nextConductingEquipmentSteps(ts, ctx, computeNextT, computeNextTNextPaths)
                nextSteps.forEach { queueItem(it) }
            }
        } else {
            NetworkTraceQueueNext<T> { ts, ctx, queueItem, queueBranch ->
                val nextSteps = nextConductingEquipmentSteps(ts, ctx, computeNextT, computeNextTNextPaths).toList()
                if (nextSteps.size > 1) {
                    nextSteps.forEach { queueBranch(it) }
                } else {
                    nextSteps.forEach { queueItem(it) }
                }
            }
        }

        return NetworkTrace(queueNext, queueFactory, { BasicQueue.breadthFirst() }, trackerFactory)
    }

    fun connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<Unit>> = { NetworkTraceTracker { it.path.toEquipment } },
        branching: Boolean = false,
    ): NetworkTrace<Unit> =
        connectedEquipmentTrace(queueFactory, trackerFactory, branching) { _, _, _ -> }

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
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        branching: Boolean = false,
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTrace(queueFactory, trackerFactory, branching, computeNextT, null)
    }

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        branching: Boolean = false,
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return connectedTerminalTrace(queueFactory, trackerFactory, branching, null, computeNextT)
    }

    private fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        branching: Boolean = false,
        computeNextT: ComputeNextT<T>?,
        computeNextTNextPaths: ComputeNextTNextPaths<T>?,
    ): NetworkTrace<T> {
        val queueNext = if (!branching) {
            NetworkTraceQueueNext<T> { ts, ctx, queueItem, _ ->
                val nextSteps = nextTerminalSteps(ts, ctx, computeNextT, computeNextTNextPaths)
                nextSteps.forEach { queueItem(it) }
            }
        } else {
            NetworkTraceQueueNext<T> { ts, ctx, queueItem, queueBranch ->
                val nextSteps = nextTerminalSteps(ts, ctx, computeNextT, computeNextTNextPaths).toList()
                if (nextSteps.size > 1) {
                    nextSteps.forEach { queueBranch(it) }
                } else {
                    nextSteps.forEach { queueItem(it) }
                }
            }
        }

        return NetworkTrace(queueNext, queueFactory, { BasicQueue.breadthFirst() }, trackerFactory)
    }

    fun connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<Unit>> = { NetworkTraceTracker { it.path.toTerminal } },
        branching: Boolean = false,
    ): NetworkTrace<Unit> =
        connectedTerminalTrace(queueFactory, trackerFactory, branching) { _, _, _ -> }

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
                nextSteps.map {
                    NetworkTraceStep(it, computeNextT(currentStep, currentContext, it))
                }
            }

            computeNextTNextPaths != null -> {
                val nextStepsList = nextSteps.toList()
                nextSteps.map {
                    NetworkTraceStep(it, computeNextTNextPaths(currentStep, currentContext, it, nextStepsList))
                }
            }

            else -> throw IllegalArgumentException("INTERNAL ERROR: computeNextT or computeNextTNextPaths must not be null")
        }
    }
}

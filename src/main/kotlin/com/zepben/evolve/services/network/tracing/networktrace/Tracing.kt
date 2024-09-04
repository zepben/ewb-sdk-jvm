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

typealias NetworkTraceQueueNext<T> = Traversal.QueueNext<NetworkTraceStep<T>>

object Tracing {

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toEquipment } },
        branching: Boolean = false,
        computeNextT: (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T,
    ): NetworkTrace<T> {
        val queueNext = NetworkTraceQueueNext { ts, ctx, queueItem, queueBranch ->
            val path = ts.path
            val nextSteps = path.toEquipment.terminals.asSequence()
                .filter { it != path.toTerminal }
                .flatMap { fromTerminal ->
                    fromTerminal.connectedTerminals().map { nextTerminal ->
                        val nextPath = when (path) {
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

                        NetworkTraceStep(nextPath, computeNextT(ts, ctx, nextPath))
                    }
                }
                .toList()

            if (branching && nextSteps.size > 1) {
                nextSteps.forEach {
                    queueBranch(it)
                }
            } else {
                nextSteps.forEach {
                    queueItem(it)
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

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        trackerFactory: () -> Tracker<NetworkTraceStep<T>> = { NetworkTraceTracker { it.path.toTerminal } },
        branching: Boolean = false,
        computeNextT: (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T,
    ): NetworkTrace<T> {
        val queueNext = NetworkTraceQueueNext { ts, ctx, queueItem, queueBranch ->
            // Check if we last moved between equipment, or across it.
            // TODO: Should we handle the TerminalToTerminalTraceStep cast here?
            val path = ts.path
            val terminals = if (path.tracedInternally) path.toTerminal?.connectedTerminals() else path.toTerminal?.otherTerminals()
            val nextSteps = (terminals ?: emptySequence()).map {
                val nextPath = when (path) {
                    is TerminalToTerminalPath -> {
                        TerminalToTerminalPath(
                            path.toTerminal,
                            it,
                            path.numTerminalSteps + 1,
                            if (path.tracedInternally) path.numEquipmentSteps else path.numEquipmentSteps + 1,
                        )
                    }
                }

                NetworkTraceStep(nextPath, computeNextT(ts, ctx, nextPath))
            }.toList()

            if (branching && nextSteps.size > 1) {
                nextSteps.forEach {
                    queueBranch(it)
                }
            } else {
                nextSteps.forEach {
                    queueItem(it)
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

}

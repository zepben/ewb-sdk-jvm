/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

object Tracing {

    fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toEquipment },
        computeNextT: (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T,
    ): NetworkTrace<T> {
        val queueNext = NetworkTrace.QueueNext { ts, ctx, queueItem, t ->
            val path = ts.path
            path.toEquipment.terminals
                .filter { it != path.toTerminal }
                .flatMap { it.connectedTerminals() }
                .map {
                    val nextPath = when (path) {
                        is TerminalToTerminalPath -> {
                            TerminalToTerminalPath(
                                path.toTerminal,
                                it,
                                path.numTerminalSteps + 1,
                                path.numEquipmentSteps + 1,
                            )
                        }
                    }

                    NetworkTraceStep(nextPath, computeNextT(ts, ctx, nextPath))
                }
                .forEach { queueItem(it) }
        }

        return NetworkTrace(queueNext, queue, tracker)
    }

    fun connectedEquipmentTrace(queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst()): NetworkTrace<Unit> =
        connectedEquipmentTrace(queue) { _, _, _ -> }

    fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        tracker: Tracker<NetworkTraceStep<T>> = NetworkTraceTracker { it.path.toTerminal },
        computeNextT: (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T,
    ): NetworkTrace<T> {
        val queueNext = NetworkTrace.QueueNext { ts, ctx, queueItem, t ->
            // Check if we last moved between equipment, or across it.
            // TODO: Should we handle the TerminalToTerminalTraceStep cast here?
            val path = ts.path
            val terminals = if (path.tracedInternally) path.toTerminal?.connectedTerminals() else path.toTerminal?.otherTerminals()
            terminals?.forEach {
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

                val nextStep = NetworkTraceStep(nextPath, computeNextT(ts, ctx, nextPath))
                queueItem(nextStep)
            }
        }

        return NetworkTrace(queueNext, queue, tracker)
    }

    fun connectedTerminalTrace(queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst()): NetworkTrace<Unit> =
        connectedTerminalTrace(queue) { _, _, _ -> }

}

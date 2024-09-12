/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

// TODO: Should these be fun interfaces? Names could probably be better too.
typealias ComputeNextT<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath) -> T
typealias ComputeNextTNextPaths<T> = (currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath, nextPaths: List<StepPath>) -> T

object Tracing {

    @JvmStatic
    @JvmOverloads
    fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queue, true, computeNextT)
    }

    fun <T> connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queue, true, computeNextT)
    }

    fun connectedEquipmentTrace(
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return connectedEquipmentTrace(queue) { _, _, _ -> }
    }

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    fun <T> connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    fun connectedEquipmentTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedEquipmentTrace(queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queue, false, computeNextT)
    }

    fun <T> connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queue, false, computeNextT)
    }

    fun connectedTerminalTrace(
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(queue) { _, _, _ -> }
    }

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    fun <T> connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextTNextPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    fun connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    fun normalDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)
    fun currentDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)

}

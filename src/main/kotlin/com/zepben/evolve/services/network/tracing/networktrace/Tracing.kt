/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

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
        computeNextT: ComputeNextTWithPaths<T>,
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
        computeNextT: ComputeNextTWithPaths<T>,
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
        computeNextT: ComputeNextTWithPaths<T>,
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
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    fun connectedTerminalTrace(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    // TODO Create some sort of network state test/selector/whatever grouping object that points to the appropriate network state tests/selectors/whateverers (NetworkStateOperators?)
    fun normalDownstreamTree(): DownstreamTree = DownstreamTree(DirectionSelector.NORMAL_DIRECTION)
    fun currentDownstreamTree(): DownstreamTree = DownstreamTree(DirectionSelector.CURRENT_DIRECTION)

    fun normalSetDirection(): SetDirection = SetDirection(NetworkStateOperators.NORMAL)
    fun currentSetDirection(): SetDirection = SetDirection(NetworkStateOperators.CURRENT)

    /**
     * Apply feeder directions from all feeder head terminals in the network.
     *
     * @param network The network in which to apply feeder directions.
     */
    fun applyFeederDirections(network: NetworkService) {
        val normal = normalSetDirection()
        val current = currentSetDirection()

        network.sequenceOf<Feeder>()
            .mapNotNull { it.normalHeadTerminal }
            .forEach {
                val feederHead = requireNotNull(it.conductingEquipment) { "head terminals require conducting equipment to apply feeder directions" }

                if (!normal.networkStateOperators.openTest.isOpen(feederHead, null))
                    normal.run(it)

                if (!current.networkStateOperators.openTest.isOpen(feederHead, null))
                    current.run(it)
            }
    }

}

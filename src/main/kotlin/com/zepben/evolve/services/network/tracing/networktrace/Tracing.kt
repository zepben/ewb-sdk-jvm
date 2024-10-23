/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.RemoveDirection
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.RemovePhases
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

object Tracing {

    @JvmStatic
    @JvmOverloads
    fun <T> connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, true, computeNextT)
    }

    fun <T> connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, true, computeNextT)
    }

    fun connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return connectedEquipmentTrace(networkStateOperators, queue) { _, _, _ -> }
    }

    fun <T> connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    fun <T> connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    fun connectedEquipmentTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedEquipmentTrace(networkStateOperators, queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    // TODO [Review]: Remove "TerminalTrace" and "connectedEquipmentTrace" and just have "networkTrace" which takes the onlyActionEquipment flag
    fun <T> connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, false, computeNextT)
    }

    fun <T> connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = BasicQueue.depthFirst(),
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, false, computeNextT)
    }

    fun connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = BasicQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(networkStateOperators, queue) { _, _, _ -> }
    }

    fun <T> connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    fun <T> connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { BasicQueue.breadthFirst() },
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    fun connectedTerminalTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { BasicQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { BasicQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return connectedTerminalTrace(networkStateOperators, queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    // TODO Create some sort of network state test/selector/whatever grouping object that points to the appropriate network state tests/selectors/whateverers (NetworkStateOperators?)
    fun normalDownstreamTree(): DownstreamTree = DownstreamTree(NetworkStateOperators.NORMAL)
    fun currentDownstreamTree(): DownstreamTree = DownstreamTree(NetworkStateOperators.CURRENT)

    fun normalSetDirection(): SetDirection = SetDirection(NetworkStateOperators.NORMAL)
    fun currentSetDirection(): SetDirection = SetDirection(NetworkStateOperators.CURRENT)

    fun normalRemoveDirection(): RemoveDirection = RemoveDirection(NetworkStateOperators.NORMAL)
    fun currentRemoveDirection(): RemoveDirection = RemoveDirection(NetworkStateOperators.CURRENT)

    fun normalAssignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders(NetworkStateOperators.NORMAL)
    fun currentAssignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders(NetworkStateOperators.CURRENT)

    fun normalAssignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders(NetworkStateOperators.NORMAL)
    fun currentAssignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders(NetworkStateOperators.CURRENT)

    fun normalSetPhases(): SetPhases = SetPhases(NetworkStateOperators.NORMAL)
    fun currentSetPhases(): SetPhases = SetPhases(NetworkStateOperators.CURRENT)

    fun normalPhaseInferrer(): PhaseInferrer = PhaseInferrer(NetworkStateOperators.NORMAL)
    fun currentPhaseInferrer(): PhaseInferrer = PhaseInferrer(NetworkStateOperators.CURRENT)

    fun normalRemovePhases(): RemovePhases = RemovePhases(NetworkStateOperators.NORMAL)
    fun currentRemovePhases(): RemovePhases = RemovePhases(NetworkStateOperators.CURRENT)

}

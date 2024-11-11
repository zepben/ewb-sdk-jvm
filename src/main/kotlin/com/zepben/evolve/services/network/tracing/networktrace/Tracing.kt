/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.FindSwerEquipment
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.RemoveDirection
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.RemovePhases
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.evolve.services.network.tracing.traversal.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

object Tracing {


    // TODO [Review]: Remove "equipmentNetworkTrace" and "terminalNetworkTrace" and just have "networkTrace" which takes the onlyActionEquipment flag?
    //                Use enum for action step type instead? Options could be `ALL_STEPS`, `EQUIPMENT_FIRST_VISIT`, `EXTERNAL_ONLY`
    //                Allow passing in a custom `canActionEquipment` implementation and we have prebuilt implementations representing the above enums?
    @JvmStatic
    fun <T> equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, true, computeNextT)
    }

    @JvmStatic
    fun <T> equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, true, computeNextT)
    }

    @JvmStatic
    fun equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = TraversalQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return equipmentNetworkTrace(networkStateOperators, queue) { _, _, _ -> }
    }

    @JvmStatic
    fun <T> equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    @JvmStatic
    fun <T> equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, true, null, computeNextT)
    }

    @JvmStatic
    fun equipmentNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { TraversalQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return equipmentNetworkTrace(networkStateOperators, queueFactory, branchQueueFactory) { _, _, _ -> }
    }

    @JvmStatic
    fun <T> terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, false, computeNextT)
    }

    @JvmStatic
    fun <T> terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, false, computeNextT)
    }

    @JvmStatic
    fun terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = TraversalQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return terminalNetworkTrace(networkStateOperators, queue) { _, _, _ -> }
    }

    @JvmStatic
    fun <T> terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    @JvmStatic
    fun <T> terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, false, null, computeNextT)
    }

    @JvmStatic
    fun terminalNetworkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { TraversalQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return terminalNetworkTrace(networkStateOperators, queueFactory, branchQueueFactory) { _, _, _, _ -> }
    }

    // TODO [Review]: Replace normal/downstream variants and have a single variant that takes NetworkStateOperators, defaulting to NORMAL
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

    fun normalFindSwerEquipment(): FindSwerEquipment = FindSwerEquipment(NetworkStateOperators.NORMAL)
    fun currentFindSwerEquipment(): FindSwerEquipment = FindSwerEquipment(NetworkStateOperators.CURRENT)

}

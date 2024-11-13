/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
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
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.RemovePhases
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.evolve.services.network.tracing.traversal.TraversalQueue
import com.zepben.evolve.services.network.tracing.tree.DownstreamTree

object Tracing {

    @JvmStatic
    fun <T> networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, actionStepType, computeNextT)
    }

    @JvmStatic
    fun <T> networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, actionStepType, computeNextT)
    }

    @JvmStatic
    fun networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = TraversalQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return networkTrace(networkStateOperators, actionStepType, queue) { _, _, _ -> }
    }

    @JvmStatic
    fun <T> networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextT<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, actionStepType, null, computeNextT)
    }

    @JvmStatic
    fun <T> networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeNextT: ComputeNextTWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, actionStepType, null, computeNextT)
    }

    @JvmStatic
    fun networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { TraversalQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return networkTraceBranching(networkStateOperators, actionStepType, queueFactory, branchQueueFactory) { _, _, _ -> }
    }

    // TODO [Review]: Replace normal/downstream variants and have a single variant that takes NetworkStateOperators, defaulting to NORMAL
    // TODO Create some sort of network state test/selector/whatever grouping object that points to the appropriate network state tests/selectors/whateverers (NetworkStateOperators?)
    fun downstreamTree(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): DownstreamTree = DownstreamTree(stateOperators)

    fun setDirection(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): SetDirection = SetDirection(stateOperators)

    fun removeDirection(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): RemoveDirection = RemoveDirection(stateOperators)

    fun assignEquipmentToFeeders(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): AssignToFeeders = AssignToFeeders(stateOperators)

    fun assignEquipmentToLvFeeders(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): AssignToLvFeeders = AssignToLvFeeders(stateOperators)

    fun setPhases(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): SetPhases = SetPhases(stateOperators)

    fun phaseInferrer(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): PhaseInferrer = PhaseInferrer(stateOperators)

    fun removePhases(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): RemovePhases = RemovePhases(stateOperators)

    fun findSwerEquipment(stateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): FindSwerEquipment = FindSwerEquipment(stateOperators)

}

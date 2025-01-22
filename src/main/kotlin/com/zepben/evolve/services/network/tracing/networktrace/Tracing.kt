/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.annotations.ZepbenExperimental
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.FindSwerEquipment
import com.zepben.evolve.services.network.tracing.feeder.AssignToFeeders
import com.zepben.evolve.services.network.tracing.feeder.AssignToLvFeeders
import com.zepben.evolve.services.network.tracing.feeder.ClearDirection
import com.zepben.evolve.services.network.tracing.feeder.SetDirection
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.phases.PhaseInferrer
import com.zepben.evolve.services.network.tracing.phases.RemovePhases
import com.zepben.evolve.services.network.tracing.phases.SetPhases
import com.zepben.evolve.services.network.tracing.traversal.TraversalQueue

/**
 * Provides factory functions to easily create instances of [NetworkTrace] and other tracing based utility classes.
 */
object Tracing {

    /**
     * Creates a [NetworkTrace] that computes contextual data for every step.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queue The traversal queue the trace is backed by. Defaults to a depth first queue.
     * @param computeData The computer that provides the [NetworkTraceStep.data] contextual step data for each step in the trace.
     *
     * @return a new NetworkTrace
     */
    @JvmStatic
    @JvmOverloads
    fun <T> networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeData: ComputeData<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, actionStepType, computeData)
    }

    /**
     * Creates a [NetworkTrace] that computes contextual data for every step.
     *
     * This variant use [ComputeDataWithPaths] that allows seeing all the next paths from a current step, which may be needed
     * when computing data for each step. If you do not need all the paths, use the factory variant that takes a [ComputeData]
     * as it is more efficient.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queue The traversal queue the trace is backed by. Defaults to a depth first queue.
     * @param computeData The computer that provides the [NetworkTraceStep.data] contextual step data for each step in the trace.
     *
     * @return a new NetworkTrace
     */
    @ZepbenExperimental
    @JvmStatic
    @JvmOverloads
    fun <T> networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<T>> = TraversalQueue.depthFirst(),
        computeData: ComputeDataWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queue, actionStepType, computeData)
    }

    /**
     * Creates a new [NetworkTrace].
     *
     * This variant relies on no contextual step data, thus [NetworkTraceStep.data] will always be Unit.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queue The traversal queue the trace is backed by. Defaults to a depth first queue.
     *
     * @return a new NetworkTrace
     */
    @JvmStatic
    @JvmOverloads
    fun networkTrace(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queue: TraversalQueue<NetworkTraceStep<Unit>> = TraversalQueue.depthFirst(),
    ): NetworkTrace<Unit> {
        return networkTrace(networkStateOperators, actionStepType, queue) { _, _, _ -> }
    }

    /**
     * Creates a branching [NetworkTrace] that computes contextual data for every step. A new 'branch' will be created for each terminal
     * where the current terminal in the trace will step to two or more terminals.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue steps. Defaults to a factory the creates depth first queues.
     * @param branchQueueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue branches. Defaults to a factory the creates breadth first queues.
     * @param computeData The computer that provides the [NetworkTraceStep.data] contextual step data for each step in the trace.
     *
     * @return a new NetworkTrace
     */
    @JvmStatic
    @JvmOverloads
    fun <T> networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeData: ComputeData<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, actionStepType, null, computeData)
    }

    /**
     * Creates a branching [NetworkTrace] that computes contextual data for every step. A new 'branch' will be created for each terminal
     * where the current terminal in the trace will step to two or more terminals.
     *
     * This variant use [ComputeDataWithPaths] that allows seeing all the next paths from a current step, which may be needed
     * when computing data for each step. If you do not need all the paths, use the factory variant that takes a [ComputeData]
     * as it is more efficient.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue steps. Defaults to a factory the creates depth first queues.
     * @param branchQueueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue branches. Defaults to a factory the creates breadth first queues.

     * @param computeData The computer that provides the [NetworkTraceStep.data] contextual step data for each step in the trace.
     *
     * @return a new NetworkTrace
     */
    @JvmStatic
    @JvmOverloads
    @ZepbenExperimental
    fun <T> networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>> = { TraversalQueue.breadthFirst() },
        computeData: ComputeDataWithPaths<T>,
    ): NetworkTrace<T> {
        return NetworkTrace(networkStateOperators, queueFactory, branchQueueFactory, actionStepType, null, computeData)
    }

    /**
     * Creates a branching [NetworkTrace]. A new 'branch' will be created for each terminal
     * where the current terminal in the trace will step to two or more terminals.
     *
     * This variant relies on no contextual step data, thus [NetworkTraceStep.data] will always be Unit.
     *
     * @param networkStateOperators The state operators to make the NetworkTrace state aware. Defaults to [NetworkStateOperators.NORMAL].
     * @param actionStepType The action step type to be applied when the trace steps. Defaults to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT].
     * @param queueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue steps. Defaults to a factory the creates depth first queues.
     * @param branchQueueFactory A factory that will produce [TraversalQueue]s used by each branch in the trace to queue branches. Defaults to a factory the creates breadth first queues.
     *
     * @return a new NetworkTrace
     */
    @JvmStatic
    @JvmOverloads
    fun networkTraceBranching(
        networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL,
        actionStepType: NetworkTraceActionType = FIRST_STEP_ON_EQUIPMENT,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<Unit>> = { TraversalQueue.depthFirst() },
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<Unit>> = { TraversalQueue.breadthFirst() },
    ): NetworkTrace<Unit> {
        return networkTraceBranching(networkStateOperators, actionStepType, queueFactory, branchQueueFactory) { _, _, _ -> }
    }

    /**
     * Returns a class that can be used to set feeder directions of items in a [NetworkService].
     * @return a new SetDirection instance.
     */
    @JvmStatic
    fun setDirection(): SetDirection = SetDirection()

    /**
     * Returns a class that can be used to clear feeder directions from items in a [NetworkService].
     * @return a new ClearDirection instance.
     */
    @JvmStatic
    fun clearDirection(): ClearDirection = ClearDirection()

    /**
     * Returns a class that can be used to assign [Equipment] to [Feeder]s of items in a [NetworkService].
     * @return a new AssignToFeeders instance.
     */
    @JvmStatic
    fun assignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders()

    /**
     * Returns a class that can be used to assign [Equipment] to [LvFeeder]s of items in a [NetworkService].
     * @return a new AssignToLvFeeders instance.
     */
    @JvmStatic
    fun assignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders()

    /**
     * Returns a class that can be used to assign traced phases to terminals in a [NetworkService].
     * @return a new SetPhases instance.
     */
    @JvmStatic
    fun setPhases(): SetPhases = SetPhases()

    /**
     * Returns a class that can be used to removed traced phases from terminals in a [NetworkService].
     * @return a new RemovePhases instance.
     */
    @JvmStatic
    fun removePhases(): RemovePhases = RemovePhases()

    /**
     * Returns a class that can be used to attempt to infer traced phases of terminals in a [NetworkService] when phasing information is unreliable.
     * @return a new PhaseInferrer instance.
     */
    @JvmStatic
    fun phaseInferrer(): PhaseInferrer = PhaseInferrer()

    /**
     * Returns a class that can be used to find SWER equipment from [Equipment] in a [NetworkService].
     * @return a new FindSwerEquipment instance.
     */
    @JvmStatic
    fun findSwerEquipment(): FindSwerEquipment = FindSwerEquipment()

}

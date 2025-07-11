/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.traversal.QueueCondition
import com.zepben.ewb.services.network.tracing.traversal.StepContext

/**
 * A special queue condition implementation that allows only checking `shouldQueue` when a [NetworkTraceStep] matches a given
 * [NetworkTraceStep.Type]. When [stepType] is:
 * *[NetworkTraceStep.Type.ALL]: [shouldQueue] will be called for every step.
 * *[NetworkTraceStep.Type.INTERNAL]: [shouldQueue] will be called only when [NetworkTraceStep.type] is [NetworkTraceStep.Type.INTERNAL].
 * *[NetworkTraceStep.Type.EXTERNAL]: [shouldQueue] will be called only when [NetworkTraceStep.type] is [NetworkTraceStep.Type.EXTERNAL].
 *
 * If the step does not match the given step type, `true` will always be returned.
 *
 * @property stepType The step type to match to check `shouldQueue`.
 */
abstract class NetworkTraceQueueCondition<T>(val stepType: NetworkTraceStep.Type) : QueueCondition<NetworkTraceStep<T>> {

    private val shouldQueueFunc = when (stepType) {
        NetworkTraceStep.Type.ALL -> ::shouldQueueMatchedStep
        NetworkTraceStep.Type.INTERNAL -> ::shouldQueueInternalStep
        NetworkTraceStep.Type.EXTERNAL -> ::shouldQueueExternalStep
    }

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean =
        shouldQueueFunc(nextItem, nextContext, currentItem, currentContext)

    /**
     * The logic you would normally put in [shouldQueue]. However, this will only be called when a step matches the [stepType].
     */
    abstract fun shouldQueueMatchedStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean

    private fun shouldQueueInternalStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean =
        if (nextItem.type == NetworkTraceStep.Type.INTERNAL) shouldQueueMatchedStep(nextItem, nextContext, currentItem, currentContext) else true

    private fun shouldQueueExternalStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean =
        if (nextItem.type == NetworkTraceStep.Type.EXTERNAL) shouldQueueMatchedStep(nextItem, nextContext, currentItem, currentContext) else true

    companion object {
        /**
         * Creates A [NetworkTraceQueueCondition] that delegates to the given [condition] when matching the given [stepType].
         * The [condition] is already a [NetworkTraceQueueCondition] this will override the [stepType] of that instance.
         *
         * @param stepType The step type to match to check the queue condition.
         * @param condition The queue conditions to delegate to when matching the step type.
         * @return A new queue condition that only checks the condition when matching the given step type.
         */
        @JvmStatic
        fun <T> delegateTo(stepType: NetworkTraceStep.Type, condition: QueueCondition<NetworkTraceStep<T>>): NetworkTraceQueueCondition<T> =
            DelegatedNetworkTraceQueueCondition(stepType, condition)
    }
}

private class DelegatedNetworkTraceQueueCondition<T>(
    stepType: NetworkTraceStep.Type,
    val delegate: QueueCondition<NetworkTraceStep<T>>
) : NetworkTraceQueueCondition<T>(stepType) {
    override fun shouldQueueMatchedStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean = delegate.shouldQueue(nextItem, nextContext, currentItem, currentContext)

    override fun shouldQueueStartItem(item: NetworkTraceStep<T>): Boolean = delegate.shouldQueueStartItem(item)
}
